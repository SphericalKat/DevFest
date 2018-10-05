package org.firehound.devfest;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

import static org.firehound.devfest.Utils.toastWrapper;


public class AudioBroadcastFragment extends Fragment {
    private static final String TAG = "AudioBroadcastFragment";
    private RtcEngine rtcEngine;
    private boolean isBroadcaster = true;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private TextView connectionStatus, broadcastCardText, userText;
    private EditText channelName;
    private FloatingActionButton disconnectButton;


    private IRtcEngineEventHandler rtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            Log.d(TAG, "Joined channel " + channel);
            toastWrapper(getActivity(),"Joined channel " + channel, Toast.LENGTH_SHORT);
            getActivity().runOnUiThread(() -> {
                channelName.setEnabled(false);
                connectionStatus.setText("Connection active");
                if (isBroadcaster) {
                    broadcastCardText.setText("Channel-wide broadcast");
                } else {
                    broadcastCardText.setText("Receiving mode");
                }

            });
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
            toastWrapper(getActivity(),"Left channel successfully!", Toast.LENGTH_SHORT);
            getActivity().runOnUiThread(() -> {
                connectionStatus.setText("Connection inactive");
                channelName.setEnabled(true);
            });


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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_nav, container, false);
        ImageButton connectButton = view.findViewById(R.id.join_channel);
        TextView broadcastSwitchText = view.findViewById(R.id.broadcast_type_switchtext);
        userText = view.findViewById(R.id.account_details);
        userText.setText("user: "+currentUser.getEmail());
        channelName = view.findViewById(R.id.channel_name_edittext);
        broadcastCardText = view.findViewById(R.id.broadcast_type_cardtext);
        connectionStatus = view.findViewById(R.id.connection_details);
        Switch broadcastSwitch = view.findViewById(R.id.broadcast_type_switch);
        disconnectButton = view.findViewById(R.id.disconnect_fab);


        //Listeners
        disconnectButton.setOnClickListener(v -> {
            rtcEngine.leaveChannel();
        });
        connectButton.setOnClickListener(v -> {
            String channel = channelName.getText().toString();
            if (channel.equals("")) {
                Toast.makeText(getActivity(), "Channel name cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isBroadcaster) {
                rtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            } else {
                rtcEngine.setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
            }
            rtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            rtcEngine.setAudioProfile(Constants.AUDIO_PROFILE_SPEECH_STANDARD, Constants.AUDIO_SCENARIO_DEFAULT);
            rtcEngine.joinChannel(null, channel, null, Utils.getUniqueInteger(firebaseAuth.getCurrentUser().getUid()));
        });
        broadcastSwitch.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            isBroadcaster = isChecked;
            if (isChecked) {
                broadcastSwitchText.setText(R.string.channelwide_broadcast);
            } else {
                broadcastSwitchText.setText("Receiving mode");
            }
        }));

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public void onStop() {
        super.onStop();
        RtcEngine.destroy();
        rtcEngine = null;
    }
}
