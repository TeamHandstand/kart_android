package us.handstand.kartwheel.controller

import android.support.annotation.IntDef
import android.text.TextUtils.isEmpty
import android.util.Log
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.squareup.sqlbrite2.BriteDatabase
import io.reactivex.disposables.Disposable
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.User
import us.handstand.kartwheel.model.UserModel
import us.handstand.kartwheel.network.API


class TicketController(val db: BriteDatabase?, var listener: TicketStepCompletionListener) {
    companion object {
        const val ERROR = -2L
        const val NONE = -1L
        const val TOS = 0L
        const val CODE_ENTRY = 1L
        const val WELCOME = 2L
        const val CRITICAL_INFO = 3L
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

    private var userDisposable: Disposable? = null

    init {
        val query = User.FACTORY.select_for_id(Storage.userId)
        userDisposable = db?.createQuery(UserModel.TABLE_NAME, query.statement, *query.args)
                ?.mapToOne { User.FACTORY.select_for_idMapper().map(it) }
                ?.subscribe {
                    user = it
                    userDisposable?.dispose()
                    userDisposable = null
                }
    }

    fun onDestroy() {
        userDisposable?.dispose()
        userDisposable = null
    }

    var code: String = ""
    var user: User? = null
    var pancakeOrWaffle: String? = null
    var charmanderOrSquirtle: String? = null
    var furbyOrTamagachi: String? = null

    fun transition(@FragmentType from: Long, @FragmentType to: Long) {
        if (from != NONE && to != ERROR) {
            validateTransition(from, to)
        }
        listener.showNextStep(from, to)
    }

    fun onStepCompleted(@FragmentType type: Long) {
        when (type) {
            TOS -> transition(type, CODE_ENTRY)

            CODE_ENTRY -> {
                Log.e("onStepCompleted", "CODE_ENTRY")
                API.claimTicket(code, object : API.APICallback<User> {
                    override fun onSuccess(response: User) {
                        user = response
                        if (response.hasAllInformation()) {
                            if (response.wasOnboarded()) {
                                transition(type, if (Storage.showRaces) RACE_LIST else GAME_INFO)
                            } else {
                                transition(type, ONBOARDING)
                            }
                        } else {
                            transition(type, WELCOME)
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
            }

            ALREADY_CLAIMED -> transition(type, CODE_ENTRY)

            WELCOME -> if (user?.hasAllInformation() == true) {
                API.updateUser(user!!, object : API.APICallback<User> {
                    override fun onSuccess(response: User) {
                        transition(type, CRITICAL_INFO)
                    }

                    override fun onFailure(errorCode: Int, errorResponse: String) {
                        listener.showDialog(WELCOME, "Unable to create user: $errorResponse")
                        transition(type, ERROR)
                    }
                })
            } else {
                listener.showDialog(WELCOME, "Not all of your information was supplied!")
            }

            CRITICAL_INFO -> if (!isEmpty(pancakeOrWaffle) && !isEmpty(charmanderOrSquirtle) && !isEmpty(furbyOrTamagachi)) {
                API.updateUser(API.gson.fromJson("{" +
                        "\"pancakeOrWaffle\":\"$pancakeOrWaffle\"," +
                        "\"charmanderOrSquirtle\":\"$charmanderOrSquirtle\"," +
                        "\"furbyOrTamagachi\":\"$furbyOrTamagachi\"" +
                        "}", JsonObject::class.java), object : API.APICallback<User> {
                    override fun onSuccess(response: User) {
                        if (response.wasOnboarded()) {
                            transition(type, if (Storage.showRaces) RACE_LIST else GAME_INFO)
                        } else {
                            transition(type, ONBOARDING)
                        }
                    }

                    override fun onFailure(errorCode: Int, errorResponse: String) {
                        listener.showDialog(CRITICAL_INFO, "Unable to create user: $errorResponse")
                        transition(type, ERROR)
                    }
                })
            }


            FORFEIT -> {
                API.forfeitTicket(Storage.ticketId, object : API.APICallback<JsonElement> {
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
            else -> Log.e("onStepCompleted", "Invalid next step: " + type.toString())
        }
    }

    private fun validateTransition(@FragmentType from: Long, @FragmentType to: Long) {
        when (from) {
            TOS -> if (to == CODE_ENTRY) return
            CODE_ENTRY -> if (to == ALREADY_CLAIMED || to == GAME_INFO || to == WELCOME || to == ONBOARDING || to == RACE_LIST) return
            ALREADY_CLAIMED -> if (to == CODE_ENTRY) return
            CRITICAL_INFO -> if (to == GAME_INFO || to == ONBOARDING || to == RACE_LIST) return
            WELCOME -> if (to == CRITICAL_INFO) return
            GAME_INFO -> if (to == FORFEIT) return
            FORFEIT -> if (to == CODE_ENTRY || to == GAME_INFO) return
        }
        throw IllegalStateException("Invalid transition from $from to $to")
    }

    fun onTicketFragmentStateChanged() {
        listener.onTicketFragmentStateChanged()
    }
}
