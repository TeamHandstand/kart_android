package us.handstand.kartwheel.network.storage.aws

import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import us.handstand.kartwheel.network.storage.TransferListener
import us.handstand.kartwheel.network.storage.TransferObserver
import java.lang.Exception


class AWSTransferObserver(val awsObserver: com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver) : TransferObserver {

    override val key: String
        get() = awsObserver.key
    override val bytesTotal: Long
        get() = awsObserver.bytesTotal
    override val bytesTransferred: Long
        get() = awsObserver.bytesTransferred

    override fun setTransferListener(transferListener: TransferListener) {
        awsObserver.setTransferListener(object : com.amazonaws.mobileconnectors.s3.transferutility.TransferListener {
            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                transferListener.onProgressChanged(id, bytesCurrent, bytesTotal)
            }

            override fun onStateChanged(id: Int, state: TransferState) {
                transferListener.onStateChanged(id, us.handstand.kartwheel.network.storage.TransferState.getState(state.toString()))
            }

            override fun onError(id: Int, ex: Exception?) {
                transferListener.onError(id, ex)
            }
        })
    }

    override fun cleanTransferListener() {
        awsObserver.cleanTransferListener()
    }
}
