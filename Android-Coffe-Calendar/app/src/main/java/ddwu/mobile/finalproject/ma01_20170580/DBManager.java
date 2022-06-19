package ddwu.mobile.finalproject.ma01_20170580;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBManager {

    DBHelper helper = null;

    public DBManager(Context context) {
        helper = new DBHelper(context);
    }

    public ArrayList<Record> getAllRecord() {
        ArrayList recordList = new ArrayList();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + helper.TABLE_NAME, null);

        while (cursor.moveToNext()) {
            int idIdx = cursor.getColumnIndex(helper.COL_ID);
            int dateIdx = cursor.getColumnIndex(helper.COL_DATE);
            int cafeIdx = cursor.getColumnIndex(helper.COL_CAFE);
            int addressIdx = cursor.getColumnIndex(helper.COL_ADDRESS);
            int menuIdx = cursor.getColumnIndex(helper.COL_MENU);
            int memoIdx = cursor.getColumnIndex(helper.COL_MEMO);
            int pathIdx = cursor.getColumnIndex(helper.COL_PATH);

            long id = cursor.getLong(idIdx);
            String date = cursor.getString(dateIdx);
            String cafe = cursor.getString(cafeIdx);
            String address = cursor.getString(addressIdx);
            String menu = cursor.getString(menuIdx);
            String memo = cursor.getString(memoIdx);
            String path = cursor.getString(pathIdx);

            recordList.add(new Record(id, date, cafe, address, menu, memo, path));
        }

        cursor.close();
        helper.close();
        return recordList;
    }

    public boolean addNewRecord(Record newRecord) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put(helper.COL_DATE, newRecord.getDate());
        row.put(helper.COL_CAFE, newRecord.getCafe());
        row.put(helper.COL_ADDRESS, newRecord.getAddress());
        row.put(helper.COL_MENU, newRecord.getMenu());
        row.put(helper.COL_MEMO, newRecord.getMemo());
        row.put(helper.COL_PATH, newRecord.getPath());

        long count = db.insert(helper.TABLE_NAME, null, row);

        helper.close();

        if (count > 0) return true;
        return false;
    }

    public boolean updateRecord(Record myData) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put(helper.COL_DATE, myData.getDate());
        row.put(helper.COL_CAFE, myData.getCafe());
        row.put(helper.COL_ADDRESS, myData.getAddress());
        row.put(helper.COL_MENU, myData.getMenu());
        row.put(helper.COL_MEMO, myData.getMemo());
        row.put(helper.COL_PATH, myData.getPath());

        String whereClause = helper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(myData.get_id()) };
        int result = db.update(helper.TABLE_NAME, row, whereClause, whereArgs);

        helper.close();

        if (result > 0) return true;
        return false;
    }

    public boolean removeRecord(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String whereClause = helper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(id) };

        int result = db.delete(helper.TABLE_NAME, whereClause, whereArgs);

        helper.close();

        if (result > 0) return true;
        return false;
    }

}
