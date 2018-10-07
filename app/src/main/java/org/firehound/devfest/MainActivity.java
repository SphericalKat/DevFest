package org.firehound.devfest;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";
    private static final String FIRST_START = "org.firehound.devfest.FIRST_START";
    public static final int REQ_CODE = 1;
    public static List<String> admins = new ArrayList<>();
    public static RtcEngine rtcEngine;
    private FirebaseAuth firebaseAuth;
    private boolean isAdmin;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signout:
                firebaseAuth.signOut();
                startActivityForResult(new Intent(this, MainIntroActivity.class), REQ_CODE);
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        admins = Arrays.asList(getResources().getStringArray(R.array.admins));
        try {
            rtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), new IRtcEngineEventHandler() {});
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
        }

        //Toolbar
        Toolbar toolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

        //First start welcome activity
        boolean firstStart = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(FIRST_START, true);
        if (firstStart) {
            Intent intent = new Intent(this, MainIntroActivity.class);
            startActivityForResult(intent, REQ_CODE);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();


        //BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            android.support.v4.app.Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_audio:
                    if (admins.contains(firebaseAuth.getCurrentUser().getUid())) {
                        Toast.makeText(this, firebaseAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT);
                        selectedFragment = new AudioBroadcastFragment();
                    } else {
                        selectedFragment = new AudioReceiveFragment();
                    }
                    break;
                case R.id.nav_video:
                    selectedFragment = new VideoBroadcastFragment();
                    break;
            }
            fragmentManager.beginTransaction().replace(R.id.frameLayout, selectedFragment).commit();
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            if (resultCode == RESULT_OK) {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(FIRST_START, false).apply();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(FIRST_START, true).apply();
                finish();
            }
        }
    }
}
