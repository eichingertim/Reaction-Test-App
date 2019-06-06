package com.teapps.hcireactiontest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "reaction_database";
    private static final String COLUMN_1 = "ID";
    private static final String COLUMN_2 = "user_id";
    private static final String COLUMN_3 = "test_type";
    private static final String COLUMN_4 = "reaction_time";
    private static final String COLUMN_5 = "gender";
    private static final String COLUMN_6 = "age";

    public DBHelper(Context context) {
        super(context, TABLE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_2 + " TEXT, " + COLUMN_3 + " TEXT, "
                + COLUMN_4 + " TEXT, " + COLUMN_5 + " TEXT, "
                + COLUMN_6 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String userID, String testType, String reactionTime
            , String gender, String age) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_2, userID);
        contentValues.put(COLUMN_3, testType);
        contentValues.put(COLUMN_4, reactionTime);
        contentValues.put(COLUMN_5, gender);
        contentValues.put(COLUMN_6, age);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return  result != -1;

    }

    public void clearDatabase() {

        SQLiteDatabase db = this.getWritableDatabase();
        String clear = "delete from " + TABLE_NAME;
        db.execSQL(clear);

    }

}
