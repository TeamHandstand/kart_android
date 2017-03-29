package us.handstand.kartwheel.activity


import android.content.Context
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
import android.view.View
import android.widget.TextView
import android.widget.Toast
import us.handstand.kartwheel.R
import us.handstand.kartwheel.fragment.*
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.*
import us.handstand.kartwheel.network.API

class TicketActivity : AppCompatActivity(), View.OnClickListener {
    interface TicketFragment : View.OnClickListener

    companion object {
        private const val INTENT_EXTRA_FRAGMENT_TYPE = "fragment_type"
        private const val TOS = 0
        private const val CODE_ENTRY = 1
        private const val CRITICAL_INFO = 2
        private const val WELCOME = 3
        private const val ALREADY_CLAIMED = 4
        private const val FORFEIT = 5
        private const val GAME_INFO = 6

        @IntDef(TOS.toLong(), CODE_ENTRY.toLong(), CRITICAL_INFO.toLong(), WELCOME.toLong(),
                ALREADY_CLAIMED.toLong(), FORFEIT.toLong(), GAME_INFO.toLong())
        private annotation class FragmentType

        internal fun getStartIntent(context: Context): Intent {
            val intent = Intent(context, TicketActivity::class.java)
            intent.putExtra(INTENT_EXTRA_FRAGMENT_TYPE, TOS)
            return intent
        }
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
        showFragment(currentFragmentType)
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
                button!!.setText(R.string.lets_go)
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
            CODE_ENTRY -> if (intent.hasExtra(TicketModel.CODE)) {
                API.claimTicket(intent.getStringExtra(TicketModel.CODE), object : API.APICallback<Ticket>() {
                    override fun onSuccess(response: Ticket) {
                        if (response.isClaimed) {
                            API.getRaces(AndroidStorage.get(AndroidStorage.EVENT_ID), object : API.APICallback<List<Race>>() {
                                override fun onSuccess(response: List<Race>) {
                                    showFragment(GAME_INFO)
                                }

                                override fun onFailure(errorCode: Int, errorResponse: String) {
                                    Toast.makeText(this@TicketActivity, "Failed to get races " + errorResponse, Toast.LENGTH_LONG).show()
                                }
                            })
                        } else {
                            runOnUiThread { showFragment(CRITICAL_INFO) }
                        }
                    }

                    override fun onFailure(errorCode: Int, errorResponse: String) {
                        if (errorCode == 409) {
                            runOnUiThread { showFragment(ALREADY_CLAIMED) }
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
                        .putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.contact_us_body, intent.getStringExtra(TicketModel.CODE)))
                startActivity(Intent.createChooser(emailIntent, resources.getString(R.string.contact_us)))
            } else {
                showFragment(CODE_ENTRY)
            }
            CRITICAL_INFO -> if (intent.hasExtra(UserModel.PANCAKEORWAFFLE) && intent.hasExtra(UserModel.CHARMANDERORSQUIRTLE)) {
                showFragment(WELCOME)
            }
            WELCOME -> if (intent.hasExtra(UserModel.BIRTH) &&
                    intent.hasExtra(UserModel.CELL) &&
                    intent.hasExtra(UserModel.EMAIL) &&
                    intent.hasExtra(UserModel.FIRSTNAME) &&
                    intent.hasExtra(UserModel.LASTNAME) &&
                    intent.hasExtra(UserModel.NICKNAME)) {
                val user = User.FACTORY.creator.create(AndroidStorage.get(AndroidStorage.USER_ID), null, // authToken
                        intent.getStringExtra(UserModel.BIRTH),
                        intent.getStringExtra(UserModel.CELL),
                        intent.getStringExtra(UserModel.CHARMANDERORSQUIRTLE),
                        intent.getStringExtra(UserModel.EMAIL),
                        AndroidStorage.get(AndroidStorage.EVENT_ID), null, // facetime count
                        intent.getStringExtra(UserModel.FIRSTNAME), null, // imageUrl
                        intent.getStringExtra(UserModel.LASTNAME), null, // miniGameId
                        intent.getStringExtra(UserModel.NICKNAME),
                        intent.getStringExtra(UserModel.PANCAKEORWAFFLE), null, null, null, null, null, null, null, null// updated at
                )// device token
                // push enabled
                // race id
                // referral type
                // team id
                // total anti miles
                // total distance miles
                API.updateUser(user, object : API.APICallback<User>() {
                    override fun onSuccess(response: User) {
                        runOnUiThread { showFragment(GAME_INFO) }
                    }

                    override fun onFailure(errorCode: Int, errorResponse: String) {
                        Toast.makeText(this@TicketActivity, "Unable to create user: " + errorResponse, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }


}
