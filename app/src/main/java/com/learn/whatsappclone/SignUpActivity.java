package com.learn.whatsappclone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.parse.ParseInstallation;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Save the current Installation to Back4App
        installToBack4App();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    private void installToBack4App() {
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
