package us.handstand.kartwheel.model


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.squareup.sqlbrite.BriteDatabase
import com.squareup.sqlbrite.SqlBrite

import rx.schedulers.Schedulers

class Database private constructor(context: Context) : SQLiteOpenHelper(context, Database.DB_NAME, null, Database.VERSION) {
    private val db: BriteDatabase

    init {
        val sqlBrite = SqlBrite.Builder().build()
        db = sqlBrite.wrapDatabaseHelper(this, Schedulers.io())
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(Team.CREATE_TABLE)
        sqLiteDatabase.execSQL(Ticket.CREATE_TABLE)
        sqLiteDatabase.execSQL(User.CREATE_TABLE)
        sqLiteDatabase.execSQL(Race.CREATE_TABLE)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        // Nothing to upgrade yet.
    }

    companion object {
        private var database: Database? = null
        private val DB_NAME = "kart_wheel"
        private val VERSION = 1
        private val tables = arrayOf(Team.TABLE_NAME, Ticket.TABLE_NAME, User.TABLE_NAME, Race.TABLE_NAME)

        fun initialize(context: Context) {
            if (database == null) {
                database = Database(context)
            }
        }

        fun get(): BriteDatabase {
            if (database == null) {
                throw RuntimeException("Database should be initialized on app startup!")
            }
            return database!!.db
        }

        fun clear() {
            for (table in tables) {
                get().writableDatabase.delete(table, null, null)
            }
        }
    }
}