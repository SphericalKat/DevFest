package org.firehound.devfest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";
    private RtcEngine rtcEngine;
    private boolean isBroadcaster;
    private int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void initRtcEngine() {
        try {
            rtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), rtcEventHandler);
            Log.d(LOG_TAG, "Constructed RTC engine.");

        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
            throw new RuntimeException("Fatal error while initing RTCEngine" + Log.getStackTraceString(e));
        }
    }

    public void onButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.audio_broadcast:
                if (checked)
                    isBroadcaster = true;
                break;
            case R.id.audio_audience:
                if (checked)
                    isBroadcaster = false;
                break;
        }
    }

    public void onConnectButtonClicked(View view) {
        initRtcEngine();
        if (isBroadcaster) {
            uid = 1;
            rtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        }
        else {
            uid = 2;
            rtcEngine.setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        }
        EditText text = findViewById(R.id.channel_name);
        rtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        rtcEngine.setAudioProfile(Constants.AUDIO_PROFILE_SPEECH_STANDARD, Constants.AUDIO_SCENARIO_DEFAULT);
        rtcEngine.joinChannel(null, "demo", "Fek aff", uid);
    }

    IRtcEngineEventHandler rtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            Toast.makeText(MainActivity.this, "Connected successfully!", Toast.LENGTH_SHORT).show();
        }
    };
}
