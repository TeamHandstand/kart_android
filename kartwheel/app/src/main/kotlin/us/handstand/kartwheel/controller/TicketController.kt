package us.handstand.kartwheel.controller

import android.support.annotation.IntDef
import com.google.gson.JsonObject
import us.handstand.kartwheel.model.Ticket
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

        @IntDef(TOS.toLong(), CODE_ENTRY.toLong(), CRITICAL_INFO.toLong(), WELCOME.toLong(),
                ALREADY_CLAIMED.toLong(), FORFEIT.toLong(), GAME_INFO.toLong(), ERROR.toLong(), NONE.toLong())
        annotation class FragmentType

        interface TicketStepCompletionListener {
            fun showNextStep(@FragmentType previous: Int, @FragmentType next: Int)
            fun showDialog(message: String)
            fun onTicketFragmentStateChanged()
        }
    }

    var ticket: Ticket? = null
    var code: String? = null
    var user: User? = null

    fun transition(@FragmentType from: Int, @FragmentType to: Int) {
        if (from != NONE && from != ERROR) {
            validateTransition(from, to)
        }
        listener.showNextStep(from, to)
    }

    fun onStepCompleted(@FragmentType type: Int) {
        when (type) {
            TOS -> transition(type, CODE_ENTRY)

            CODE_ENTRY -> API.claimTicket(code!!, object : API.APICallback<Ticket>() {
                override fun onSuccess(response: Ticket) {
                    transition(type, if (response.isClaimed) GAME_INFO else CRITICAL_INFO)
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
                API.updateUser(user!!, object : API.APICallback<User>() {
                    override fun onSuccess(response: User) {
                        transition(type, GAME_INFO)
                    }

                    override fun onFailure(errorCode: Int, errorResponse: String) {
                        listener.showDialog("Unable to create user: $errorResponse")
                        transition(type, ERROR)
                    }
                })
            }

            FORFEIT -> API.forfeitTicket(ticket!!.id(), object : API.APICallback<JsonObject>() {
                override fun onSuccess(response: JsonObject) {
                    ticket = null
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
            CODE_ENTRY -> if (to == ALREADY_CLAIMED || to == GAME_INFO || to == CRITICAL_INFO) return
            ALREADY_CLAIMED -> if (to == CODE_ENTRY) return
            CRITICAL_INFO -> if (to == WELCOME) return
            WELCOME -> if (to == GAME_INFO) return
            GAME_INFO -> if (to == FORFEIT) return
            FORFEIT -> if (to == CODE_ENTRY || to == GAME_INFO) return
        }
        throw IllegalStateException("Invalid transition from $from to $to")
    }

    fun onTicketFragmentStateChanged() {
        listener.onTicketFragmentStateChanged()
    }
}
