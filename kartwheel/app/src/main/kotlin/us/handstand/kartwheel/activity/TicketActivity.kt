package us.handstand.kartwheel.activity


import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.IntDef
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.text.TextUtils.isEmpty
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.gson.JsonObject
import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.R
import us.handstand.kartwheel.fragment.*
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.Ticket
import us.handstand.kartwheel.model.User
import us.handstand.kartwheel.network.API

class TicketActivity : AppCompatActivity(), View.OnClickListener {
    interface TicketFragment : View.OnClickListener

    companion object {
        private const val INTENT_EXTRA_FRAGMENT_TYPE = "fragment_type"
        const val TOS = 0
        const val CODE_ENTRY = 1
        const val CRITICAL_INFO = 2
        const val WELCOME = 3
        const val ALREADY_CLAIMED = 4
        const val FORFEIT = 5
        const val GAME_INFO = 6

        @IntDef(TOS.toLong(), CODE_ENTRY.toLong(), CRITICAL_INFO.toLong(), WELCOME.toLong(),
                ALREADY_CLAIMED.toLong(), FORFEIT.toLong(), GAME_INFO.toLong())
        annotation class FragmentType
    }

    internal var title: TextView? = null
    internal var button: AppCompatButton? = null
    internal var additionalButton: AppCompatButton? = null
    private var ticketFragment: TicketFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)
        title = ViewUtil.findView(this, R.id.title_text)
        button = ViewUtil.findView(this, R.id.button)
        additionalButton = ViewUtil.findView(this, R.id.additional_button)
        button!!.setOnClickListener(this)
        additionalButton!!.setOnClickListener(this)
        showFragment(if (isEmpty(Storage.userId)) TOS else GAME_INFO)
    }

    fun showFragment(@FragmentType type: Int) {
        title!!.setText(R.string.app_name)
        additionalButton!!.visibility = View.GONE
        when (type) {
            TOS -> {
                ticketFragment = TOSFragment()
                button!!.setText(R.string.scroll_down)
            }
            CODE_ENTRY -> {
                ticketFragment = CodeEntryFragment()
                setButtonState(R.color.blue, R.string.lets_go, true)
            }
            ALREADY_CLAIMED -> {
                title!!.setText(R.string.already_claimed_title)
                button!!.setText(R.string.try_different_code)
                setAdditionalButtonState(R.color.green, R.string.contact_us, true)
                ticketFragment = AlreadyClaimedFragment()
            }
            CRITICAL_INFO -> {
                title!!.setText(R.string.welcome)
                button!!.setText(R.string.next)
                ticketFragment = CriticalInformationFragment()
            }
            WELCOME -> {
                title!!.setText(R.string.welcome)
                ticketFragment = WelcomeFragment()
                setButtonState(R.color.grey_button_disabled, R.string.im_ready, false)
            }
            GAME_INFO -> {
                title!!.setText(R.string.app_name)
                ticketFragment = GameInfoFragment()
                // TODO: FAB Buttons
            }
            FORFEIT -> {
                title!!.setText(R.string.forfeit_ticket_title)
                ticketFragment = ForfeitFragment()
                setAdditionalButtonState(R.color.blue, R.string.keep_ticket, true)
                setButtonState(R.color.red, R.string.forfeit_ticket, true)
            }
        }
        if (ticketFragment != null) {
            intent.putExtra(INTENT_EXTRA_FRAGMENT_TYPE, type)
            if (!isFinishing && !supportFragmentManager.isDestroyed) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment, ticketFragment as Fragment?).commit()
            }
        }
    }

    private val currentFragmentType: Int
        @FragmentType
        get() {
            @FragmentType val currentFragmentType = intent.getIntExtra(INTENT_EXTRA_FRAGMENT_TYPE, TOS)
            return currentFragmentType
        }

    fun setButtonState(@ColorRes color: Int, @StringRes textRes: Int, enabled: Boolean) {
        ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(resources.getColor(color)))
        button!!.setText(textRes)
        button!!.isEnabled = enabled
    }

    fun setAdditionalButtonState(@ColorRes color: Int, @StringRes textRes: Int, enabled: Boolean) {
        ViewCompat.setBackgroundTintList(additionalButton, ColorStateList.valueOf(resources.getColor(color)))
        additionalButton!!.setText(textRes)
        additionalButton!!.isEnabled = enabled
        if (enabled) {
            additionalButton!!.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View) {
        if (!v.isEnabled || v !is AppCompatButton) {
            return
        }

        ticketFragment!!.onClick(v)
        when (currentFragmentType) {
            TOS -> showFragment(CODE_ENTRY)

            CODE_ENTRY -> if (intent.hasExtra(Ticket.CODE)) {
                (ticketFragment as CodeEntryFragment).setProgressVisibility(View.VISIBLE)
                API.claimTicket(intent.getStringExtra(Ticket.CODE), object : API.APICallback<Ticket>() {
                    override fun onSuccess(response: Ticket) {
                        (ticketFragment as CodeEntryFragment).setProgressVisibility(View.GONE)
                        if (response.isClaimed) {
                            showFragment(GAME_INFO)
                        } else {
                            showFragment(CRITICAL_INFO)
                        }
                    }

                    override fun onFailure(errorCode: Int, errorResponse: String) {
                        (ticketFragment as CodeEntryFragment).setProgressVisibility(View.GONE)
                        if (errorCode == 409) {
                            showFragment(ALREADY_CLAIMED)
                        } else {
                            Toast.makeText(this@TicketActivity, errorResponse, Toast.LENGTH_LONG).show()
                        }
                    }
                })
            }

            ALREADY_CLAIMED -> if (v.getId() == R.id.additional_button) {
                val emailIntent = Intent(Intent.ACTION_SEND)
                        .setType("plain/text")
                        .putExtra(Intent.EXTRA_EMAIL, arrayOf(resources.getString(R.string.support_email)))
                        .putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.contact_us_subject_line))
                        .putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.contact_us_body, intent.getStringExtra(Ticket.CODE)))
                startActivity(Intent.createChooser(emailIntent, resources.getString(R.string.contact_us)))
            } else {
                showFragment(CODE_ENTRY)
            }

            CRITICAL_INFO -> if (intent.hasExtra(User.PANCAKEORWAFFLE) && intent.hasExtra(User.CHARMANDERORSQUIRTLE)) {
                showFragment(WELCOME)
            }

            WELCOME -> if (intent.hasExtra(User.BIRTH) &&
                    intent.hasExtra(User.CELL) &&
                    intent.hasExtra(User.EMAIL) &&
                    intent.hasExtra(User.FIRSTNAME) &&
                    intent.hasExtra(User.LASTNAME) &&
                    intent.hasExtra(User.NICKNAME)) {
                val user = User.FACTORY.creator.create(Storage.userId,
                        null, // authToken
                        intent.getStringExtra(User.BIRTH),
                        intent.getStringExtra(User.CELL),
                        intent.getStringExtra(User.CHARMANDERORSQUIRTLE),
                        intent.getStringExtra(User.EMAIL),
                        Storage.eventId,
                        null, // facetime count
                        intent.getStringExtra(User.FIRSTNAME),
                        null, // imageUrl
                        intent.getStringExtra(User.LASTNAME),
                        null, // miniGameId
                        intent.getStringExtra(User.NICKNAME),
                        intent.getStringExtra(User.PANCAKEORWAFFLE),
                        null, // device token
                        null, // push enabled
                        null, // race id
                        null, // referral type
                        null, // team id
                        null, // total anti miles
                        null, // total distance miles
                        null // updated at
                )
                API.updateUser(user, object : API.APICallback<User>() {
                    override fun onSuccess(response: User) {
                        showFragment(GAME_INFO)
                    }

                    override fun onFailure(errorCode: Int, errorResponse: String) {
                        Toast.makeText(this@TicketActivity, "Unable to create user: " + errorResponse, Toast.LENGTH_SHORT).show()
                    }
                })
            }
            FORFEIT -> {
                if (v.id == R.id.additional_button) {
                    // Keep ticket
                    intent.removeExtra(Ticket.ID)
                    intent.removeExtra(Ticket.CODE)
                    showFragment(GAME_INFO)
                } else if (v.id == R.id.button) {
                    // Forfeit ticket
                    API.forfeitTicket(intent.getStringExtra(Ticket.ID), object : API.APICallback<JsonObject>() {
                        override fun onSuccess(response: JsonObject) {
                            Toast.makeText(this@TicketActivity, "Ticket forfeited", Toast.LENGTH_LONG).show()
                            intent.removeExtra(Ticket.ID)
                            intent.removeExtra(Ticket.CODE)
                            KartWheel.logout()
                            showFragment(CODE_ENTRY)
                        }

                        override fun onFailure(errorCode: Int, errorResponse: String) {
                            Toast.makeText(this@TicketActivity, "Unable to forfeit ticket: " + errorResponse, Toast.LENGTH_LONG).show()
                        }
                    })
                }
            }
        }
    }
}
