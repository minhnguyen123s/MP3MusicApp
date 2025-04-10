package com.example.android.mp3musicapp.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import com.example.android.mp3musicapp.R;

import java.util.ArrayList;
import java.util.List;

public class SongDataManager {

    private UserDatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private Context context;

    public SongDataManager(Context context) {
        dbHelper = new UserDatabaseHelper(context);
        this.context = context;
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Thêm một bài hát mới vào cơ sở dữ liệu
    public long addSong(String title, String artist, String album, String filePath, String year) {
        ContentValues values = new ContentValues();
        values.put(UserDatabaseHelper.COLUMN_SONG_TITLE, title);
        values.put(UserDatabaseHelper.COLUMN_SONG_ARTIST, artist);
        values.put(UserDatabaseHelper.COLUMN_SONG_ALBUM, album);
        values.put(UserDatabaseHelper.COLUMN_SONG_FILE_PATH, filePath);
        values.put(UserDatabaseHelper.COLUMN_SONG_YEAR, year); // Thêm trường year
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
                int yearColumnIndex = cursor.getColumnIndex(UserDatabaseHelper.COLUMN_SONG_YEAR);

                int id = (idColumnIndex != -1) ? cursor.getInt(idColumnIndex) : -1;
                String title = (titleColumnIndex != -1) ? cursor.getString(titleColumnIndex) : "";
                String artist = (artistColumnIndex != -1) ? cursor.getString(artistColumnIndex) : "";
                String album = (albumColumnIndex != -1) ? cursor.getString(albumColumnIndex) : "";
                String filePath = (filePathColumnIndex != -1) ? cursor.getString(filePathColumnIndex) : "";
                String yearFromDB = (yearColumnIndex != -1) ? cursor.getString(yearColumnIndex) : "";

                Song song = new Song(id, title, artist, album, filePath, yearFromDB);
                songs.add(song);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return songs;
    }

    // Thêm 3 bài hát mẫu nếu cơ sở dữ liệu trống
    public void addSampleSongs() {
        open(); // Mở database trước khi thao tác
        if (getAllSongs().isEmpty()) {
            String package_name = context.getPackageName();

            // Bài hát mẫu 1: gapemdungluc.mp3
            Uri gapemdunglucUri = Uri.parse("android.resource://" + package_name + "/" + R.raw.gapemdungluc);
            addSong("Gặp Em Đúng Lúc", "Ca Sĩ Gặp Em", "Album Gặp Em", gapemdunglucUri.toString(), "2020");

            // Bài hát mẫu 2: phaidaucuoctinh.mp3
            Uri phaidaucuoctinhUri = Uri.parse("android.resource://" + package_name + "/" + R.raw.phaidaucuoctinh);
            addSong("Phai Dấu Cuộc Tình", "Ca Sĩ Phai Dấu", "Album Phai Dấu", phaidaucuoctinhUri.toString(), "2021");

            // Bài hát mẫu 3: quenmotnguoi.mp3
            Uri quenmotnguoiUri = Uri.parse("android.resource://" + package_name + "/" + R.raw.quenmotnguoi);
            addSong("Quên Một Người", "Ca Sĩ Quên Một Người", "Album Quên Một Người", quenmotnguoiUri.toString(), "2022");

            Log.d("SongDataManager", "Đã thêm 3 bài hát mẫu từ raw resource vào cơ sở dữ liệu.");
        } else {
            Log.d("SongDataManager", "Cơ sở dữ liệu đã có bài hát, không thêm bài hát mẫu.");
        }
        close(); // Đóng database sau khi thao tác
    }

    // Lấy thông tin album và năm từ metadata (nếu có)
    private String getMetadata(String filePath, int metadataKey) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(context, Uri.parse("file://" + filePath));
            return retriever.extractMetadata(metadataKey);
        } catch (Exception e) {
            Log.e("SongDataManager", "Lỗi khi đọc metadata cho: " + filePath, e);
            return null;
        } finally {
            try {
                retriever.release();
            } catch (Exception e) {
                // Xử lý lỗi khi release retriever (có thể đã bị lỗi trước đó)
            }
        }
    }

    // Lấy tất cả bài hát từ thiết bị và đọc metadata
    public List<Song> getAllSongsFromDevice() {
        List<Song> songList = new ArrayList<>();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                android.provider.MediaStore.Audio.Media._ID,
                android.provider.MediaStore.Audio.Media.TITLE,
                android.provider.MediaStore.Audio.Media.ARTIST,
                android.provider.MediaStore.Audio.Media.ALBUM,
                android.provider.MediaStore.Audio.Media.DATA // Path
        };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String album = cursor.getString(3);
                String filePath = cursor.getString(4);
                String year = getMetadata(filePath, MediaMetadataRetriever.METADATA_KEY_YEAR);

                Song song = new Song(id, title, artist, album, filePath, year != null ? year : "");
                songList.add(song);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return songList;
    }

    // Thêm phương thức xóa bài hát theo ID
    public long deleteSong(int songId) {
        open();
        long result = database.delete(
                UserDatabaseHelper.TABLE_SONGS,
                UserDatabaseHelper.COLUMN_SONG_ID + "=?",
                new String[]{String.valueOf(songId)}
        );
        close();
        return result;
    }
}