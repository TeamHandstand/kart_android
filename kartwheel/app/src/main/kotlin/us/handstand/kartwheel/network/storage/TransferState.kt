package us.handstand.kartwheel.network.storage

enum class TransferState {

    /**
     * This state represents a transfer that has been queued, but has not yet
     * started
     */
    WAITING,
    /**
     * This state represents a transfer that is currently uploading or
     * downloading data
     */
    IN_PROGRESS,
    /**
     * This state represents a transfer that is paused
     */
    PAUSED,
    /**
     * This state represents a transfer that has been resumed and queued for
     * execution, but has not started to actively transfer data
     */
    RESUMED_WAITING,
    /**
     * This state represents a transfer that is completed
     */
    COMPLETED,
    /**
     * This state represents a transfer that is canceled
     */
    CANCELED,
    /**
     * This state represents a transfer that has failed
     */
    FAILED,

    /**
     * This state represents a transfer that is currently on hold, waiting for
     * the network to become available
     */
    WAITING_FOR_NETWORK,
    /**
     * This state represents a transfer that is a completed part of a multi-part
     * upload. This state is primarily used internally and there should be no
     * need to use this state.
     */
    PART_COMPLETED,
    /**
     * This state represents a transfer that has been requested to cancel, but
     * the service processing transfers has not yet fulfilled this request. This
     * state is primarily used internally and there should be no need to use
     * this state.
     */
    PENDING_CANCEL,
    /**
     * This state represents a transfer that has been requested to pause by the
     * client, but the service processing transfers has not yet fulfilled this
     * request. This state is primarily used internally and there should be no
     * need to use this state.
     */
    PENDING_PAUSE,
    /**
     * This state represents a transfer that has been requested to pause by the
     * client because the network has been loss, but the service processing
     * transfers has not yet fulfilled this request. This state is primarily
     * used internally and there should be no need to use this state.
     */
    PENDING_NETWORK_DISCONNECT,
    /**
     * This is an internal value used to detect if the current transfer is in an
     * unknown state
     */
    UNKNOWN;

    companion object {
        private val MAP = mutableMapOf<String, TransferState>()

        init {
            for (state in TransferState.values()) {
                MAP.putIfAbsent(state.toString(), state)
            }
        }

        /**
         * Returns the transfer state from string
         *
         * @param stateAsString state of the transfer represented as string.
         * @return the {@link TransferState}
         */
        fun getState(stateAsString: String): TransferState {
            if (MAP.containsKey(stateAsString)) {
                return MAP.get(stateAsString)!!
            }
            return UNKNOWN;
        }
    }
}
