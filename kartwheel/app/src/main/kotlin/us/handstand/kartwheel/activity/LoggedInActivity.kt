package us.handstand.kartwheel.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import us.handstand.kartwheel.R
import us.handstand.kartwheel.layout.LoggedInPagerAdapter
import us.handstand.kartwheel.layout.ViewUtil

class LoggedInActivity : AppCompatActivity() {
    lateinit var pager: ViewPager
    lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)
        pager = ViewUtil.findView(this, R.id.pager)
        tabLayout = ViewUtil.findView(this, R.id.tabLayout)
        tabLayout.setupWithViewPager(pager)
        pager.adapter = LoggedInPagerAdapter(supportFragmentManager)
        pager.setCurrentItem(0, false)
    }
}
