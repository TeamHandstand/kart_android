package us.handstand.kartwheel.controller

import com.squareup.sqlbrite.BriteDatabase
import rx.Subscription
import us.handstand.kartwheel.model.*


class GameInfoController constructor(val listener: GameInfoCompletionListener, val db: BriteDatabase, val teamId: String, val userId: String) {
    private var p1: User? = null
    private var p2: User? = null
    private var t1: Ticket? = null
    private var t2: Ticket? = null
    private var userSubscription: Subscription? = null
    private var ticket1Subscription: Subscription? = null
    private var ticket2Subscription: Subscription? = null


    companion object {
        interface GameInfoCompletionListener {
            fun onPlayer1Info(user: User, ticket: Ticket)
            fun onPlayer2Info(user: User, ticket: Ticket)
        }
    }

    fun subscribe() {
        val query = User.FACTORY.select_from_team(teamId)
        userSubscription = db.createQuery(UserModel.TABLE_NAME, query.statement, *query.args)
                .mapToList { User.FACTORY.select_from_teamMapper().map(it) }
                .subscribe { users ->
                    for (user in users) {
                        synchronized(this, {
                            if (user.id() == userId) {
                                p1 = user
                                ticket1Subscription = getTicketForUser(user.id())
                            } else {
                                p2 = user
                                ticket2Subscription = getTicketForUser(user.id())
                            }
                        })
                    }
                }
    }

    fun unsubscribe() {
        userSubscription?.unsubscribe()
        ticket1Subscription?.unsubscribe()
        ticket2Subscription?.unsubscribe()
        p1 = null
        p2 = null
        t1 = null
        t2 = null
    }

    fun getTicketForUser(userId: String): Subscription {
        val query = Ticket.FACTORY.select_for_player(userId)
        return db.createQuery(TicketModel.TABLE_NAME, query.statement, *query.args)
                .mapToOne { Ticket.FACTORY.select_for_playerMapper().map(it) }
                .subscribe({ ticket ->
                    synchronized(this, {
                        if (p1 != null && ticket.playerId() == p1!!.id()) {
                            t1 = ticket
                            listener.onPlayer1Info(p1!!, ticket)
                        } else if (p2 != null && ticket.playerId() == p2!!.id()) {
                            t2 = ticket
                            listener.onPlayer2Info(p2!!, ticket)
                        } else {
                            throw IllegalStateException("Invalid ticket with player id " + ticket.playerId())
                        }
                    })
                })
    }
}