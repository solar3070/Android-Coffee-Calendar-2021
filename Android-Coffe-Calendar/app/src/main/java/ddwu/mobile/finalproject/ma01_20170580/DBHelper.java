package ddwu.mobile.finalproject.ma01_20170580;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    final static String DB_NAME = "recordCoffee.db";

    public final static String TABLE_NAME = "recordCoffee";
    public final static String COL_ID = "_id";
    public final static String COL_DATE = "date";
    public final static String COL_CAFE = "cafe";
    public final static String COL_ADDRESS = "address";
    public final static String COL_MENU = "menu";
    public final static String COL_MEMO = "memo";
    public final static String COL_PATH = "path";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " integer primary key autoincrement, "
                + COL_DATE + " TEXT, " + COL_CAFE + " TEXT, " + COL_ADDRESS + " TEXT, "
                + COL_MENU + " TEXT, " + COL_MEMO + " TEXT, " + COL_PATH + " TEXT )";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
