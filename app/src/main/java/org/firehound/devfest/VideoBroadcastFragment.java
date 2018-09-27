package org.firehound.devfest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.agora.rtc.IRtcEngineEventHandler;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoBroadcastFragment extends Fragment {
    private static final String TAG = "VideoBroadcastFragment";
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


    public VideoBroadcastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_nav, container, false);
    }

}
