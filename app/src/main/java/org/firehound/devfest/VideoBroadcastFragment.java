package org.firehound.devfest;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

import static org.firehound.devfest.MainActivity.rtcEngine;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoBroadcastFragment extends Fragment {
    private static final String TAG = "VideoBroadcastFragment";
    private IRtcEngineEventHandler rtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            Log.d(TAG, "Joined channel " + channel);
            Utils.toastWrapper(getActivity(),"Joined channel " + channel, Toast.LENGTH_SHORT);
        }



        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
            Utils.toastWrapper(getActivity(), "Left channel successfully!", Toast.LENGTH_SHORT);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            Utils.toastWrapper(getActivity(), "A user has joined the channel.", Toast.LENGTH_SHORT);
            UID = uid;
        }
    };
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FragmentActivity fragmentActivity;
    private int UID;


    public VideoBroadcastFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rtcEngine.addHandler(rtcEventHandler);
        rtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
        //rtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        rtcEngine.enableVideo();
        setVideoProfile();
        rtcEngine.switchCamera();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_nav, container, false);
        FloatingActionButton connectButton = view.findViewById(R.id.connect_video);
        FloatingActionButton disconnectButton = view.findViewById(R.id.disconnect_video);
        FrameLayout videoContainer = view.findViewById(R.id.video_container);
        disconnectButton.setOnClickListener(l -> {
            rtcEngine.leaveChannel();
            videoContainer.removeAllViews();
        });
        connectButton.setOnClickListener(l ->{
            SurfaceView surfaceView = RtcEngine.CreateRendererView(getActivity().getBaseContext());
            surfaceView.setZOrderMediaOverlay(true);
            videoContainer.addView(surfaceView);
            rtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));
            rtcEngine.joinChannel(null, "1000", "Extra data", 0);
        });



        return view;
    }

    private void setVideoProfile() {
        VideoEncoderConfiguration.ORIENTATION_MODE orientationMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
        VideoEncoderConfiguration.VideoDimensions dimensions = new VideoEncoderConfiguration.VideoDimensions(1080, 1920);
        VideoEncoderConfiguration videoEncoderConfiguration = new VideoEncoderConfiguration(dimensions, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15, VideoEncoderConfiguration.STANDARD_BITRATE, orientationMode);
        rtcEngine.setVideoEncoderConfiguration(videoEncoderConfiguration);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentActivity = getActivity();
    }
}
