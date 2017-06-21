package us.handstand.kartwheel.inject.provider

import dagger.Module
import dagger.Provides
import us.handstand.kartwheel.network.storage.StorageProvider
import us.handstand.kartwheel.network.storage.aws.AWS

@Module
open class CloudStorageProvider {
    @Provides open fun getStorageProvider(): StorageProvider = AWS
}
