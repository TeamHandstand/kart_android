package us.handstand.kartwheel.network.storage

/**
 * Wrapper for any cloud storage we choose to go with.
 */
interface TransferObserver {
    val key: String
    val bytesTotal: Long
    val bytesTransferred: Long
    fun setTransferListener(transferListener: TransferListener)
    fun cleanTransferListener()
}
