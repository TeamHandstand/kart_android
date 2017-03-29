package us.handstand.kartwheel.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.UserModel
import us.handstand.kartwheel.util.DateFormatter

class WelcomeFragment : Fragment(), TicketActivity.TicketFragment, TextWatcher {

    internal var birth: EditText? = null
    internal var cell: EditText? = null
    internal var email: EditText? = null
    internal var firstName: EditText? = null
    internal var lastName: EditText? = null
    internal var nickname: EditText? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater!!.inflate(R.layout.fragment_welcome, container, false) as ViewGroup
        birth = ViewUtil.findView(fragmentView, R.id.birth)
        cell = ViewUtil.findView(fragmentView, R.id.cell)
        email = ViewUtil.findView(fragmentView, R.id.email)
        firstName = ViewUtil.findView(fragmentView, R.id.first_name)
        lastName = ViewUtil.findView(fragmentView, R.id.last_name)
        nickname = ViewUtil.findView(fragmentView, R.id.nickname)
        birth!!.addTextChangedListener(this)
        cell!!.addTextChangedListener(this)
        email!!.addTextChangedListener(this)
        firstName!!.addTextChangedListener(this)
        lastName!!.addTextChangedListener(this)
        nickname!!.addTextChangedListener(this)
        return fragmentView
    }

    private // TODO: better validation, birth and cell not required
    val isValidInput: Boolean
        get() = !ViewUtil.isEmpty(birth) && !ViewUtil.isEmpty(cell) && !ViewUtil.isEmpty(email) &&
                !ViewUtil.isEmpty(firstName) && !ViewUtil.isEmpty(lastName) && !ViewUtil.isEmpty(nickname)

    override fun onClick(v: View) {
        if (isValidInput) {
            activity.intent.putExtra(UserModel.BIRTH, DateFormatter.getString(DateFormatter.get(birth!!.text.toString())))
            activity.intent.putExtra(UserModel.CELL, cell!!.text.toString())
            activity.intent.putExtra(UserModel.EMAIL, email!!.text.toString())
            activity.intent.putExtra(UserModel.FIRSTNAME, firstName!!.text.toString())
            activity.intent.putExtra(UserModel.LASTNAME, lastName!!.text.toString())
            activity.intent.putExtra(UserModel.NICKNAME, nickname!!.text.toString())
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val isValidInput = isValidInput
        (activity as TicketActivity).setButtonState(if (isValidInput) R.color.blue else R.color.grey_button_disabled, R.string.im_ready, isValidInput)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable) {}
}
