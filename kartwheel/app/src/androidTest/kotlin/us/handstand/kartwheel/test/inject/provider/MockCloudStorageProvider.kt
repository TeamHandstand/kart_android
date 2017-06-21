package us.handstand.kartwheel.test.inject.provider

import dagger.Provides
import us.handstand.kartwheel.inject.provider.CloudStorageProvider
import us.handstand.kartwheel.mocks.MockStorageProvider
import us.handstand.kartwheel.network.storage.StorageProvider

@dagger.Module
class MockCloudStorageProvider : CloudStorageProvider() {
    @Provides override fun getStorageProvider(): StorageProvider = MockStorageProvider
}
