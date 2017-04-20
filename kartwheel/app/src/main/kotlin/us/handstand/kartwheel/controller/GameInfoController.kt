package us.handstand.kartwheel.controller

import android.database.Cursor
import com.squareup.sqlbrite.BriteDatabase
import us.handstand.kartwheel.model.Ticket
import us.handstand.kartwheel.model.User


class GameInfoController constructor(val db: BriteDatabase, val teamId: String, val userId: String) {
    private var listener: GameInfoCompletionListener? = null
    private var p1: User? = null
    private var p2: User? = null
    private var t1: Ticket? = null
    private var t2: Ticket? = null

    init {
        val query = User.FACTORY.select_from_team(teamId)
        db.createQuery(User.TABLE_NAME, query.statement, *query.args)
                .subscribe({ updateUsers(it.run()) }, { it.printStackTrace() })
    }

    companion object {
        interface GameInfoCompletionListener {
            fun onPlayer1Info(user: User, ticket: Ticket)
            fun onPlayer2Info(user: User, ticket: Ticket)
        }
    }

    fun setGameInfoCompetionListener(listener: GameInfoCompletionListener) {
        this.listener = listener
        synchronized(this, {
            if (p1 != null && t1 != null) {
                listener.onPlayer1Info(p1!!, t1!!)
            }
        })

        synchronized(this, {
            if (p2 != null && t2 != null) {
                listener.onPlayer2Info(p2!!, t2!!)
            }
        })
    }

    fun updateUsers(cursor: Cursor?) {
        cursor.use { cursor ->
            while (cursor!!.moveToNext()) {
                val user = User.FACTORY.select_from_teamMapper().map(cursor)
                if (user.id() == userId) {
                    synchronized(this, { p1 = user })
                } else {
                    synchronized(this, { p2 = user })
                }
                getTicketForUser(user)
            }
        }
    }

    fun getTicketForUser(user: User) {
        val query = Ticket.FACTORY.select_for_player(user.id())
        db.createQuery(Ticket.TABLE_NAME, query.statement, *query.args)
                .subscribe({
                    val cursor = it.run()
                    cursor.use {
                        if (cursor!!.moveToFirst()) {
                            val ticket = Ticket.FACTORY.select_for_playerMapper().map(cursor)
                            if (p1 != null && ticket.playerId() == p1!!.id()) {
                                synchronized(this, {
                                    t1 = ticket
                                    listener?.onPlayer1Info(p1!!, t1!!)
                                })
                            } else if (p2 != null && ticket.playerId() == p2!!.id()) {
                                synchronized(this, {
                                    t2 = ticket
                                    listener?.onPlayer2Info(p2!!, t2!!)
                                })
                            } else {
                                throw IllegalStateException("Invalid ticket with player id " + ticket.playerId()) as Throwable
                            }
                        }
                    }
                })
    }
}