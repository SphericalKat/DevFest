package org.firehound.devfest;

import android.Manifest;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class MainIntroActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        //Slide 1
        addSlide(new SimpleSlide.Builder()
                .title(R.string.title_1)
                .description(R.string.desc_1)
                .background(R.color.colorAccent)
                .backgroundDark(R.color.colorAccent)
                .permissions(new String[] {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.BLUETOOTH})
                .build());


        addSlide(new FragmentSlide.Builder()
                .fragment(new IntroAuthFragment())
                .background(R.color.colorAccent)
                .backgroundDark(R.color.colorAccent)
                .build());
    }
}
