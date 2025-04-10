package com.example.android.mp3musicapp.Activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "music_app_users";
    private static final int DATABASE_VERSION = 3; // Tăng version để kích hoạt onUpgrade

    // Bảng người dùng
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD_HASH = "password_hash";

    private static final String SQL_CREATE_USERS_TABLE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME + " TEXT NOT NULL," +
                    COLUMN_EMAIL + " TEXT UNIQUE NOT NULL," +
                    COLUMN_PASSWORD_HASH + " TEXT NOT NULL)";

    private static final String SQL_DELETE_USERS_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_USERS;

    // Bảng bài hát
    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_SONG_ID = "_id";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_SONG_ARTIST = "artist";
    public static final String COLUMN_SONG_ALBUM = "album";
    public static final String COLUMN_SONG_FILE_PATH = "file_path";
    public static final String COLUMN_SONG_YEAR = "year";

    private static final String SQL_CREATE_SONGS_TABLE =
            "CREATE TABLE " + TABLE_SONGS + " (" +
                    COLUMN_SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_SONG_TITLE + " TEXT NOT NULL," +
                    COLUMN_SONG_ARTIST + " TEXT," +
                    COLUMN_SONG_ALBUM + " TEXT," +
                    COLUMN_SONG_FILE_PATH + " TEXT NOT NULL," +
                    COLUMN_SONG_YEAR + " TEXT DEFAULT '')"; // Thêm cột YEAR và đặt giá trị mặc định

    private static final String SQL_DELETE_SONGS_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_SONGS;

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_SONGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(UserDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        if (oldVersion < 3) {
            // Thêm cột year nếu phiên bản cũ chưa có (nếu bạn đã ở version 2)
            db.execSQL("ALTER TABLE " + TABLE_SONGS + " ADD COLUMN " + COLUMN_SONG_YEAR + " TEXT DEFAULT '';");
        }
        // Bạn có thể thêm các thay đổi schema khác ở đây nếu cần
    }
}