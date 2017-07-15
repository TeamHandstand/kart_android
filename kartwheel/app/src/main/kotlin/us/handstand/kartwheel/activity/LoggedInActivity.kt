package us.handstand.kartwheel.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import us.handstand.kartwheel.R
import us.handstand.kartwheel.layout.LoggedInPagerAdapter
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.layout.setCandyCaneBackground
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.notifications.PubNubManager

class LoggedInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PubNubManager.setup(Database.get())
        setContentView(R.layout.activity_logged_in)
        findViewById(R.id.loggedInParent).setCandyCaneBackground(R.color.blue_background, R.color.blue)
        val pager = ViewUtil.findView<ViewPager>(this, R.id.pager)
        val tabLayout = ViewUtil.findView<TabLayout>(this, R.id.tabLayout)
        tabLayout.setupWithViewPager(pager)
        pager.adapter = LoggedInPagerAdapter(supportFragmentManager)
        pager.setCurrentItem(0, false)
    }
}
