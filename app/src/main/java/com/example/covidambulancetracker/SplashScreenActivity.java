package com.example.covidambulancetracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SplashScreenActivity extends AppCompatActivity {
    private final static  int LOGIN_REQUEST_CODE=7171;
    private FirebaseAuth firebaseAuth;
    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if(firebaseAuth!=null && listener!=null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        providers= Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        firebaseAuth=FirebaseAuth.getInstance();
        listener= myFirebaseAuth -> {
            FirebaseUser user=myFirebaseAuth.getCurrentUser();
            if(user!=null){
                delaysplashscreen();
            }else{
                showLoginLayout();

            }
        };
    }

    private void showLoginLayout() {
        AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout.Builder(R.layout.layout_sigh_in).setPhoneButtonId(R.id.btn_phone_sigh_in).build();
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAuthMethodPickerLayout(authMethodPickerLayout).setIsSmartLockEnabled(false).setAvailableProviders(providers).build(),LOGIN_REQUEST_CODE);
    }

    private void delaysplashscreen() {
        Completable.timer(5, TimeUnit.SECONDS,
                AndroidSchedulers.mainThread())
                .subscribe(() -> Toast.makeText(SplashScreenActivity.this, "Covid Ambulance Tracker", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            } else {
                Toast.makeText(this, "[ERROR]: " + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}