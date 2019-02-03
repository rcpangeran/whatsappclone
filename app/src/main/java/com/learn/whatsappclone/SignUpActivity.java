package com.learn.whatsappclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{
    private RelativeLayout layoutSignUp_Root;
    private EditText edtSignUp_Email, edtSignUp_Username, edtSignUp_Password;
    private Button btnSignUp_Register;
    private TextView txtSignUp_Login;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Save the current Installation to Back4App
        installToBack4App();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        assignUI();

        // Check User already login or not
        checkUserIsAlreadyLoggedIn();

        // Call All OnClick Event Handler
        callAllOnClickEvent();

        // Call All OnKeyTapped Event Handler
        callAllOnKeyTappedEvent();
    }

    private void installToBack4App() {
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    private void assignUI() {
        layoutSignUp_Root = findViewById(R.id.layoutSignUp_Root);
        edtSignUp_Email = findViewById(R.id.edtSignUp_Email);
        edtSignUp_Username = findViewById(R.id.edtSignUp_Username);
        edtSignUp_Password = findViewById(R.id.edtSignUp_Password);
        btnSignUp_Register = findViewById(R.id.btnSignUp_Register);
        txtSignUp_Login = findViewById(R.id.txtSignUp_Login);
    }

    private void checkUserIsAlreadyLoggedIn() {
        // Check whether user already signed in or not
        if (ParseUser.getCurrentUser() != null) {
            transitionToMainActivity();
        }
    }

    @Override
    public void onBackPressed() {
        Toast mToast = null;
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            mToast.cancel();
            super.onBackPressed();
            return;
        } else {
            mToast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    private void transitionToLoginActivity() {
        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }

    private void transitionToMainActivity() {
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

    private void callAllOnKeyTappedEvent() {
        edtSignUp_Password.setOnKeyListener(this);
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        switch (view.getId()) {
            case (R.id.edtSignUp_Password) :
                onKey_edtSignUp_Password_Enter(keyCode, event);
                break;
        }

        return false;
    }

    private void onKey_edtSignUp_Password_Enter(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            onClick(btnSignUp_Register);
        }
    }

    private void callAllOnClickEvent() {
        layoutSignUp_Root.setOnClickListener(this);
        btnSignUp_Register.setOnClickListener(this);
        txtSignUp_Login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.layoutSignUp_Root) :
                onClick_dismissAllUIInterface();
                break;
            case (R.id.btnSignUp_Register) :
                onClick_createNewUser();
                break;
            case (R.id.txtSignUp_Login) :
                transitionToLoginActivity();
                break;
        }
    }

    private void onClick_dismissAllUIInterface() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onClick_createNewUser() {
        boolean isComplete = false;

        // Check all fields
        if (edtSignUp_Email.getText().toString().equals("") ||
                edtSignUp_Username.getText().toString().equals("") ||
                edtSignUp_Password.getText().toString().equals("")) {
            FancyToast.makeText(this,
                    "Please fill all required fields",
                    Toast.LENGTH_SHORT,
                    FancyToast.INFO,
                    true)
                    .show();
        } else {
            isComplete = true;
        }

        // Register new user
        if (isComplete) {
            ParseUser newUser = new ParseUser();
            newUser.setEmail(edtSignUp_Email.getText().toString());
            newUser.setUsername(edtSignUp_Username.getText().toString());
            newUser.setPassword(edtSignUp_Password.getText().toString());

            final ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
            dialog.setMessage("Signing up " + edtSignUp_Username.getText().toString());
            dialog.show();

            newUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    dialog.dismiss();
                    if (e == null) {
                        transitionToMainActivity();
                    } else {
                        FancyToast.makeText(SignUpActivity.this,
                                "Error : " + e.getMessage(),
                                Toast.LENGTH_SHORT,
                                FancyToast.ERROR,
                                true)
                                .show();
                    }
                }
            });
        }
    }
}
