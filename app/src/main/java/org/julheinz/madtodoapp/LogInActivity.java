package org.julheinz.madtodoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class LogInActivity extends AppCompatActivity {
    private static final String LOG_TAG = LogInActivity.class.getSimpleName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        //TODO: This doesn't work cause application needs a few seconds to try and reach server
        Log.d(LOG_TAG, "Offline mode? " + ((TaskApplication) getApplication()).isInOfflineMode());
        if(((TaskApplication) getApplication()).isInOfflineMode()){
            Log.i(LOG_TAG,"Offline! Skipping log in.");
            //If offline, skip login view and go to OverviewActivity
            startActivity(new Intent(this, OverviewActivity.class));
        }

    }


}
