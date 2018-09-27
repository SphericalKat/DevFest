package org.firehound.devfest;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

import static org.firehound.devfest.Utils.toastWrapper;


public class AudioBroadcastFragment extends Fragment {
    private static final String TAG = "AudioBroadcastFragment";
    private RtcEngine rtcEngine;
    private boolean isBroadcaster = true;

    private IRtcEngineEventHandler rtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            Log.d(TAG, "Joined channel " + channel);
            toastWrapper(getActivity(),"Joined channel " + channel, Toast.LENGTH_SHORT);
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
            toastWrapper(getActivity(),"Left channel successfully!", Toast.LENGTH_SHORT);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            toastWrapper(getActivity(),"A user has joined the channel.", Toast.LENGTH_SHORT);
        }
    };



    public AudioBroadcastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            rtcEngine = RtcEngine.create(getActivity(), getString(R.string.agora_app_id),rtcEventHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_nav, container, false);

        //OnClick methods
        RadioButton broadcastButton = view.findViewById(R.id.audio_broadcast), audienceButton = view.findViewById(R.id.audio_audience);
        broadcastButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isBroadcaster = true;
            }
        });
        audienceButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    isBroadcaster = false;
                }
        });

        Button connectButton = view.findViewById(R.id.join_channel), disconnectButton = view.findViewById(R.id.button_disconnect);
        disconnectButton.setOnClickListener(v -> {
            rtcEngine.leaveChannel();
        });
        connectButton.setOnClickListener(v -> {
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
