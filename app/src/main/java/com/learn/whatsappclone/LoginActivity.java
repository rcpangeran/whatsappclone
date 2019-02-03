package com.learn.whatsappclone;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {
    private RelativeLayout lyoLogin_Root;
    private EditText edtLogin_Email, edtLogin_Password;
    private Button btnLogin_Login;
    private TextView txtLogin_Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        assignUI();

        // Check User already login or not
        checkUserIsAlreadyLoggedIn();

        // Call All OnClick Event Handler
        callAllOnClickEvent();

        // Call All OnKey Event Handler
        callAllOnKeyTappedEvent();
    }

    private void assignUI() {
        lyoLogin_Root = findViewById(R.id.lyoLogin_Root);
        edtLogin_Email = findViewById(R.id.edtLogin_Email);
        edtLogin_Password = findViewById(R.id.edtLogin_Password);
        btnLogin_Login = findViewById(R.id.btnLogin_Login);
        txtLogin_Register = findViewById(R.id.txtLogin_Register);
    }

    @Override
    public void onBackPressed() {
        transitionToSignUpActivity();
    }

    private void checkUserIsAlreadyLoggedIn() {
        // Check whether user already signed in or not
        if (ParseUser.getCurrentUser() != null) {
            transitionToMainActivity();
        }
    }

    private void transitionToSignUpActivity() {
        Intent signUpActivity = new Intent(this, SignUpActivity.class);
        startActivity(signUpActivity);
        finish();
    }

    private void transitionToMainActivity() {
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

    private void callAllOnKeyTappedEvent() {
        edtLogin_Password.setOnKeyListener(this);
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        switch(view.getId()) {
            case (R.id.edtLogin_Password) :
                onKey_edtLogin_Password_Enter(keyCode, event);
                break;
        }
        return false;
    }

    private void onKey_edtLogin_Password_Enter(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            onClick(btnLogin_Login);
        }
    }

    private void callAllOnClickEvent() {
        lyoLogin_Root.setOnClickListener(this);
        btnLogin_Login.setOnClickListener(this);
        txtLogin_Register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.lyoLogin_Root) :
                onClick_dismissAllUIInterface();
                break;
            case (R.id.btnLogin_Login) :
                onClick_loginUser();
                break;
            case (R.id.txtLogin_Register) :
                transitionToSignUpActivity();
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

    private void onClick_loginUser() {
        boolean isComplete = false;

        // Check all fields
        if (edtLogin_Email.getText().toString().isEmpty() ||
                edtLogin_Password.getText().toString().isEmpty()) {
            FancyToast.makeText(this,
                    "Please fill all required fields",
                    Toast.LENGTH_LONG,
                    FancyToast.INFO,
                    true)
                    .show();
        } else {
            isComplete = true;
        }

        // Login user
        if (isComplete) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Logging in...");
            dialog.show();

            ParseUser.logInInBackground(edtLogin_Email.getText().toString(),
                    edtLogin_Password.getText().toString(),
                    new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null && e == null) {
                                transitionToMainActivity();
                            } else {
                                FancyToast.makeText(LoginActivity.this,
                                        "Error : " + e.getMessage(),
                                        Toast.LENGTH_SHORT,
                                        FancyToast.ERROR,
                                        true)
                                        .show();
                            }
                            dialog.dismiss();
                        }
                    });
        }
    }
}
