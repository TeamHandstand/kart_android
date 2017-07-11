package us.handstand.kartwheel.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils.isEmpty
import android.view.ViewTreeObserver
import rx.Subscription
import us.handstand.kartwheel.R
import us.handstand.kartwheel.controller.OnboardingController
import us.handstand.kartwheel.controller.TicketController
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.Database
import us.handstand.kartwheel.model.Event
import us.handstand.kartwheel.model.EventModel
import us.handstand.kartwheel.model.Storage


class LaunchActivity : AppCompatActivity() {
    var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        val parentView = findViewById(R.id.parent)
        parentView.viewTreeObserver.addOnDrawListener(object : ViewTreeObserver.OnDrawListener {
            override fun onDraw() {
                parentView.viewTreeObserver.removeOnDrawListener(this)
                parentView.background = ViewUtil.drawStripes(this@LaunchActivity, parentView.measuredWidth.toFloat(), parentView.measuredHeight.toFloat(), R.color.blue_background, R.color.blue)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (isEmpty(Storage.eventId)) {
            startActivity(Intent(this, TicketActivity::class.java))
            finish()
        } else {
            val eventQuery = Event.FACTORY.select_all(Storage.eventId)
            subscription = Database.get().createQuery(EventModel.TABLE_NAME, eventQuery.statement, *eventQuery.args)
                    .mapToOne { it.use { Event.FACTORY.select_allMapper().map(it) } }
                    .subscribe {
                        if (it.usersCanSeeRaces() == true
                                && (Storage.lastTicketState == TicketController.RACE_LIST || Storage.lastTicketState == TicketController.ONBOARDING)
                                && Storage.lastOnboardingState == OnboardingController.FINISHED) {
                            startActivity(Intent(this, LoggedInActivity::class.java))
                        } else {
                            startActivity(Intent(this, TicketActivity::class.java))
                        }
                        finish()
                    }
        }
    }

    override fun onPause() {
        super.onPause()
        subscription?.unsubscribe()
    }
}