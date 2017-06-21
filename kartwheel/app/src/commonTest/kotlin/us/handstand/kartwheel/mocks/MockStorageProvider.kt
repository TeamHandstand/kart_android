package us.handstand.kartwheel.mocks

import android.content.Context
import android.net.Uri
import us.handstand.kartwheel.network.storage.StorageProvider
import us.handstand.kartwheel.network.storage.TransferListener
import us.handstand.kartwheel.network.storage.TransferObserver

object MockStorageProvider : StorageProvider {

    override fun upload(photoUri: Uri, context: Context): TransferObserver {
        return transferObserver
    }

    override fun getTransferById(id: Int): TransferObserver? {
        return transferObserver
    }

    // TODO: Need to replace this var if you don't want your tests crashing
    var transferObserver = object : TransferObserver {
        override val key: String
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val bytesTotal: Long
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val bytesTransferred: Long
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

        override fun setTransferListener(transferListener: TransferListener) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun cleanTransferListener() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}
