package com.example.android.mp3musicapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mp3musicapp.Activity.Song;
import com.example.android.mp3musicapp.R;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PlayMusicActivity extends AppCompatActivity {
    private Song currentSong;
    private boolean isPlaying = false;

    private TextView textViewSongTitle;
    private TextView textViewArtistName;
    private ImageButton buttonPlayPause;
    private ImageButton buttonPrevious;
    private ImageButton buttonNext;
    private SeekBar seekBarProgress;
    private TextView textViewCurrentTime;
    private TextView textViewTotalTime;
    private MediaPlayer mediaPlayer;
    private List<Song> songList;
    private int currentSongIndex = 0;
    private Handler handler = new Handler();
    private ImageButton buttonShuffle;
    private ImageButton buttonRepeat;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private int repeatMode = REPEAT_OFF; // Sử dụng hằng số cho trạng thái lặp lại
    private static final int REPEAT_OFF = 0;
    private static final int REPEAT_ONE = 1;
    private static final int REPEAT_ALL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        // Ánh xạ các View
        textViewSongTitle = findViewById(R.id.textViewSongTitle);
        textViewArtistName = findViewById(R.id.textViewArtistName);
        buttonPlayPause = findViewById(R.id.buttonPlayPause);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonNext = findViewById(R.id.buttonNext);
        seekBarProgress = findViewById(R.id.seekBarProgress);
        textViewCurrentTime = findViewById(R.id.textViewCurrentTime);
        textViewTotalTime = findViewById(R.id.textViewTotalTime);
        buttonShuffle = findViewById(R.id.buttonShuffle);
        buttonRepeat = findViewById(R.id.buttonRepeat);

        // Khởi tạo MediaPlayer ở đây
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            buttonPlayPause.setImageResource(android.R.drawable.ic_media_play);
            if (isRepeat && repeatMode == REPEAT_ONE) {
                // Phát lại bài hát hiện tại
                playMusic(currentSong.getFilePath());
            } else if (isShuffle) {
                // Phát một bài hát ngẫu nhiên khác
                playNextRandomSong();
            } else {
                // Hành vi mặc định: phát bài tiếp theo
                playNextSong();
            }
        });
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e("PlayMusicActivity", "Lỗi MediaPlayer: what=" + what + ", extra=" + extra);
            return false;
        });

        // Nhận dữ liệu Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("song")) {
            currentSong = (Song) intent.getSerializableExtra("song");
            songList = (List<Song>) intent.getSerializableExtra("songList");
            currentSongIndex = intent.getIntExtra("currentSongIndex", 0);
            if (currentSong != null) {
                textViewSongTitle.setText(currentSong.getTitle());
                textViewArtistName.setText(currentSong.getArtist());
                playMusic(currentSong.getFilePath());
            } else {
                finish();
            }
        } else {
            finish();
        }

        // Thiết lập OnClickListener cho các nút
        buttonPlayPause.setOnClickListener(v -> togglePlayPause());
        buttonPrevious.setOnClickListener(v -> playPreviousSong());
        buttonNext.setOnClickListener(v -> playNextSong());
        buttonShuffle.setOnClickListener(v -> toggleShuffle());
        buttonRepeat.setOnClickListener(v -> toggleRepeat());



        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                    textViewCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Không cần xử lý
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Không cần xử lý
            }
        });
    }

    private void playPreviousSong() {
        if (songList != null && !songList.isEmpty()) {
            if (isRepeat && repeatMode == REPEAT_ONE) {
                // Phát lại bài hát hiện tại
                playMusic(currentSong.getFilePath());
            } else if (isShuffle) {
                // Phát một bài hát ngẫu nhiên khác (tương tự như next)
                playNextRandomSong();
            } else {
                // Hành vi mặc định: phát bài trước theo thứ tự
                currentSongIndex--;
                if (currentSongIndex < 0) {
                    currentSongIndex = songList.size() - 1; // Quay lại cuối danh sách
                }
                currentSong = songList.get(currentSongIndex);
                updatePlayingSong();
                playMusic(currentSong.getFilePath());
            }
        }
    }

    private void playNextSong() {
        if (songList != null && !songList.isEmpty()) {
            if (isRepeat && repeatMode == REPEAT_ONE) {
                // Phát lại bài hát hiện tại
                playMusic(currentSong.getFilePath());
            } else if (isShuffle) {
                // Phát một bài hát ngẫu nhiên khác
                playNextRandomSong();
            } else {
                // Hành vi mặc định: phát bài tiếp theo theo thứ tự
                currentSongIndex++;
                if (currentSongIndex >= songList.size()) {
                    currentSongIndex = 0; // Quay lại đầu danh sách
                }
                currentSong = songList.get(currentSongIndex);
                updatePlayingSong();
                playMusic(currentSong.getFilePath());
            }
        }
    }

    private void playMusic(String filePath) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, Uri.parse(filePath));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                seekBarProgress.setMax(mediaPlayer.getDuration());
                textViewTotalTime.setText(formatTime(mediaPlayer.getDuration()));
                mediaPlayer.start();
                isPlaying = true;
                buttonPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                updateSeekBar();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playNextRandomSong() {
        if (songList != null && !songList.isEmpty()) {
            Random random = new Random();
            int randomIndex;
            // Đảm bảo chọn một chỉ mục khác với chỉ mục hiện tại (nếu có nhiều hơn 1 bài hát)
            if (songList.size() > 1) {
                do {
                    randomIndex = random.nextInt(songList.size());
                } while (randomIndex == currentSongIndex);
            } else {
                randomIndex = 0; // Chỉ có một bài hát, phát lại nó (tương tự repeat one khi shuffle bật)
            }
            currentSongIndex = randomIndex;
            currentSong = songList.get(currentSongIndex);
            updatePlayingSong();
            playMusic(currentSong.getFilePath());
        }
    }

    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                mediaPlayer.pause();
                buttonPlayPause.setImageResource(android.R.drawable.ic_media_play);
            } else {
                mediaPlayer.start();
                buttonPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            }
            isPlaying = !isPlaying;
        }
    }

    private void updatePlayingSong() {
        textViewSongTitle.setText(currentSong.getTitle());
        textViewArtistName.setText(currentSong.getArtist());
    }

    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBarProgress.setProgress(currentPosition);
                    textViewCurrentTime.setText(formatTime(currentPosition));
                }
                handler.postDelayed(this, 1000); // Cập nhật mỗi giây
            }
        }, 1000);
    }
    private void toggleShuffle() {
        isShuffle = !isShuffle;
        if (isShuffle) {
            buttonShuffle.setImageResource(R.drawable.ic_shuffle_on); // Icon bật shuffle
            // Tắt repeat nếu đang bật (tùy chọn)
            if (repeatMode != REPEAT_OFF) {
                repeatMode = REPEAT_OFF;
                buttonRepeat.setImageResource(R.drawable.ic_loop); // Đặt về icon tắt repeat (hoặc lặp lại tất cả)
            }
        } else {
            buttonShuffle.setImageResource(R.drawable.ic_shuffle); // Icon tắt shuffle
        }
        // Logic xáo trộn hoặc đánh dấu trạng thái shuffle
    }

    private void toggleRepeat() {
        if (repeatMode == REPEAT_OFF) {
            repeatMode = REPEAT_ONE;
            buttonRepeat.setImageResource(R.drawable.ic_loop_on); // Icon lặp lại một bài
            isRepeat = true;
            // Tắt shuffle nếu đang bật (tùy chọn)
            if (isShuffle) {
                isShuffle = false;
                buttonShuffle.setImageResource(R.drawable.ic_shuffle); // Icon tắt shuffle
            }
        } else { // repeatMode == REPEAT_ONE
            repeatMode = REPEAT_OFF;
            buttonRepeat.setImageResource(R.drawable.ic_loop); // Icon tắt lặp lại (hoặc icon mặc định)
            isRepeat = false;
        }
        Log.d("PlayMusicActivity", "isRepeat: " + isRepeat + ", repeatMode: " + repeatMode);
    }

    private String formatTime(int milliseconds) {
        return String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            handler.removeCallbacksAndMessages(null);
        }
    }
}