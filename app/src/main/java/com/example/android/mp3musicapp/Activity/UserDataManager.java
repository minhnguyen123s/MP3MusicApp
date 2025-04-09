package com.example.android.mp3musicapp.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserDataManager {

    private UserDatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public UserDataManager(Context context) {
        dbHelper = new UserDatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long registerUser(String name, String email, String password) {
        Log.d("UserDataManager", "Bắt đầu registerUser với: Tên=" + name + ", Email=" + email);
        String passwordHash = hashPassword(password);
        Log.d("UserDataManager", "Password Hash: " + passwordHash);
        if (passwordHash == null) {
            Log.e("UserDataManager", "Lỗi khi băm mật khẩu, không thể đăng ký.");
            return -1;
        }
        ContentValues values = new ContentValues();
        values.put(UserDatabaseHelper.COLUMN_NAME, name);
        values.put(UserDatabaseHelper.COLUMN_EMAIL, email);
        values.put(UserDatabaseHelper.COLUMN_PASSWORD_HASH, passwordHash);
        long result = database.insert(UserDatabaseHelper.TABLE_USERS, null, values);
        Log.d("UserDataManager", "Kết quả insert: " + result);
        return result;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String hash = hexString.toString();
            Log.d("UserDataManager", "Mật khẩu " + password + " được băm thành: " + hash);
            return hash;
        } catch (NoSuchAlgorithmException e) {
            Log.e("UserDataManager", "Lỗi băm mật khẩu", e);
            return null;
        }
    }

    public boolean checkUserExists(String email) {
        Log.d("UserDataManager", "Kiểm tra xem email " + email + " có tồn tại không.");
        Cursor cursor = database.query(UserDatabaseHelper.TABLE_USERS,
                new String[]{UserDatabaseHelper.COLUMN_EMAIL},
                UserDatabaseHelper.COLUMN_EMAIL + "=?",
                new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        Log.d("UserDataManager", "Email " + email + " tồn tại: " + exists);
        return exists;
    }

    public boolean loginUser(String email, String password) {
        Log.d("UserDataManager", "Bắt đầu loginUser với email: " + email);
        Cursor cursor = database.query(UserDatabaseHelper.TABLE_USERS,
                new String[]{UserDatabaseHelper.COLUMN_PASSWORD_HASH},
                UserDatabaseHelper.COLUMN_EMAIL + "=?",
                new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String storedPasswordHash = cursor.getString(cursor.getColumnIndex(UserDatabaseHelper.COLUMN_PASSWORD_HASH));
            cursor.close();
            String enteredPasswordHash = hashPassword(password);
            Log.d("UserDataManager", "Hash mật khẩu đã lưu: " + storedPasswordHash);
            Log.d("UserDataManager", "Hash mật khẩu đã nhập: " + enteredPasswordHash);
            return storedPasswordHash != null && storedPasswordHash.equals(enteredPasswordHash);
        } else {
            Log.d("UserDataManager", "Không tìm thấy người dùng với email: " + email);
            return false;
        }
    }

}