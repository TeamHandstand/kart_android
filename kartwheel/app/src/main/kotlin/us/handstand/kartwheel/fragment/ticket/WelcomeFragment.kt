package us.handstand.kartwheel.fragment.ticket


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_GO
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import com.google.i18n.phonenumbers.PhoneNumberUtil
import us.handstand.kartwheel.R
import us.handstand.kartwheel.R.*
import us.handstand.kartwheel.activity.TicketActivity.TicketFragment
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.layout.ViewUtil.findView
import us.handstand.kartwheel.layout.ViewUtil.isEmpty
import us.handstand.kartwheel.util.DateFormatter

class WelcomeFragment : Fragment(), TicketFragment, android.text.TextWatcher, OnEditorActionListener {

    lateinit internal var birth: EditText
    lateinit internal var cell: EditText
    lateinit internal var email: EditText
    lateinit internal var firstName: EditText
    lateinit internal var lastName: EditText
    lateinit internal var nickname: EditText
    lateinit private var button: AppCompatButton

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater!!.inflate(layout.fragment_welcome, container, false) as ViewGroup
        birth = findView(fragmentView, R.id.birth)
        cell = findView(fragmentView, R.id.cell)
        email = findView(fragmentView, R.id.email)
        firstName = findView(fragmentView, R.id.first_name)
        lastName = findView(fragmentView, R.id.last_name)
        nickname = findView(fragmentView, R.id.nickname)
        birth.addTextChangedListener(FormatTextWatcher("/", 2))
        cell.addTextChangedListener(FormatTextWatcher("-", 3))
        email.addTextChangedListener(this)
        firstName.addTextChangedListener(this)
        lastName.addTextChangedListener(this)
        nickname.addTextChangedListener(this)
        nickname.setOnEditorActionListener(this)

        // Pre-populate the cells that the user has already filled out
        val user = ticketController.user
        ViewUtil.setIfNotEmpty(birth, if (user?.birth() == null) null else DateFormatter.getString(user.birth()!!))
        ViewUtil.setIfNotEmpty(cell, user?.cell())
        ViewUtil.setIfNotEmpty(email, user?.email())
        ViewUtil.setIfNotEmpty(firstName, user?.firstName())
        ViewUtil.setIfNotEmpty(lastName, user?.lastName())
        ViewUtil.setIfNotEmpty(nickname, user?.nickName())

        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button = findView(activity, R.id.button)
    }

    override fun getTitleResId(): Int {
        return string.welcome
    }

    override fun getAdvanceButtonTextResId(): Int {
        return string.im_ready
    }

    override fun getAdvanceButtonColor(): Int {
        return if (isAdvanceButtonEnabled()) color.blue else super.getAdvanceButtonColor()
    }

    override fun isAdvanceButtonEnabled(): Boolean {
        return isValidInput
    }

    override fun canAdvanceToNextStep(): Boolean {
        ticketController.user = ticketController.user!!.construct(DateFormatter.getFromUserInput(birth.text.toString()),
                cell.text.toString(),
                email.text.toString(),
                firstName.text.toString(),
                lastName.text.toString(),
                nickname.text.toString())
        return super.canAdvanceToNextStep()
    }

    private val isValidInput: Boolean
        get() {
            // TODO: Validate phone number based on location or user supplied input
            val validCellNumber = PhoneNumberUtil.getInstance().isPossibleNumber(cell.text.toString(), "US")
            return DateFormatter.isValid(birth.text.toString()) && validCellNumber && !isEmpty(email)
                    && !isEmpty(firstName) && !isEmpty(lastName) && !isEmpty(nickname)
        }


    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (isAdvanceButtonEnabled()) {
            ticketController.onTicketFragmentStateChanged()
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable) {}

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == IME_ACTION_GO || event?.action == ACTION_DOWN) {
            activity.findViewById(R.id.button).performClick()
            return true
        }
        return false
    }

    inner class FormatTextWatcher(val delimiter: String, val chunkSize: Int) : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            var noDelimiter = s.toString().replace(delimiter, "")
            val formattedStringBuilder = StringBuilder()
            if (noDelimiter.length > chunkSize) {
                val chunk = noDelimiter.substring(0, chunkSize)
                formattedStringBuilder.append(chunk).append(delimiter)
                noDelimiter = noDelimiter.replaceFirst(chunk, "")
            }
            if (noDelimiter.length > chunkSize) {
                val chunk = noDelimiter.substring(0, chunkSize)
                formattedStringBuilder.append(chunk).append(delimiter)
                noDelimiter = noDelimiter.replaceFirst(chunk, "")
            }
            formattedStringBuilder.append(noDelimiter)

            if (s.toString() != formattedStringBuilder.toString()) {
                s.replace(0, s.length, formattedStringBuilder.toString())
                if (isAdvanceButtonEnabled()) {
                    ticketController.onTicketFragmentStateChanged()
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
}
