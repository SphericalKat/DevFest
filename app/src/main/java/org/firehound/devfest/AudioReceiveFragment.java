package org.firehound.devfest;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;

import static org.firehound.devfest.MainActivity.rtcEngine;


public class AudioReceiveFragment extends Fragment {


    public AudioReceiveFragment() {
        // Required empty public constructor
    }
    private FragmentActivity fragmentActivity;
    private static final String TAG = "AudioReceiveFragment";
    private FirebaseAuth firebaseAuth;
    private IRtcEngineEventHandler rtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            Log.d(TAG, "Joined channel " + channel);
            Utils.toastWrapper(fragmentActivity,"Joined channel " + channel, Toast.LENGTH_SHORT);
            fragmentActivity.runOnUiThread(() -> {
                connectionStatus.setText("Connection active");
            });
        }



        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
            Utils.toastWrapper(getActivity(), "Left channel successfully!", Toast.LENGTH_SHORT);
            fragmentActivity.runOnUiThread(() ->{
                connectionStatus.setText("Connection inactive");
            });
        }
    };
    private TextView emailText, connectionStatus;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        rtcEngine.addHandler(rtcEventHandler);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio_receive, container, false);
        emailText = view.findViewById(R.id.account_details_unpriv);
        emailText.setText("Admin:"+firebaseAuth.getCurrentUser().getEmail());
        connectionStatus = view.findViewById(R.id.connection_details_unpriv);
        ImageButton connectButton = view.findViewById(R.id.join_channel_unpriv);
        FloatingActionButton disconnectButton = view.findViewById(R.id.disconnect_fab_unpriv);
        connectButton.setOnClickListener(l -> {
            rtcEngine.setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
            rtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            rtcEngine.setAudioProfile(Constants.AUDIO_PROFILE_SPEECH_STANDARD, Constants.AUDIO_SCENARIO_DEFAULT);
            rtcEngine.joinChannel(null, getString(R.string.emergency_channel), "Sample data", 0);
        });
        disconnectButton.setOnClickListener(l -> {
            rtcEngine.leaveChannel();
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentActivity = getActivity();
    }
}
