package app.com.example.android.hagglemaster;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ahaidrey on 7/30/15.
 */
public class HaggleDB extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Haggle.db";
//    public static final String table = "(address TEXT, bed INT, bath DECIMAL(10,5), price DECIMAL(10,5), date DATETIME)";

    public HaggleDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEM_TABLE = "CREATE TABLE item ( " + "title TEXT, " + "price INT, " +
                "address TEXT, " + "description TEXT, " + "image BLOB )";
        db.execSQL(CREATE_ITEM_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // blank for now , but will need it to update db with new item
        this.onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // blank for now
    }
}
