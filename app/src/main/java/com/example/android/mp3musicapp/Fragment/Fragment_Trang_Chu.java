package com.example.android.mp3musicapp.Fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

public class Fragment_Trang_Chu extends Fragment implements SongAdapter.OnItemClickListener, SongAdapter.OnItemLongClickListener {

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
        loadAllSongs(); // Tải danh sách bài hát khi Fragment được tạo

        return view;
    }

    public void loadAllSongs() {
        songDataManager.open();
        songList = songDataManager.getAllSongs();
        originalSongList.clear(); // Xóa danh sách gốc cũ
        originalSongList.addAll(songList); // Sao chép danh sách mới
        songDataManager.close();

        if (songAdapter == null) {
            songAdapter = new SongAdapter(getActivity(), songList);
            songAdapter.setOnItemClickListener(this);
            songAdapter.setOnItemLongClickListener(this); // Thiết lập OnItemLongClickListener
            recyclerViewSongs.setAdapter(songAdapter);
        } else {
            songAdapter.setSongs(songList); // Cập nhật danh sách trong Adapter
        }
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
    public void onItemLongClick(int position) {
        Song songToDelete = songList.get(position);
        showDeleteConfirmationDialog(songToDelete, position);
    }

    private void showDeleteConfirmationDialog(Song song, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa bài hát")
                .setMessage("Bạn có chắc chắn muốn xóa bài hát '" + song.getTitle() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteSong(song, position);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteSong(Song song, int position) {
        songDataManager.open();
        long result = songDataManager.deleteSong(song.getId());
        songDataManager.close();

        if (result > 0) {
            songList.remove(position);
            songAdapter.notifyItemRemoved(position);
            Toast.makeText(getContext(), "Đã xóa bài hát.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Xóa bài hát thất bại.", Toast.LENGTH_SHORT).show();
        }
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