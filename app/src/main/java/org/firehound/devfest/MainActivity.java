package org.firehound.devfest;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.RtcEngine;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";
    private static final String FIRST_START = "org.firehound.devfest.FIRST_START";
    public static final int REQ_CODE = 1;
    public static RtcEngine rtcEngine;
    private FirebaseAuth firebaseAuth;
    public static List<String> admins = new ArrayList<>();
    public static boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

        //First start welcome activity
        boolean firstStart = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(FIRST_START, true);
        if (firstStart) {
            Intent intent = new Intent(this, MainIntroActivity.class);
            startActivityForResult(intent, REQ_CODE);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new AudioBroadcastFragment()).commit();

        //BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.setSelectedItemId(R.id.nav_audio);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            android.support.v4.app.Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    break;
                case R.id.nav_audio:
                        selectedFragment = new AudioBroadcastFragment();
                    break;
                case R.id.nav_video:
                    selectedFragment = new VideoBroadcastFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selectedFragment).commit();
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            if (resultCode == RESULT_OK) {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(FIRST_START, false).apply();
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(FIRST_START, true).apply();
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivityForResult(new Intent(this, MainIntroActivity.class), REQ_CODE);
        } else {
            isAdmin = admins.contains(firebaseAuth.getCurrentUser().getUid());
        }

    }
}
