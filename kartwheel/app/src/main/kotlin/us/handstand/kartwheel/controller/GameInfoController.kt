package us.handstand.kartwheel.controller

import io.reactivex.disposables.Disposable
import us.handstand.kartwheel.model.*


open class GameInfoController {
    private var p1: User? = null
    private var p2: User? = null
    private var t1: Ticket? = null
    private var t2: Ticket? = null
    private var userDisposable: Disposable? = null
    private var ticket1Disposable: Disposable? = null
    private var ticket2Disposable: Disposable? = null
    lateinit var listener: GameInfoCompletionListener

    companion object {
        interface GameInfoCompletionListener {
            fun onPlayer1Info(user: User, ticket: Ticket)
            fun onPlayer2Info(user: User, ticket: Ticket)
        }
    }

    fun subscribe() {
        val query = User.FACTORY.select_from_team(Storage.teamId)
        userDisposable = Database.get().createQuery(UserModel.TABLE_NAME, query.statement, *query.args)
                .mapToList { User.FACTORY.select_from_teamMapper().map(it) }
                .subscribe { users ->
                    synchronized(this, {
                        onUsersFound(users)
                    })
                }
    }

    fun dispose() {
        userDisposable?.dispose()
        ticket1Disposable?.dispose()
        ticket2Disposable?.dispose()
        p1 = null
        p2 = null
        t1 = null
        t2 = null
    }

    fun getTicketForUser(userId: String): Disposable {
        val query = Ticket.FACTORY.select_for_player(userId)
        return Database.get().createQuery(TicketModel.TABLE_NAME, query.statement, *query.args)
                .mapToOne { Ticket.FACTORY.select_for_playerMapper().map(it) }
                .subscribe({ ticket ->
                    synchronized(this, {
                        onTicketFound(ticket)
                    })
                })
    }

    open fun onUsersFound(users: List<User>) {
        for (user in users) {
            if (user.id() == Storage.userId) {
                p1 = user
                ticket1Disposable = getTicketForUser(user.id())
            } else {
                p2 = user
                ticket2Disposable = getTicketForUser(user.id())
            }
        }
    }

    open fun onTicketFound(ticket: Ticket) {
        if (p1 != null && ticket.playerId() == p1!!.id()) {
            t1 = ticket
            listener.onPlayer1Info(p1!!, ticket)
        } else if (p2 != null && ticket.playerId() == p2!!.id()) {
            t2 = ticket
            listener.onPlayer2Info(p2!!, ticket)
        } else {
            throw IllegalStateException("Invalid ticket with player id " + ticket.playerId())
        }
    }
}