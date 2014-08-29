package user.d802p;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.ContentValues;
import android.util.Log;

public class CalendarUtil extends SQLiteOpenHelper {
    final private static String databaseName = "d802p.db";
    final private static String tableNameDB = "d802p_data";
    final private static String tableNameEvent = "d802p_event";
    final private static String tableNameBasic = "d802p_basic";
    final private static String tableNameWeight = "d802p_weight";
    final private static int databaseVersion = 6;
    final public static Locale defaultLocal = Locale.TAIWAN;
    final public static String dateFormat = "yyyy-MM-dd";
    final public static String titledateFormat = "MMMM yyyy";
    final public static int MESSAGE_SET_SELECTION = 0;

    class struct_data
    {
        public String _id;
        public String _class;
        public String _name;
        public String _cal;
    }

    class struct_event
    {
        public String _id;
        public String _date;
        public String _name;
        public String _cal;
        public String _num;
    }

    class struct_basic
    {
        public String _id;
        public String _start_weight;
        public String _start_date;
        public String _end_weight;
        public String _end_date;
    }

    class struct_weight
    {
        public String _id;
        public String _date;
        public String _weight;
    }

    public CalendarUtil(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    public void openDatabase(){
    }

    public void closeDatabase(){
        this.close();
    }

    public void createData(String _class, String _name, String _cal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("_class", _class);
        values.put("_name", _name);
        values.put("_cal", _cal);
        db.insert(tableNameDB, null, values);
    }

    public void deleteData(String _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableNameDB, "_id=" + _id, null);
    }

    public ArrayList<struct_data> queryData() {
        ArrayList<struct_data> data = new ArrayList<struct_data>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"_id", "_class", "_name", "_cal"};
        Cursor cursor = db.query(tableNameDB, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            struct_data _data = new struct_data();
            _data._id = cursor.getString(0);
            _data._class = cursor.getString(1);
            _data._name = cursor.getString(2);
            _data._cal = cursor.getString(3);
            data.add(_data);
        }
        return data;
    }

    public void createEvent(String _date, String _name, String _cal, String _num) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("_date", _date);
        values.put("_name", _name);
        values.put("_cal", _cal);
        values.put("_num", _num);
        db.insert(tableNameEvent, null, values);
    }

    public void deleteEvent(String _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableNameEvent, "_id=" + _id, null);
    }

    public ArrayList<struct_event> queryEvent() {
        ArrayList<struct_event> events = new ArrayList<struct_event>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"_id", "_date", "_name", "_cal", "_num"};
        Cursor cursor = db.query(tableNameEvent, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            struct_event _event = new struct_event();
            _event._id = cursor.getString(0);
            _event._date = cursor.getString(1);
            _event._name = cursor.getString(2);
            _event._cal = cursor.getString(3);
            _event._num = cursor.getString(4);
            events.add(_event);
        }
        return events;
    }

    public void updateBasic(String _start_weight, String _start_date, String _end_weight, String _end_date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableNameBasic, "1=1", null);
        ContentValues values = new ContentValues();
        values.put("_start_weight", _start_weight);
        values.put("_start_date", _start_date);
        values.put("_end_weight", _end_weight);
        values.put("_end_date", _end_date);
        db.insert(tableNameBasic, null, values);
    }

    public ArrayList<struct_basic> queryBasic() {
        ArrayList<struct_basic> basics = new ArrayList<struct_basic>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"_id", "_start_weight", "_start_date", "_end_weight", "_end_date"};
        Cursor cursor = db.query(tableNameBasic, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            struct_basic _basic = new struct_basic();
            _basic._id = cursor.getString(0);
            _basic._start_weight = cursor.getString(1);
            _basic._start_date = cursor.getString(2);
            _basic._end_weight = cursor.getString(3);
            _basic._end_date = cursor.getString(4);
            basics.add(_basic);
        }
        return basics;
    }

    public void updateWeight(String _date, String _weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableNameWeight, "_date='" + _date + "'", null);
        ContentValues values = new ContentValues();
        values.put("_date", _date);
        values.put("_weight", _weight);
        db.insert(tableNameWeight, null, values);
    }

    public ArrayList<struct_weight> queryWeight() {
        ArrayList<struct_weight> weights = new ArrayList<struct_weight>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"_id", "_date", "_weight"};
        Cursor cursor = db.query(tableNameWeight, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            struct_weight _weight = new struct_weight();
            _weight._id = cursor.getString(0);
            _weight._date = cursor.getString(1);
            _weight._weight = cursor.getString(2);
            weights.add(_weight);
        }
        return weights;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String init_d802p_database = "CREATE TABLE " + tableNameDB + " (" +
                "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "_class" + " TEXT , " +
                "_name" + " TEXT UNIQUE, " +
                "_cal" + " TEXT);";
        final String init_d802p_event = "CREATE TABLE " + tableNameEvent + " (" +
                "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "_date" + " TEXT, " +
                "_name" + " TEXT, " +
                "_cal" + " TEXT, " +
                "_num" + " TEXT);";
        final String init_d802p_basic = "CREATE TABLE " + tableNameBasic + " (" +
                "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "_start_weight" + " TEXT, " +
                "_start_date" + " TEXT, " +
                "_end_weight" + " TEXT, " +
                "_end_date" + " TEXT);";
        final String init_d802p_weight = "CREATE TABLE " + tableNameWeight + " (" +
                "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "_date" + " TEXT, " +
                "_weight" + " TEXT);";
        db.execSQL(init_d802p_database);
        db.execSQL(init_d802p_event);
        db.execSQL(init_d802p_basic);
        db.execSQL(init_d802p_weight);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // get called when database version changed
        final String drop_d802p_database = "DROP TABLE IF EXISTS " + tableNameDB;
        final String drop_d802p_event = "DROP TABLE IF EXISTS " + tableNameEvent;
        final String drop_d802p_basic = "DROP TABLE IF EXISTS " + tableNameBasic;
        final String drop_d802p_weight = "DROP TABLE IF EXISTS " + tableNameWeight;
        db.execSQL(drop_d802p_database);
        db.execSQL(drop_d802p_event);
        db.execSQL(drop_d802p_basic);
        db.execSQL(drop_d802p_weight);
        onCreate(db);
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static double getTodayExpectedWeight(ArrayList<CalendarUtil.struct_basic> basics, String queryDate) {
        double w = 0;
        if (basics.size() == 1)
        {
            String start_date = basics.get(0)._start_date;
            String end_date = basics.get(0)._end_date;
            double start_weight = Double.parseDouble(basics.get(0)._start_weight);
            double end_weight = Double.parseDouble(basics.get(0)._end_weight);
            if (queryDate.compareTo(start_date) >= 0 && queryDate.compareTo(end_date) <= 0) {
                SimpleDateFormat dateParser = new SimpleDateFormat(dateFormat, defaultLocal);
                try {
                    Date queryDateObj = dateParser.parse(queryDate);
                    Date startDateObj = dateParser.parse(start_date);
                    Date endDateObj = dateParser.parse(end_date);
                    long queryDateTs = queryDateObj.getTime();
                    long startDateTs = startDateObj.getTime();
                    long endDateTs = endDateObj.getTime();
                    double totalOffset = (double) ((endDateTs - startDateTs) / 86400000L);
                    double dateOffset = (double) ((queryDateTs - startDateTs) / 86400000L);
                    w = start_weight + (end_weight - start_weight) * (dateOffset / totalOffset);
                } catch (Exception ex) {}
            }
        }

        return w;
    }
}

