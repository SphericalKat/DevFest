package org.firehound.devfest;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class AudioReceiveFragment extends Fragment {


    public AudioReceiveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio_receive, container, false);
        ImageButton connectButton = view.findViewById(R.id.join_channel_unpriv);
        FloatingActionButton disconnectButton = view.findViewById(R.id.disconnect_fab_unpriv);
        
        return view;
    }

}
