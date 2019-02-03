package com.learn.whatsappclone;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private ConstraintLayout lyoMain_Root;
    private ListView lsvMain_Users;
    private ArrayList<String> waUsers;
    private ArrayAdapter arrayAdapter;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignUI();
        initializeVars();

        // Force user to Sign Up Activity if not yet logged in to member area
        forceUserToSignUpActivityIfNotYetLoggedIn();

        // Load all users on List View
        loadListViewMain();

        // Call All OnClick Event Handler
        callAllOnClickEvent();

        // Call Swipe Refresh Event Handler
        callOnRefreshEvent();

        // Call ALl OnItemClick Event Handler
        callOnItemClickEvent();
    }

    private void assignUI() {
        lyoMain_Root = findViewById(R.id.lyoMain_Root);
        lsvMain_Users = findViewById(R.id.lsvMain_Users);
        mySwipeRefreshLayout = findViewById(R.id.swipeContainer);
    }

    private void initializeVars() {
        waUsers = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, waUsers);
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

    private void forceUserToSignUpActivityIfNotYetLoggedIn() {
        // Check whether user already signed in or not
        if (ParseUser.getCurrentUser() == null) {
            transitionToSignUpActivity();
        }
    }

    private void transitionToSignUpActivity() {
        Intent signUpActivity = new Intent(this, SignUpActivity.class);
        startActivity(signUpActivity);
        finish();
    }

    private void loadListViewMain() {
        try {
            ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
            parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

            parseQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> users, ParseException e) {
                    if (e == null) {
                        if (users.size() > 0) {
                            for (ParseUser user : users) {
                                waUsers.add(user.getUsername());
                            }
                            lsvMain_Users.setAdapter(arrayAdapter);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.logoutUserItem) :
                logOutClicked();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void logOutClicked() {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    transitionToSignUpActivity();
                } else {
                    FancyToast.makeText(MainActivity.this,
                            "Error : " + e.getMessage(),
                            Toast.LENGTH_SHORT,
                            FancyToast.ERROR,
                            true)
                        .show();
                }

            }
        });
    }

    private void callAllOnClickEvent() {
        lyoMain_Root.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.lyoMain_Root) :
                onClick_dismissAllUIInterface();
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

    private void callOnRefreshEvent() {
        mySwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        try {
            ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
            parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
            parseQuery.whereNotContainedIn("username", waUsers);

            parseQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> users, ParseException e) {
                    if (e == null) {
                        if (users.size() > 0) {
                            for (ParseUser user : users) {
                                waUsers.add(user.getUsername());
                            }
                            arrayAdapter.notifyDataSetChanged();
                            if (mySwipeRefreshLayout.isRefreshing()) {
                                mySwipeRefreshLayout.setRefreshing(false);
                            }
                        } else if (mySwipeRefreshLayout.isRefreshing()) {
                            mySwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callOnItemClickEvent() {
        lsvMain_Users.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent chatActivity = new Intent(this, ChatActivity.class);
        chatActivity.putExtra("selectedUser", waUsers.get(position));
        startActivity(chatActivity);
    }
}
