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
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.util.DateFormatter

class WelcomeFragment : android.support.v4.app.Fragment(), us.handstand.kartwheel.activity.TicketActivity.TicketFragment, android.text.TextWatcher, android.widget.TextView.OnEditorActionListener {

    lateinit internal var birth: android.widget.EditText
    lateinit internal var cell: android.widget.EditText
    lateinit internal var email: android.widget.EditText
    lateinit internal var firstName: android.widget.EditText
    lateinit internal var lastName: android.widget.EditText
    lateinit internal var nickname: android.widget.EditText
    lateinit private var button: android.support.v7.widget.AppCompatButton

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        val fragmentView = inflater!!.inflate(us.handstand.kartwheel.R.layout.fragment_welcome, container, false) as android.view.ViewGroup
        birth = us.handstand.kartwheel.layout.ViewUtil.findView(fragmentView, R.id.birth)
        cell = us.handstand.kartwheel.layout.ViewUtil.findView(fragmentView, R.id.cell)
        email = us.handstand.kartwheel.layout.ViewUtil.findView(fragmentView, R.id.email)
        firstName = us.handstand.kartwheel.layout.ViewUtil.findView(fragmentView, R.id.first_name)
        lastName = us.handstand.kartwheel.layout.ViewUtil.findView(fragmentView, R.id.last_name)
        nickname = us.handstand.kartwheel.layout.ViewUtil.findView(fragmentView, R.id.nickname)
        birth.addTextChangedListener(this)
        cell.addTextChangedListener(this)
        email.addTextChangedListener(this)
        firstName.addTextChangedListener(this)
        lastName.addTextChangedListener(this)
        nickname.addTextChangedListener(this)
        nickname.setOnEditorActionListener(this)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: android.os.Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button = us.handstand.kartwheel.layout.ViewUtil.findView(activity, R.id.button)
    }

    override fun getTitleResId(): Int {
        return us.handstand.kartwheel.R.string.welcome
    }

    override fun getAdvanceButtonTextResId(): Int {
        return us.handstand.kartwheel.R.string.im_ready
    }

    override fun getAdvanceButtonColor(): Int {
        return if (isAdvanceButtonEnabled()) us.handstand.kartwheel.R.color.blue else super.getAdvanceButtonColor()
    }

    override fun isAdvanceButtonEnabled(): Boolean {
        return isValidInput
    }

    override fun canAdvanceToNextStep(): Boolean {
        ticketController.user = ticketController.user!!.construct(us.handstand.kartwheel.util.DateFormatter.getFromUserInput(birth.text.toString()),
                cell.text.toString(),
                email.text.toString(),
                firstName.text.toString(),
                lastName.text.toString(),
                nickname.text.toString())
        return super.canAdvanceToNextStep()
    }

    private // TODO: better validation, birth and cell not required
    val isValidInput: Boolean
        get() = !us.handstand.kartwheel.layout.ViewUtil.isEmpty(birth) && !us.handstand.kartwheel.layout.ViewUtil.isEmpty(cell) && !us.handstand.kartwheel.layout.ViewUtil.isEmpty(email)
                && !us.handstand.kartwheel.layout.ViewUtil.isEmpty(firstName) && !us.handstand.kartwheel.layout.ViewUtil.isEmpty(lastName) && !us.handstand.kartwheel.layout.ViewUtil.isEmpty(nickname)


    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (isAdvanceButtonEnabled()) {
            ticketController.onTicketFragmentStateChanged()
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: android.text.Editable) {}

    override fun onEditorAction(v: android.widget.TextView?, actionId: Int, event: android.view.KeyEvent?): Boolean {
        if (actionId == IME_ACTION_GO || event?.action == ACTION_DOWN) {
            activity.findViewById(us.handstand.kartwheel.R.id.button).performClick()
            return true
        }
        return false
    }
}
