package com.example.finalassignment_group5_topic1b2.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "project_management.db";
    private static final int DATABASE_VERSION = 1;

    // task
    public static final String TABLE_TASK = "task";
    public static final String COLUMN_TASK_ID = "id";
    public static final String COLUMN_TASK_NAME = "task_name";
    public static final String COLUMN_ESTIMATE_DAY = "estimate_day";

    // project
    public static final String TABLE_PROJECT = "project";
    public static final String COLUMN_PROJECT_ID = "id";
    public static final String COLUMN_DEV_NAME = "dev_name";
    public static final String COLUMN_TASK_FOREIGN_ID = "task_id"; // foregin key with task
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTaskTable = "CREATE TABLE " + TABLE_TASK + " (" +
                COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TASK_NAME + " TEXT NOT NULL, " +
                COLUMN_ESTIMATE_DAY + " INTEGER)";

        String createProjectTable = "CREATE TABLE " + TABLE_PROJECT + " (" +
                COLUMN_PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DEV_NAME + " TEXT, " +
                COLUMN_TASK_FOREIGN_ID + " INTEGER, " +
                COLUMN_START_DATE + " TEXT, " +
                COLUMN_END_DATE + " TEXT, " +
                "FOREIGN KEY (" + COLUMN_TASK_FOREIGN_ID + ") REFERENCES " + TABLE_TASK + "(" + COLUMN_TASK_ID + "))";

        db.execSQL(createTaskTable);
        db.execSQL(createProjectTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        onCreate(db);
    }
}
