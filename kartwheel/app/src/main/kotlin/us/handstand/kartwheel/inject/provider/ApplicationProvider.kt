package us.handstand.kartwheel.inject.provider

import android.content.Context
import dagger.Module
import dagger.Provides
import us.handstand.kartwheel.KartWheel

@Module
class ApplicationProvider(val application: KartWheel) {
    @Provides fun context(): Context = application
    @Provides fun application(): KartWheel = application
}

