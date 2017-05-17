package us.handstand.kartwheel.fragment


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

class WelcomeFragment : Fragment(), TicketActivity.TicketFragment, TextWatcher, TextView.OnEditorActionListener {

    lateinit internal var birth: EditText
    lateinit internal var cell: EditText
    lateinit internal var email: EditText
    lateinit internal var firstName: EditText
    lateinit internal var lastName: EditText
    lateinit internal var nickname: EditText
    lateinit private var button: AppCompatButton

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater!!.inflate(R.layout.fragment_welcome, container, false) as ViewGroup
        birth = ViewUtil.findView(fragmentView, R.id.birth)
        cell = ViewUtil.findView(fragmentView, R.id.cell)
        email = ViewUtil.findView(fragmentView, R.id.email)
        firstName = ViewUtil.findView(fragmentView, R.id.first_name)
        lastName = ViewUtil.findView(fragmentView, R.id.last_name)
        nickname = ViewUtil.findView(fragmentView, R.id.nickname)
        birth.addTextChangedListener(this)
        cell.addTextChangedListener(this)
        email.addTextChangedListener(this)
        firstName.addTextChangedListener(this)
        lastName.addTextChangedListener(this)
        nickname.addTextChangedListener(this)
        nickname.setOnEditorActionListener(this)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button = ViewUtil.findView(activity, R.id.button)
    }

    override fun getTitleResId(): Int {
        return R.string.welcome
    }

    override fun getAdvanceButtonTextResId(): Int {
        return R.string.im_ready
    }

    override fun getAdvanceButtonColor(): Int {
        return if (isAdvanceButtonEnabled()) R.color.blue else super.getAdvanceButtonColor()
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

    private // TODO: better validation, birth and cell not required
    val isValidInput: Boolean
        get() = !ViewUtil.isEmpty(birth) && !ViewUtil.isEmpty(cell) && !ViewUtil.isEmpty(email)
                && !ViewUtil.isEmpty(firstName) && !ViewUtil.isEmpty(lastName) && !ViewUtil.isEmpty(nickname)


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
}
