package us.handstand.kartwheel.model;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import rx.schedulers.Schedulers;

public class Database extends SQLiteOpenHelper {
    private static Database database;
    private final BriteDatabase db;
    private static final String DB_NAME = "kart_wheel";
    private static final int VERSION = 1;

    private Database(Context context) {
        super(context, DB_NAME, null, VERSION);
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        db = sqlBrite.wrapDatabaseHelper(this, Schedulers.io());
    }

    public static void initialize(Context context) {
        if (database == null) {
            database = new Database(context);
        }
    }

    public static BriteDatabase get() {
        if (database == null) {
            throw new RuntimeException("Database should be initialized on app startup!");
        }
        return database.db;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Team.CREATE_TABLE);
        sqLiteDatabase.execSQL(Ticket.CREATE_TABLE);
        sqLiteDatabase.execSQL(User.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Nothing to upgrade yet.
    }
}