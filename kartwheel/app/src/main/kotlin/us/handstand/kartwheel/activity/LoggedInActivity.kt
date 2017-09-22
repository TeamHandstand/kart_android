package us.handstand.kartwheel.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_logged_in.*
import us.handstand.kartwheel.R
import us.handstand.kartwheel.layout.LoggedInPagerAdapter
import us.handstand.kartwheel.layout.setCandyCaneBackground
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.notifications.PubNubManager

class LoggedInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PubNubManager.setup(Database.get())
        setContentView(R.layout.activity_logged_in)
        findViewById<View>(R.id.parent).setCandyCaneBackground(android.R.color.white, R.color.textLightGrey_40p)
        pager.adapter = LoggedInPagerAdapter(this, supportFragmentManager)
        pager.setCurrentItem(0, false)
        tabLayout.setupWithViewPager(pager)
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            tab!!.setCustomView(R.layout.tab_item)
            val layout = tab.customView!!
            layout.findViewById<ImageView>(R.id.icon).setImageResource(LoggedInPagerAdapter.getIconFromType(i))
            layout.findViewById<TextView>(R.id.title).text = LoggedInPagerAdapter.getTitleFromType(i)
        }
    }
}
