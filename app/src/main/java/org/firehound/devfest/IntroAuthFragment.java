package org.firehound.devfest;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;


public class IntroAuthFragment extends SlideFragment {


    public IntroAuthFragment() {
        // Required empty public constructor
    }
    private FirebaseAuth firebaseAuth;
    private String email, password;
    private static final String TAG = "IntroAuthFragment";
    private boolean forwardNav, backNav;


    private void createAccount() {
    }
    private void signIn() {


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        forwardNav = false;
        backNav = false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_intro_auth, container, false);
        Button signInButton = view.findViewById(R.id.sign_in);
        Button registerButton = view.findViewById(R.id.register_auth);
        TextView emailText = view.findViewById(R.id.auth_email);
        TextView passwordText = view.findViewById(R.id.auth_password);
        signInButton.setOnClickListener(v -> {
            if (emailText.getText().toString().equals("") || passwordText.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Email/Password must not be empty!", Toast.LENGTH_SHORT).show();
                return;
            } else if (passwordText.getText().toString().length() < 6) {
                Toast.makeText(getActivity(), "Password must be larger than 6 characters!", Toast.LENGTH_SHORT).show();
                return;
            }
            firebaseAuth.signInWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Sign-in successful.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Successfully signed in");
                            forwardNav = true;
                            nextSlide();
                        } else {
                            Toast.makeText(getActivity(), "Sign-in failed.", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Failed to sign-in.");
                        }
                    });
        });
        registerButton.setOnClickListener(v -> {
            if (emailText.getText().toString().equals("") || passwordText.getText().toString().equals("")){
                Toast.makeText(getActivity(), "Email/Password must not be empty!", Toast.LENGTH_SHORT).show();
                return;
            } else if (passwordText.getText().toString().length() < 6) {
                Toast.makeText(getActivity(), "Password must be larger than 6 characters!", Toast.LENGTH_SHORT).show();
                return;
            }
            firebaseAuth.createUserWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Registration successful.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Successfully created user.");
                            forwardNav = true;
                            nextSlide();
                        } else {
                            if (task.getException().getClass() == FirebaseAuthUserCollisionException.class) {
                                Toast.makeText(getActivity(), "This account already exists. Sign in instead.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Registration failed.", Toast.LENGTH_SHORT).show();
                            }
                            Log.w(TAG, "Failed to register." + task.getException());
                        }
                    });
        });
        return view;
    }

    @Override
    public boolean canGoForward() {
        return forwardNav;
    }

    @Override
    public boolean canGoBackward() {
        return backNav;
    }
}
