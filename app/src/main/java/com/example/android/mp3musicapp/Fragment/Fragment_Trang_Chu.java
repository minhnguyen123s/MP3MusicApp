package com.example.android.mp3musicapp.Fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.mp3musicapp.Activity.PlayMusicActivity;
import com.example.android.mp3musicapp.Activity.Song;
import com.example.android.mp3musicapp.Activity.SongDataManager;
import com.example.android.mp3musicapp.Adapter.SongAdapter;
import com.example.android.mp3musicapp.R;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Trang_Chu extends Fragment implements SongAdapter.OnItemClickListener {

    View view;
    RecyclerView recyclerViewSongs;
    SongDataManager songDataManager;
    SongAdapter songAdapter;
    List<Song> songList;
    List<Song> originalSongList = new ArrayList<>(); // Thêm danh sách gốc
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = new MediaPlayer();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_trang_chu, container, false);
        recyclerViewSongs = view.findViewById(R.id.recyclerViewSongs);
        recyclerViewSongs.setLayoutManager(new LinearLayoutManager(getActivity()));

        songDataManager = new SongDataManager(getActivity());
        songDataManager.open();
        songList = songDataManager.getAllSongs();
        originalSongList.addAll(songList); // Sao chép danh sách ban đầu
        songDataManager.close();

        songAdapter = new SongAdapter(getActivity(), songList);
        songAdapter.setOnItemClickListener(this);
        recyclerViewSongs.setAdapter(songAdapter);

        return view;
    }

    public void filter(String query) {
        List<Song> filteredList = new ArrayList<>();
        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(originalSongList);
        } else {
            query = query.toLowerCase().trim();
            for (Song song : originalSongList) {
                if (song.getTitle().toLowerCase().contains(query) ||
                        song.getArtist().toLowerCase().contains(query)) {
                    filteredList.add(song);
                }
            }
        }
        songAdapter.setSongs(filteredList); // Sử dụng phương thức setSongs của Adapter
    }

    @Override
    public void onItemClick(int position) {
        Song clickedSong = songList.get(position);
        Intent intent = new Intent(getActivity(), PlayMusicActivity.class);
        intent.putExtra("song", clickedSong);
        intent.putExtra("songList", (java.util.ArrayList) originalSongList); // Truyền danh sách gốc
        intent.putExtra("currentSongIndex", position);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}