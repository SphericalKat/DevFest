package org.firehound.devfest;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;


/**
 * A simple {@link Fragment} subclass.
 */
public class AudioBroadcastFragment extends Fragment {
    private static final String TAG = "AudioBroadcastFragment";
    private RtcEngine rtcEngine;
    private boolean isBroadcaster = true;
    protected void toastWrapper(final String msg, final int length) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, length).show();
            }
        });
    }
    private IRtcEngineEventHandler rtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            Log.d(TAG, "Joined channel " + channel);
            toastWrapper("Joined channel " + channel, Toast.LENGTH_SHORT);
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
            toastWrapper("Left channel successfully!", Toast.LENGTH_SHORT);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            toastWrapper("A user has joined the channel.", Toast.LENGTH_SHORT);
        }
    };
    private class rtcInitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                rtcEngine = RtcEngine.create(getActivity(), getString(R.string.agora_app_id), rtcEventHandler);
                Log.d(TAG, "Constructed RTC engine.");

            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
                throw new RuntimeException("Fatal error while initializing RTCEngine" + Log.getStackTraceString(e));
            }
            return null;
        }
    }



    public AudioBroadcastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_nav, container, false);
        rtcInitTask task = new rtcInitTask();
        task.execute();
        //OnClick methods
        RadioButton broadcastButton = view.findViewById(R.id.audio_broadcast), audienceButton = view.findViewById(R.id.audio_audience);
        broadcastButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isBroadcaster = true;
                }
            }
        });
        audienceButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isBroadcaster = false;
                }
            }
        });

        Button connectButton = view.findViewById(R.id.join_channel), disconnectButton = view.findViewById(R.id.button_disconnect);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rtcEngine.leaveChannel();
            }
        });
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (isBroadcaster) {
                    rtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
                }
                else {
                    rtcEngine.setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
                }
                EditText text = container.findViewById(R.id.channel_name);
                if (text.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "You must enter a channel name!", Toast.LENGTH_SHORT).show();
                    inputMethodManager.hideSoftInputFromWindow(text.getWindowToken(), 0);
                    return;
                }
                rtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
                rtcEngine.setAudioProfile(Constants.AUDIO_PROFILE_SPEECH_STANDARD, Constants.AUDIO_SCENARIO_DEFAULT);
                rtcEngine.joinChannel(null, String.valueOf(text.getText()), null,0);
                inputMethodManager.hideSoftInputFromWindow(text.getWindowToken(), 0);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RtcEngine.destroy();
        rtcEngine = null;
    }
}
