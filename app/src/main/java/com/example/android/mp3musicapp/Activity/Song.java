package com.example.android.mp3musicapp.Activity;

import java.io.Serializable;

public class Song implements Serializable {
    private int id;
    private String title;
    private String artist;
    private String album;
    private String filePath;
    private String year;

    // Sửa constructor này để nhận tham số 'year'
    public Song(int id, String title, String artist, String album, String filePath, String year) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.filePath = filePath;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getFilePath() {
        return filePath;
    }
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}