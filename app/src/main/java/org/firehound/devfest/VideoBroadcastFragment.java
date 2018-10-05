package org.firehound.devfest;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;


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
        }
    };
    private RtcEngine rtcEngine;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;


    public VideoBroadcastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            rtcEngine = RtcEngine.create(getActivity(), getString(R.string.agora_app_id),rtcEventHandler);
        } catch (Exception e) {
           Log.e(TAG, e.toString());
        }
        rtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
        rtcEngine.enableVideo();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_nav, container, false);
        Button connectButton = view.findViewById(R.id.connect_video);
        connectButton.setOnClickListener(l ->{
            setupLocalVideo(view);
            rtcEngine.joinChannel(null, "demo", null, Utils.getUniqueInteger(currentUser.getUid()));
        });



        return view;
    }

    private void setupLocalVideo(View view){
        FrameLayout container = view.findViewById(R.id.video_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getActivity().getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        rtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, Utils.getUniqueInteger(currentUser.getUid())));
    }

    @Override
    public void onStop() {
        super.onStop();
        RtcEngine.destroy();
        rtcEngine = null;
    }
}
