package us.handstand.kartwheel.test

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner

class KartWheelTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, AndroidTestKartWheel::class.java.name, context);
    }
}
