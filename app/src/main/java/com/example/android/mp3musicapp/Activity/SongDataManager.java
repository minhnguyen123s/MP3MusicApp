package com.example.android.mp3musicapp.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SongDataManager {

    private UserDatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public SongDataManager(Context context) {
        dbHelper = new UserDatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Thêm một bài hát mới vào cơ sở dữ liệu
    public long addSong(String title, String artist, String album, String filePath) {
        ContentValues values = new ContentValues();
        values.put(UserDatabaseHelper.COLUMN_SONG_TITLE, title);
        values.put(UserDatabaseHelper.COLUMN_SONG_ARTIST, artist);
        values.put(UserDatabaseHelper.COLUMN_SONG_ALBUM, album);
        values.put(UserDatabaseHelper.COLUMN_SONG_FILE_PATH, filePath);
        return database.insert(UserDatabaseHelper.TABLE_SONGS, null, values);
    }

    // Lấy tất cả bài hát từ cơ sở dữ liệu
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        Cursor cursor = database.query(UserDatabaseHelper.TABLE_SONGS,
                null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idColumnIndex = cursor.getColumnIndex(UserDatabaseHelper.COLUMN_SONG_ID);
                int titleColumnIndex = cursor.getColumnIndex(UserDatabaseHelper.COLUMN_SONG_TITLE);
                int artistColumnIndex = cursor.getColumnIndex(UserDatabaseHelper.COLUMN_SONG_ARTIST);
                int albumColumnIndex = cursor.getColumnIndex(UserDatabaseHelper.COLUMN_SONG_ALBUM);
                int filePathColumnIndex = cursor.getColumnIndex(UserDatabaseHelper.COLUMN_SONG_FILE_PATH);

                int id = (idColumnIndex != -1) ? cursor.getInt(idColumnIndex) : -1;
                String title = (titleColumnIndex != -1) ? cursor.getString(titleColumnIndex) : "";
                String artist = (artistColumnIndex != -1) ? cursor.getString(artistColumnIndex) : "";
                String album = (albumColumnIndex != -1) ? cursor.getString(albumColumnIndex) : "";
                String filePath = (filePathColumnIndex != -1) ? cursor.getString(filePathColumnIndex) : "";

                Song song = new Song(id, title, artist, album, filePath);
                songs.add(song);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return songs;
    }

    // Bạn có thể thêm các phương thức khác như lấy bài hát theo ID, tìm kiếm, v.v.
}