package us.handstand.kartwheel.network.storage

interface TransferListener {
    /**
     * Called when the state of the transfer is changed.
     *
     * @param id The id of the transfer record.
     * @param state The new state of the transfer.
     */
    fun onStateChanged(id: Int, state: TransferState);

    /**
     * Called when more bytes are transferred.
     *
     * @param id The id of the transfer record.
     * @param bytesCurrent Bytes transferred currently.
     * @param bytesTotal The total bytes to be transferred.
     */
    fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long);

    /**
     * Called when an exception happens.
     *
     * @param id The id of the transfer record.
     * @param ex An exception object.
     */
    fun onError(id: Int, ex: Exception?);
}
