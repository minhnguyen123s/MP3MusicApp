package com.example.android.mp3musicapp.Fragment;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.mp3musicapp.Activity.Song;
import com.example.android.mp3musicapp.Activity.SongDataManager;
import com.example.android.mp3musicapp.Adapter.SongAdapter;
import com.example.android.mp3musicapp.R;

import java.io.IOException;
import java.util.List;

public class Fragment_Trang_Chu extends Fragment implements SongAdapter.OnItemClickListener {

    View view;
    RecyclerView recyclerViewSongs;
    SongDataManager songDataManager;
    SongAdapter songAdapter;
    List<Song> songList;
    private MediaPlayer mediaPlayer; // Khai báo MediaPlayer

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = new MediaPlayer(); // Khởi tạo MediaPlayer
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_trang_chu, container, false);
        recyclerViewSongs = view.findViewById(R.id.recyclerViewSongs); // Thêm RecyclerView vào layout
        recyclerViewSongs.setLayoutManager(new LinearLayoutManager(getActivity()));

        songDataManager = new SongDataManager(getActivity());
        songDataManager.open();
        songList = songDataManager.getAllSongs();
        songDataManager.close();

        songAdapter = new SongAdapter(getActivity(), songList);
        songAdapter.setOnItemClickListener(this); // Set OnItemClickListener cho Adapter
        recyclerViewSongs.setAdapter(songAdapter);

        // Thử thêm một vài bài hát mẫu (chỉ chạy một lần hoặc khi cần thiết)
        addSampleSongs();

        return view;
    }

    private void addSampleSongs() {
        songDataManager.open();
        songDataManager.addSong("Bài hát 1", "Nghệ sĩ 1", "Album 1",
                "android.resource://" + getActivity().getPackageName() + "/" + R.raw.gapemdungluc);
        songDataManager.addSong("Bài hát 2", "Nghệ sĩ 2", "Album 2",
                "android.resource://" + getActivity().getPackageName() + "/" + R.raw.phaidaucuoctinh);
        songDataManager.addSong("Bài hát 3", "Nghệ sĩ 1", "Album 3",
                "android.resource://" + getActivity().getPackageName() + "/" + R.raw.quenmotnguoi); // Thêm một file nữa nếu bạn có
        songDataManager.close();
    }

    @Override
    public void onItemClick(int position) {
        Song clickedSong = songList.get(position);
        String filePath = clickedSong.getFilePath();

        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(getActivity(), Uri.parse(filePath));
                mediaPlayer.prepare();
                mediaPlayer.start();
                Log.d("TrangChuFragment", "Đang phát: " + clickedSong.getTitle());
            } else {
                Log.e("TrangChuFragment", "MediaPlayer chưa được khởi tạo.");
            }
        } catch (IOException e) {
            Log.e("TrangChuFragment", "Lỗi phát nhạc: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Giải phóng MediaPlayer khi Fragment bị hủy
            mediaPlayer = null;
        }
    }
}