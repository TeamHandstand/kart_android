package us.handstand.kartwheel.network.storage

import android.content.Context
import android.net.Uri

interface StorageProvider {
    /**
     * @return null if the upload failed
     */
    fun uploadPhoto(photoUri: Uri, context: Context): TransferObserver?
    fun getTransferById(id: Int): TransferObserver?
}
