package us.handstand.kartwheel.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
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
import us.handstand.kartwheel.model.TicketModel

class CodeEntryFragment : Fragment(), TicketActivity.TicketFragment, TextView.OnEditorActionListener {
    internal var codeEntry: EditText? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater!!.inflate(R.layout.fragment_code_entry, container, false) as ViewGroup
        codeEntry = ViewUtil.findView(fragmentView, R.id.code_edit_text)
        codeEntry?.setOnEditorActionListener(this)
        return fragmentView
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == IME_ACTION_GO || event?.action == ACTION_DOWN) {
            activity.findViewById(R.id.button).performClick()
            return true
        }
        return false
    }

    override fun onClick(v: View) {
        activity.intent.putExtra(TicketModel.CODE, codeEntry!!.text.toString().toLowerCase().replace(" ", ""))
    }
}
