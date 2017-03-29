package us.handstand.kartwheel.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.TicketActivity
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.TicketModel

class CodeEntryFragment : Fragment(), TicketActivity.TicketFragment {

    internal var codeEntry: EditText? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater!!.inflate(R.layout.fragment_code_entry, container, false) as ViewGroup
        codeEntry = ViewUtil.findView(fragmentView, R.id.code_edit_text)
        return fragmentView
    }

    override fun onClick(v: View) {
        activity.intent.putExtra(TicketModel.CODE, codeEntry!!.text.toString().toLowerCase().replace(" ", ""))
    }
}
