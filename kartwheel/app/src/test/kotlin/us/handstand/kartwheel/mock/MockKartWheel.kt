package us.handstand.kartwheel.mock

import android.content.res.AssetManager
import us.handstand.kartwheel.KartWheel


class MockKartWheel : KartWheel() {
    override fun getAssets(): AssetManager? {
        return null
    }
}