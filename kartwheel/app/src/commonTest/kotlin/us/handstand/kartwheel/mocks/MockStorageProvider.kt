package us.handstand.kartwheel.mocks

import android.content.Context
import android.net.Uri
import us.handstand.kartwheel.network.storage.StorageProvider
import us.handstand.kartwheel.network.storage.TransferListener
import us.handstand.kartwheel.network.storage.TransferObserver
import us.handstand.kartwheel.network.storage.TransferState

object MockStorageProvider : StorageProvider {
    var uploading: Boolean = false
    val transferObserver = MockTransferObserver()

    override fun upload(photoUri: Uri, context: Context): TransferObserver {
        uploading = true
        return transferObserver
    }

    override fun getTransferById(id: Int): TransferObserver? {
        return transferObserver
    }

    fun failUpload() {
        transferObserver.transferListener_?.onStateChanged(1, TransferState.FAILED)
    }

    class MockTransferObserver : TransferObserver {
        var transferListener_: TransferListener? = null
        override var key: String = "123"
        override var bytesTotal: Long = 100
        override var bytesTransferred: Long = 0
            set(value) {
                transferListener_?.onProgressChanged(1, value, bytesTotal)
                if (value == bytesTotal) {
                    uploading = false
                    transferListener_?.onStateChanged(1, TransferState.COMPLETED)
                }
                field = value
            }

        override fun setTransferListener(transferListener: TransferListener) {
            transferListener_ = transferListener
        }

        override fun cleanTransferListener() {
            transferListener_ = null
        }
    }
}
