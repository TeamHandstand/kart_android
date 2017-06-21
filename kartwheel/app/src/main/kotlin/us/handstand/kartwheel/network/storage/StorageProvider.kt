package us.handstand.kartwheel.network.storage

import android.content.Context
import android.net.Uri

interface StorageProvider {
    fun upload(photoUri: Uri, context: Context): TransferObserver
    fun getTransferById(id: Int): TransferObserver?
}
