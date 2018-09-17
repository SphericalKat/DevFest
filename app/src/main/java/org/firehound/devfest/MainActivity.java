package org.firehound.devfest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";
    private RtcEngine rtcEngine;
    private boolean isBroadcaster = true;

    private final void toastWrapper(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRtcEngine();
    }

    private void initRtcEngine() {
        try {
            rtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), rtcEventHandler);
            Log.d(LOG_TAG, "Constructed RTC engine.");

        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
            throw new RuntimeException("Fatal error while initializing RTCEngine" + Log.getStackTraceString(e));
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
        if (isBroadcaster) {
            rtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        }
        else {
            rtcEngine.setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        }
        EditText text = findViewById(R.id.channel_name);
        if (text.getText().toString().equals("")) {
            Toast.makeText(this, "You must enter a channel name!", Toast.LENGTH_SHORT).show();
            return;
        }
        rtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        rtcEngine.setAudioProfile(Constants.AUDIO_PROFILE_SPEECH_STANDARD, Constants.AUDIO_SCENARIO_DEFAULT);
        rtcEngine.joinChannel(null, String.valueOf(text.getText()), "Fek aff",0);
    }

    IRtcEngineEventHandler rtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            Log.d(LOG_TAG, "Joined channel " + channel);
            toastWrapper("Joined channel " + channel);
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
            toastWrapper("Left channel successfully!");
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            toastWrapper("A user has joined the channel.");
        }
    };

    public void onDisconnectClicked(View view) {
        rtcEngine.leaveChannel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RtcEngine.destroy();
        rtcEngine = null;
    }
}
