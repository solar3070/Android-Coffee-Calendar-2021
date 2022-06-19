package ddwu.mobile.finalproject.ma01_20170580;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";

    private CompactCalendarView compactCalendarView;
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("yyyy년 MM월", Locale.KOREA);

    final int ADD_CODE = 100;
    final int UPDATE_CODE = 200;

    ListView listView = null;
    DBManager dbManager;
    DBHelper helper;
    Cursor cursor;
    MyCursorAdapter adapter;

    boolean read = true;
    ArrayList<Record> recordList;
    String dateClicked = null;

    PendingIntent sender = null;
    AlarmManager alarmManager = null;
    String today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        long now = System.currentTimeMillis();
        today = dateFormatForDisplaying.format(new Date(now));

        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        recordList = new ArrayList<Record>();
        dbManager = new DBManager(this);

        listView = (ListView)findViewById(R.id.listView);
        helper = new DBHelper(this);
//        adapter = new SimpleCursorAdapter (this, android.R.layout.simple_list_item_2, null,
//                new String[] { DBHelper.COL_DATE, DBHelper.COL_CAFE},
//                new int[] { android.R.id.text1, android.R.id.text2 },
//                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        adapter = new MyCursorAdapter(this, R.layout.listview_layout, null);
        listView.setAdapter(adapter);

        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);

        TextView textView_month = (TextView) findViewById(R.id.textView_month);
        textView_month.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date click) {
                SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = transFormat.format(click);
                dateClicked = date;

                selectDate(date);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                textView_month.setText(dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });

        getEvents();

        // 추가
        Button add_record = (Button)findViewById(R.id.add_record);
        add_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtra("date", dateClicked);
                startActivityForResult(intent, ADD_CODE);
            }
        });

        // 수정
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Record record = new Record();

                int dateIdx = cursor.getColumnIndex(DBHelper.COL_DATE);
                int cafeIdx = cursor.getColumnIndex(DBHelper.COL_CAFE);
                int addressIdx = cursor.getColumnIndex(DBHelper.COL_ADDRESS);
                int menuIdx = cursor.getColumnIndex(DBHelper.COL_MENU);
                int memoIdx = cursor.getColumnIndex(DBHelper.COL_MEMO);
                int pathIdx = cursor.getColumnIndex(DBHelper.COL_PATH);
                record.set_id(id);
                record.setDate(cursor.getString(dateIdx));
                record.setCafe(cursor.getString(cafeIdx));
                record.setAddress(cursor.getString(addressIdx));
                record.setMenu(cursor.getString(menuIdx));
                record.setMemo(cursor.getString(memoIdx));
                record.setPath(cursor.getString(pathIdx));

                Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                intent.putExtra("record", record);
                startActivityForResult(intent, UPDATE_CODE);
            }
        });

        // 삭제
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("기록 삭제")
                        .setMessage("기록을 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (dbManager.removeRecord(id)) {
                                    Toast.makeText(MainActivity.this, getString(R.string.deleteSuccess), Toast.LENGTH_SHORT).show();
                                    removeEvent();
                                    readDB();
                                } else {
                                    Toast.makeText(MainActivity.this, getString(R.string.deleteFail), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("아니오", null)
                        .show();
                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    addEvent(data.getStringExtra("date"), data.getStringExtra("cafe"));
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(this, getString(R.string.addRecordCancel), Toast.LENGTH_SHORT).show();
                    break;
            }
        } else if (requestCode == UPDATE_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    Toast.makeText(this, getString(R.string.updateRecordMessage), Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(this, getString(R.string.updateRecordCancel), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private Date parsingDate(String date) {
        Date trans_date = null;
        try {
            trans_date = dateFormatForDisplaying.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return trans_date;
    }

    private void addEvent(String date, String cafe) {
        long time = parsingDate(date).getTime();
        compactCalendarView.addEvent(new Event(Color.WHITE, time, date + " " + cafe));

        if (date.equals(today)) {
            int n = compactCalendarView.getEvents(parsingDate(date)).size();
            // 오늘 커피를 마신 횟수가 3을 넘으면 알림
            if (n >= 3) {
                Intent intent = new Intent(this, BroadcastReceiver.class);
                sender = PendingIntent.getBroadcast(this, 0, intent, 0);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(SystemClock.elapsedRealtime());
                calendar.add(Calendar.SECOND, 1);

                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);
            }
        }

    }

    private void removeEvent() {
        int dateIdx = cursor.getColumnIndex(DBHelper.COL_DATE);
        int areaIdx = cursor.getColumnIndex(DBHelper.COL_CAFE);
        String date = cursor.getString(dateIdx);
        String place = cursor.getString(areaIdx);

        Log.d(TAG, date + "랑라아랄라라라" + place);

        List<Event> events = compactCalendarView.getEvents(parsingDate(date));
        for (Event ev : events) {
            Log.d(TAG, ev.getData().toString());
            if (ev.getData().equals(date + " " + place)) {
                Log.d(TAG, ev.getData().toString());
                compactCalendarView.removeEvent(ev);
                break;
            }
        }
    }

    private void getEvents() {
        recordList.addAll(dbManager.getAllRecord());
        for (Record rd : recordList) {
            addEvent(rd.getDate(), rd.getCafe());
        }
    }

    public void selectDate(String date) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String selection = helper.COL_DATE + "=?";
        String[] selectArgs = new String[] { date };
        cursor = db.query(DBHelper.TABLE_NAME, null, selection, selectArgs, null, null, null, null);
        adapter.changeCursor(cursor);
        helper.close();
    }

    public void readDB() {
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME, null);
        adapter.changeCursor(cursor);
        helper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (read) {
            readDB();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }

}