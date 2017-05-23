package us.handstand.kartwheel.mocks

import android.content.Context
import android.content.SharedPreferences
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.test.mock.MockContext
import java.io.File


class MockDBContext : MockContext() {

    val sharedPrefs = MockSharedPreferences(HashMap<String, Map<String, Any>>(), "", 0)

    override fun openOrCreateDatabase(file: String?, mode: Int, factory: SQLiteDatabase.CursorFactory?, errorHandler: DatabaseErrorHandler?): SQLiteDatabase {
        return SQLiteDatabase.openOrCreateDatabase(file, factory, errorHandler)
    }

    override fun getDatabasePath(name: String?): File {
        return super.getDatabasePath(name)
    }

    override fun getPackageName(): String {
        return "us.handstand.kartwheel"
    }

    override fun getSharedPreferences(name: String?, mode: Int): SharedPreferences {
        return sharedPrefs
    }

    override fun getApplicationContext(): Context {
        return this
    }

}