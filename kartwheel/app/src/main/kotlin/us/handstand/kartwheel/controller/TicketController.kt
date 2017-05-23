package us.handstand.kartwheel.controller

import android.support.annotation.IntDef
import com.google.gson.JsonElement
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.User
import us.handstand.kartwheel.network.API


class TicketController(var listener: TicketStepCompletionListener) {
    companion object {
        const val ERROR = -2
        const val NONE = -1
        const val TOS = 0
        const val CODE_ENTRY = 1
        const val CRITICAL_INFO = 2
        const val WELCOME = 3
        const val ALREADY_CLAIMED = 4
        const val FORFEIT = 5
        const val GAME_INFO = 6
        const val ONBOARDING = 7
        const val RACE_LIST = 8

        @IntDef(TOS.toLong(), CODE_ENTRY.toLong(), CRITICAL_INFO.toLong(), WELCOME.toLong(),
                ALREADY_CLAIMED.toLong(), FORFEIT.toLong(), GAME_INFO.toLong(), ONBOARDING.toLong(),
                RACE_LIST.toLong(), ERROR.toLong(), NONE.toLong())
        annotation class FragmentType

        interface TicketStepCompletionListener {
            fun showNextStep(@FragmentType previous: Int, @FragmentType next: Int)
            fun showDialog(message: String)
            fun onTicketFragmentStateChanged()
        }
    }

    var code: String? = null
    var user: User? = null

    fun transition(@FragmentType from: Int, @FragmentType to: Int) {
        if (from != NONE && to != ERROR) {
            validateTransition(from, to)
        }
        listener.showNextStep(from, to)
    }

    fun onStepCompleted(@FragmentType type: Int) {
        when (type) {
            TOS -> transition(type, CODE_ENTRY)

            CODE_ENTRY -> API.claimTicket(code!!, object : API.APICallback<User> {
                override fun onSuccess(response: User) {
                    user = response
                    var nextTransition: Int = CRITICAL_INFO
                    if (response.hasAllInformation()) {
                        if (response.wasOnboarded()) {
                            if (Storage.showRaces) {
                                nextTransition = RACE_LIST
                            } else {
                                nextTransition = GAME_INFO
                            }
                        } else {
                            nextTransition = ONBOARDING
                        }
                    }
                    transition(type, nextTransition)
                }

                override fun onFailure(errorCode: Int, errorResponse: String) {
                    if (errorCode == 409) {
                        transition(type, ALREADY_CLAIMED)
                    } else {
                        listener.showDialog(errorResponse)
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
                        listener.showDialog("Unable to create user: $errorResponse")
                        transition(type, ERROR)
                    }
                })
            }

            FORFEIT -> API.forfeitTicket(Storage.ticketId, object : API.APICallback<JsonElement> {
                override fun onSuccess(response: JsonElement) {
                    user = null
                    code = null
                    listener.showDialog("Ticket forfeited")
                    transition(type, CODE_ENTRY)
                }

                override fun onFailure(errorCode: Int, errorResponse: String) {
                    listener.showDialog("Unable to forfeit ticket: $errorResponse")
                    transition(type, ERROR)
                }
            })
        }
    }

    private fun validateTransition(@FragmentType from: Int, @FragmentType to: Int) {
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
