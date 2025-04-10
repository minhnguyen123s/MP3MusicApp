package com.example.android.mp3musicapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.mp3musicapp.Fragment.Fragment_Trang_Chu;
import com.example.android.mp3musicapp.R;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CHOOSE_AUDIO = 123;
    private static final int REQUEST_PERMISSION_READ_EXTERNAL = 1;
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_FIRST_LAUNCH = "isFirstLaunch";

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private EditText editTextSearch;
    private MainPagerAdapter pagerAdapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private Button buttonAddSong;
    private Fragment_Trang_Chu trangChuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonAddSong = findViewById(R.id.buttonAddSong);

        if (buttonAddSong != null) {
            buttonAddSong.setOnClickListener(v -> checkPermissionAndOpenFileChooser());
        }

        trangChuFragment = new Fragment_Trang_Chu();
        fragmentList.add(trangChuFragment);

        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), getLifecycle(), fragmentList);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("Trang Chủ"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSongs(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Thêm logic gọi addSampleSongs khi ứng dụng khởi chạy lần đầu
        SongDataManager songDataManager = new SongDataManager(this);
        songDataManager.open();

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean(KEY_FIRST_LAUNCH, true);

        if (isFirstLaunch) {
            songDataManager.addSampleSongs(); // Gọi phương thức addSampleSongs()
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_FIRST_LAUNCH, false);
            editor.apply();
        }

        songDataManager.close();
    }

    private void checkPermissionAndOpenFileChooser() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_READ_EXTERNAL);
        } else {
            openFileChooser();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(Intent.createChooser(intent, "Chọn bài hát"), REQUEST_CODE_CHOOSE_AUDIO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openFileChooser();
        } else {
            Toast.makeText(this, "Bạn cần cấp quyền để chọn file.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_AUDIO && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri audioUri = data.getData();
                addSongToDatabase(audioUri);
            }
        }
    }

    private void addSongToDatabase(Uri audioUri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, audioUri);

            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);

            if (title == null || title.isEmpty()) {
                title = getFileNameFromUri(audioUri);
            }

            SongDataManager songDataManager = new SongDataManager(this);
            songDataManager.open();
            long result = songDataManager.addSong(
                    title,
                    artist != null ? artist : "",
                    album != null ? album : "",
                    audioUri.toString(), // Lưu URI
                    year != null ? year : ""
            );
            songDataManager.close();

            if (result != -1) {
                Toast.makeText(this, "Đã thêm bài hát vào thư viện.", Toast.LENGTH_SHORT).show();
                if (trangChuFragment != null) {
                    trangChuFragment.loadAllSongs();
                }
            } else {
                Toast.makeText(this, "Thêm bài hát thất bại.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi đọc metadata bài hát.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getFileNameFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
        String result = "Không rõ tên";
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex >= 0) {
                result = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return result;
    }

    private void filterSongs(String query) {
        query = query.toLowerCase().trim();
        int currentItem = viewPager.getCurrentItem();
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("f" + currentItem);

        if (currentFragment instanceof Fragment_Trang_Chu) {
            ((Fragment_Trang_Chu) currentFragment).filter(query);
        }
    }

    private static class MainPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragmentList;
        public MainPagerAdapter(@NonNull FragmentManager fm, @NonNull Lifecycle lc, List<Fragment> list) {
            super(fm, lc);
            this.fragmentList = list;
        }

        @NonNull
        @Override public Fragment createFragment(int position) {
            return fragmentList.get(position);
        }

        @Override public int getItemCount() {
            return fragmentList.size();
        }
    }
}