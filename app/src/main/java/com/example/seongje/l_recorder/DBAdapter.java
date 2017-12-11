package com.example.seongje.l_recorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by SEONGJE on 2017-12-04.
 */

public class DBAdapter {

    private static final String DATABASE_NAME = "ITEM";
    private static final int DATABASE_VERSION = 1;
    //기본으로 주어지는 것들
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private final Context context;


    public DBAdapter(Context ctx) {
        context = ctx;
        dbHelper = new DBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBAdapter open() throws SQLException {//SQLException 통해서 디비어뎁터 열기
        db = dbHelper.getWritableDatabase();
        // 읽고 쓰기 위해 DB 연다. 권한이 없거나 디스크가 가득 차면 실패
        return this;
    }

    public void close() {
        db.close();
    }//디비 닫는 메소드


    private static final String ADRESS_KEY_TITLE = "title";
    private static final String ADRESS_KEY_DATE = "date";
    private static final String ADRESS_KEY_TIME= "time";
    private static final int ADRESS_KEY_COLUMN_TITLE = 1;
    private static final int ADRESS_KEY_COLUMN_DATE = 2;
    private static final int ADRESS_KEY_COLUMN_TIME= 3;

    private static final String ADRESS_DATABASE_TABLE = "memotable";
    private static final String ADRESS_DATABASE_CREATE = "create table "
            + ADRESS_DATABASE_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT," + ADRESS_KEY_TITLE + " TEXT, "
            + ADRESS_KEY_DATE  + " TEXT, "+ ADRESS_KEY_TIME + " TEXT);";


    public void insertAddress(ListViewItems object) {//입력
        ContentValues newValues = new ContentValues();


        Cursor c = db.query(ADRESS_DATABASE_TABLE, null, null, null, null,
                null, null);

        c.moveToLast();

        newValues.put(ADRESS_KEY_TITLE, object.getTitle());
        newValues.put(ADRESS_KEY_DATE, object.getDate());
        newValues.put(ADRESS_KEY_TIME, object.getTime());


        db.insert(ADRESS_DATABASE_TABLE, null, newValues);

    }

    public void editAddress(Items object, int id) {
        ContentValues newValues = new ContentValues();

        Cursor c = db.query(ADRESS_DATABASE_TABLE, null, null, null, null,
                null, null);
        c.moveToPosition(id);//입력된 id로 이동

        newValues.put(ADRESS_KEY_TITLE, object.getTitle());
        newValues.put(ADRESS_KEY_DATE, object.getDate());
        newValues.put(ADRESS_KEY_TIME, object.getTime());

        db.update(ADRESS_DATABASE_TABLE, newValues, "_id = "+id, null);
    }

    public void delAddress(int id) {


        db.delete(ADRESS_DATABASE_TABLE,"_id = "+id, null);

    }

    public ListViewItems select(int id) {

        Cursor c = db.query(ADRESS_DATABASE_TABLE+" where _id = "+id, null, null, null, null,
                null, null);

        c.moveToFirst();

        //테이블 내에서 _id와 같은 id위치 찾기
        ListViewItems temp = new ListViewItems(c
                .getString(ADRESS_KEY_COLUMN_TITLE)
                , c.getString(ADRESS_KEY_COLUMN_DATE)   , c.getString(ADRESS_KEY_COLUMN_TIME));
        temp.setId(c.getInt(0));

        c.close();

        return temp;
    }

    public ArrayList<ListViewItems> selectAllPersonList() {
        ArrayList<ListViewItems> returnValue = new ArrayList<>();

        Cursor c = db.query(ADRESS_DATABASE_TABLE, null, null, null, null,
                null, null);

        if ((c.getCount() == 0) || !c.moveToFirst()) {

        } else if (c.moveToFirst()) {
            do {
                ListViewItems temp = new ListViewItems(c
                        .getString(ADRESS_KEY_COLUMN_TITLE)
                        , c.getString(ADRESS_KEY_COLUMN_DATE)   , c.getString(ADRESS_KEY_COLUMN_TIME));
                temp.setId(c.getInt(0));
                returnValue.add(temp);

            } while (c.moveToNext());
        }
        c.close();

        return returnValue;
    }

    private static class DBHelper extends SQLiteOpenHelper {//디비 헬퍼

        public DBHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory,
                        int version) {//생성자
            super(context, dbName, factory, version);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ADRESS_DATABASE_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + ADRESS_DATABASE_TABLE);
            //onCreate(db);
        }
    }
}
