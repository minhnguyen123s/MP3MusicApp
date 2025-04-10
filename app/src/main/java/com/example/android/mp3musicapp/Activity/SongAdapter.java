package com.example.android.mp3musicapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.mp3musicapp.Activity.Song;
import com.example.android.mp3musicapp.R;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private Context context;
    private List<Song> songList;
    private OnItemClickListener mListener; // Listener cho click thông thường
    private OnItemLongClickListener mLongListener; // Listener cho long click

    public SongAdapter(Context context, List<Song> songList) {
        this.context = context;
        this.songList = songList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) { // Phương thức set listener cho click
        mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longListener) { // Phương thức set listener cho long click
        mLongListener = longListener;
    }

    public void setSongs(List<Song> newSongList) {
        this.songList = newSongList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.tvSongTitle.setText(song.getTitle());
        holder.tvSongArtist.setText(song.getArtist());
        // Bạn có thể load ảnh bìa ở đây nếu có
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSongTitle;
        public TextView tvSongArtist;
        public ImageView imgSong;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvSongArtist = itemView.findViewById(R.id.tvSongArtist);
            imgSong = itemView.findViewById(R.id.imgSong);

            itemView.setOnClickListener(new View.OnClickListener() { // Thêm OnClickListener cho itemView
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() { // Thêm OnLongClickListener cho itemView
                @Override
                public boolean onLongClick(View v) {
                    if (mLongListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mLongListener.onItemLongClick(position);
                            return true; // Trả về true để báo sự kiện đã được xử lý
                        }
                    }
                    return false;
                }
            });
        }
    }
}