package us.handstand.kartwheel.controller

import android.support.annotation.IntDef
import com.google.gson.JsonElement
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.User
import us.handstand.kartwheel.network.API


class TicketController(var listener: TicketStepCompletionListener) {
    companion object {
        const val ERROR = -2L
        const val NONE = -1L
        const val TOS = 0L
        const val CODE_ENTRY = 1L
        const val CRITICAL_INFO = 2L
        const val WELCOME = 3L
        const val ALREADY_CLAIMED = 4L
        const val FORFEIT = 5L
        const val GAME_INFO = 6L
        const val ONBOARDING = 7L
        const val RACE_LIST = 8L

        @IntDef(TOS, CODE_ENTRY, CRITICAL_INFO, WELCOME, ALREADY_CLAIMED, FORFEIT, GAME_INFO, ONBOARDING,
                RACE_LIST, ERROR, NONE)
        annotation class FragmentType

        interface TicketStepCompletionListener {
            fun showNextStep(@FragmentType previous: Long, @FragmentType next: Long)
            fun showDialog(@FragmentType step: Long, message: String)
            fun onTicketFragmentStateChanged()
        }
    }

    var code: String = ""
    var user: User? = null

    fun transition(@FragmentType from: Long, @FragmentType to: Long) {
        if (from != NONE && to != ERROR) {
            validateTransition(from, to)
        }
        listener.showNextStep(from, to)
    }

    fun onStepCompleted(@FragmentType type: Long) {
        when (type) {
            TOS -> transition(type, CODE_ENTRY)

            CODE_ENTRY -> API.claimTicket(code, object : API.APICallback<User> {
                override fun onSuccess(response: User) {
                    user = response
                    if (response.hasAllInformation()) {
                        if (response.wasOnboarded()) {
                            transition(type, if (Storage.showRaces) RACE_LIST else GAME_INFO)
                        } else {
                            transition(type, ONBOARDING)
                        }
                    } else {
                        transition(type, CRITICAL_INFO)
                    }
                }

                override fun onFailure(errorCode: Int, errorResponse: String) {
                    if (errorCode == 409) {
                        transition(type, ALREADY_CLAIMED)
                    } else {
                        listener.showDialog(CODE_ENTRY, errorResponse)
                    }
                }
            })

            ALREADY_CLAIMED -> transition(type, CODE_ENTRY)

            CRITICAL_INFO -> if (user?.hasCriticalInfo() == true) transition(type, WELCOME)

            WELCOME -> if (user?.hasAllInformation() == true) {
                API.updateUser(user!!, object : API.APICallback<User> {
                    override fun onSuccess(response: User) {
                        if (response.wasOnboarded()) {
                            transition(type, if (Storage.showRaces) RACE_LIST else GAME_INFO)
                        } else {
                            transition(type, ONBOARDING)
                        }
                    }

                    override fun onFailure(errorCode: Int, errorResponse: String) {
                        listener.showDialog(WELCOME, "Unable to create user: $errorResponse")
                        transition(type, ERROR)
                    }
                })
            } else {
                listener.showDialog(WELCOME, "Not all of your information was supplied!")
            }

            FORFEIT -> API.forfeitTicket(Storage.ticketId, object : API.APICallback<JsonElement> {
                override fun onSuccess(response: JsonElement) {
                    user = null
                    code = ""
                    listener.showDialog(FORFEIT, "Ticket forfeited")
                    transition(type, CODE_ENTRY)
                }

                override fun onFailure(errorCode: Int, errorResponse: String) {
                    listener.showDialog(FORFEIT, "Unable to forfeit ticket: $errorResponse")
                    transition(type, ERROR)
                }
            })
        }
    }

    private fun validateTransition(@FragmentType from: Long, @FragmentType to: Long) {
        when (from) {
            TOS -> if (to == CODE_ENTRY) return
            CODE_ENTRY -> if (to == ALREADY_CLAIMED || to == GAME_INFO || to == CRITICAL_INFO || to == ONBOARDING || to == RACE_LIST) return
            ALREADY_CLAIMED -> if (to == CODE_ENTRY) return
            CRITICAL_INFO -> if (to == WELCOME) return
            WELCOME -> if (to == GAME_INFO || to == ONBOARDING || to == RACE_LIST) return
            GAME_INFO -> if (to == FORFEIT) return
            FORFEIT -> if (to == CODE_ENTRY || to == GAME_INFO) return
        }
        throw IllegalStateException("Invalid transition from $from to $to")
    }

    fun onTicketFragmentStateChanged() {
        listener.onTicketFragmentStateChanged()
    }
}
