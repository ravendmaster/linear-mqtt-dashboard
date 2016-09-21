package com.ravendmaster.linearmqttdashboard.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String NUMERIC_TYPE = " NUMERIC";
    private static final String BLOB_TYPE = " BLOB";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_TOPICS =
            "CREATE TABLE " + HistoryContract.TopicEntry.TABLE_NAME + " (" +
                    HistoryContract.TopicEntry._ID + INTEGER_TYPE +" PRIMARY KEY" + COMMA_SEP +
                    HistoryContract.TopicEntry.COLUMN_NAME_TOPIC + TEXT_TYPE +
                    " )";
    private static final String SQL_DELETE_TOPICS =
            "DROP TABLE IF EXISTS " + HistoryContract.TopicEntry.TABLE_NAME;

    private static final String SQL_CREATE_HISTORY =
            "CREATE TABLE " + HistoryContract.HistoryEntry.TABLE_NAME + " (" +
                    HistoryContract.HistoryEntry._ID + INTEGER_TYPE +" PRIMARY KEY" + COMMA_SEP +
                    HistoryContract.HistoryEntry.COLUMN_NAME_DETAIL_LEVEL + INTEGER_TYPE + COMMA_SEP +
                    HistoryContract.HistoryEntry.COLUMN_NAME_TIMESTAMP + NUMERIC_TYPE + COMMA_SEP +
                    HistoryContract.HistoryEntry.COLUMN_NAME_TOPIC_ID + INTEGER_TYPE + COMMA_SEP +
                    HistoryContract.HistoryEntry.COLUMN_NAME_VALUE + NUMERIC_TYPE +
                    " )";
    private static final String SQL_DELETE_HISTORY =
            "DROP TABLE IF EXISTS " + HistoryContract.HistoryEntry.TABLE_NAME;


    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "linear.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TOPICS);
        db.execSQL("CREATE INDEX topics_index on topics (topic);");

        db.execSQL(SQL_CREATE_HISTORY);
        db.execSQL("CREATE INDEX history_index on history (detail_level, timestamp, topic_id);");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL(SQL_DELETE_TOPICS);
        //db.execSQL(SQL_DELETE_HISTORY);
        //onCreate(db);
        //db.execSQL("DELETE FROM HISTORY WHERE value>1000");
        //db.execSQL("CREATE INDEX topics_index on topics (topic);");
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}