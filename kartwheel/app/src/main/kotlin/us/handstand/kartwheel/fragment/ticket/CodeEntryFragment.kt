package us.handstand.kartwheel.fragment.ticket


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.ContentLoadingProgressBar
import android.text.Editable
import android.text.TextUtils.isEmpty
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
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
import us.handstand.kartwheel.layout.ViewUtil.findView

class CodeEntryFragment : Fragment(), TicketActivity.TicketFragment, TextView.OnEditorActionListener, TextWatcher {
    private lateinit var codeEntry: EditText
    private lateinit var progress: ContentLoadingProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentView = inflater.inflate(R.layout.fragment_ticket_code_entry, container, false) as ViewGroup
        codeEntry = findView(fragmentView, R.id.codeEditText)
        progress = findView(fragmentView, R.id.networkProgress)
        codeEntry.setOnEditorActionListener(this)
        codeEntry.addTextChangedListener(this)
        (fragmentView.findViewById(R.id.getCodeText) as TextView).movementMethod = LinkMovementMethod.getInstance()
        return fragmentView
    }

    override fun getTitleResId(): Int {
        return R.string.app_name
    }

    override fun getAdvanceButtonTextResId(): Int {
        return R.string.lets_go
    }

    override fun getAdvanceButtonColor(): Int {
        return if (isAdvanceButtonEnabled()) R.color.blue else super.getAdvanceButtonColor()
    }

    override fun getAdvanceButtonLoadingColor(): Int {
        return R.color.blue_loading
    }

    override fun isAdvanceButtonEnabled(): Boolean {
        return !isEmpty(codeEntry.text?.toString())
    }

    fun setProgressVisibility(visibility: Int) {
        progress.visibility = visibility
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == IME_ACTION_GO || event?.action == ACTION_DOWN) {
            activity.findViewById(R.id.button).performClick()
            return true
        }
        return false
    }

    override fun afterTextChanged(s: Editable?) {
        ticketController.code = codeEntry.text.toString().toLowerCase().replace(" ", "")
        return ticketController.onTicketFragmentStateChanged()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}
