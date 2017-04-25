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
        for (createTableStatement in createTables) {
            sqLiteDatabase.execSQL(createTableStatement)
        }
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        // Nothing to upgrade yet.
    }

    companion object {
        private var database: Database? = null
        private val DB_NAME = "kart_wheel"
        private val VERSION = 1
        private val createTables = arrayOf(TeamModel.CREATE_TABLE, TicketModel.CREATE_TABLE, UserModel.CREATE_TABLE, RaceModel.CREATE_TABLE)
        private val tables = arrayOf(TeamModel.TABLE_NAME, TicketModel.TABLE_NAME, UserModel.TABLE_NAME, RaceModel.TABLE_NAME)

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

        fun clear(db: BriteDatabase?) {
            if (db == null) {
                return
            }
            for (table in tables) {
                db.writableDatabase.delete(table, null, null)
            }
        }
    }
}