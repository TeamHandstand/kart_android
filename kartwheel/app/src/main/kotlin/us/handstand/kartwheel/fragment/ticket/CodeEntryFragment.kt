package us.handstand.kartwheel.fragment.ticket


import android.text.TextUtils.isEmpty
import android.view.KeyEvent.ACTION_DOWN
import android.view.inputmethod.EditorInfo.IME_ACTION_GO
import us.handstand.kartwheel.R

class CodeEntryFragment : android.support.v4.app.Fragment(), us.handstand.kartwheel.activity.TicketActivity.TicketFragment, android.widget.TextView.OnEditorActionListener, android.text.TextWatcher {
    internal var codeEntry: android.widget.EditText? = null

    var progress: android.support.v4.widget.ContentLoadingProgressBar? = null
    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        val fragmentView = inflater!!.inflate(us.handstand.kartwheel.R.layout.fragment_code_entry, container, false) as android.view.ViewGroup
        codeEntry = us.handstand.kartwheel.layout.ViewUtil.findView(fragmentView, R.id.code_edit_text)
        progress = us.handstand.kartwheel.layout.ViewUtil.findView(fragmentView, R.id.networkProgress)
        codeEntry?.setOnEditorActionListener(this)
        codeEntry?.addTextChangedListener(this)
        return fragmentView
    }

    override fun getTitleResId(): Int {
        return us.handstand.kartwheel.R.string.app_name
    }

    override fun getAdvanceButtonTextResId(): Int {
        return us.handstand.kartwheel.R.string.lets_go
    }

    override fun getAdvanceButtonColor(): Int {
        return if (isAdvanceButtonEnabled()) us.handstand.kartwheel.R.color.blue else super.getAdvanceButtonColor()
    }

    override fun isAdvanceButtonEnabled(): Boolean {
        return !isEmpty(codeEntry?.text?.toString())
    }

    fun setProgressVisibility(visibility: Int) {
        progress?.visibility = visibility
    }

    override fun onEditorAction(v: android.widget.TextView?, actionId: Int, event: android.view.KeyEvent?): Boolean {
        if (actionId == IME_ACTION_GO || event?.action == ACTION_DOWN) {
            activity.findViewById(us.handstand.kartwheel.R.id.button).performClick()
            return true
        }
        return false
    }

    override fun afterTextChanged(s: android.text.Editable?) {
        ticketController.code = codeEntry!!.text.toString().toLowerCase().replace(" ", "")
        return ticketController.onTicketFragmentStateChanged()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}
