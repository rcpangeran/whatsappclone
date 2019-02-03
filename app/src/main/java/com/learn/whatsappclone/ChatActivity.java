package com.learn.whatsappclone;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private ConstraintLayout lyoChat_Root;
    private ListView lsvChat_Messages;
    private EditText edtChat_Message;
    private Button btnChat_Send;
    private ArrayList<String> chatsList;
    private ArrayAdapter adapter;
    private String selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        assignUI();
        initializeVars();

        // Get info about with whom user chat to
        chatWith();

        // Get all chat records after get data with whom user chat to
        chatRecords();

        // Call All OnClick Event Handler
        callOnClickEvent();
    }

    private void assignUI() {
        lyoChat_Root = findViewById(R.id.lyoChat_Root);
        lsvChat_Messages = findViewById(R.id.lsvChat_Messages);
        edtChat_Message = findViewById(R.id.edtChat_Message);
        btnChat_Send = findViewById(R.id.btnChat_Send);
    }

    private void initializeVars() {
        chatsList = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, chatsList);

        lsvChat_Messages.setAdapter(adapter);
    }

    private void chatWith() {
        selectedUser = getIntent().getStringExtra("selectedUser");
        FancyToast.makeText(this,
                "Chat with " + selectedUser,
                Toast.LENGTH_SHORT,
                FancyToast.INFO,
                true)
            .show();

        // Set with whom user chat to ActionBar
        this.setTitle(selectedUser);
    }

    private void chatRecords() {
        // try {
            ParseQuery<ParseObject> firstUserChatQuery = ParseQuery.getQuery("Chat");
            ParseQuery<ParseObject> secondUserChatQuery = ParseQuery.getQuery("Chat");

            firstUserChatQuery.whereEqualTo("waSender", ParseUser.getCurrentUser().getUsername());
            firstUserChatQuery.whereEqualTo("waTargetRecipient", selectedUser);

            secondUserChatQuery.whereEqualTo("waSender", selectedUser);
            secondUserChatQuery.whereEqualTo("waTargetRecipient", ParseUser.getCurrentUser().getUsername());

            ArrayList<ParseQuery<ParseObject>> allQueries = new ArrayList<>();
            allQueries.add(firstUserChatQuery);
            allQueries.add(secondUserChatQuery);

            ParseQuery<ParseObject> myQuery = ParseQuery.or(allQueries);
            myQuery.orderByAscending("createdAt");

            myQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            for (ParseObject chatObject : objects) {
                                String waMessage = chatObject.get("waMessage") + "";

                                if (chatObject.get("waSender").equals(ParseUser.getCurrentUser().getUsername())) {
                                    waMessage = ParseUser.getCurrentUser().getUsername() + ": " + waMessage;
                                }
                                if (chatObject.get("waSender").equals(selectedUser)) {
                                    waMessage = selectedUser + ": " + waMessage;
                                }

                                chatsList.add(waMessage);
                            }
                            adapter.notifyDataSetChanged();
                        } else if (objects.size() == 0) {
                            FancyToast.makeText(ChatActivity.this,
                                    "No chat",
                                    Toast.LENGTH_LONG,
                                    FancyToast.INFO,
                                    true)
                                    .show();
                        }
                    }
                }
            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void callOnClickEvent() {
        lyoChat_Root.setOnClickListener(this);
        btnChat_Send.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.lyoChat_Root) :
                onClick_dismissAllUIInterface();
                break;
            case (R.id.btnChat_Send) :
                onClick_sendMessage();
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

    private void onClick_sendMessage() {
        boolean isFilled = false;

        // Check whether message already typed before send
        if (!edtChat_Message.getText().toString().equals("")) {
            isFilled = true;
        }

        if (isFilled) {
            ParseObject chat = new ParseObject("Chat");
            chat.put("waSender", ParseUser.getCurrentUser().getUsername());
            chat.put("waTargetRecipient", selectedUser);
            chat.put("waMessage", edtChat_Message.getText().toString());

            chat.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        FancyToast.makeText(ChatActivity.this,
                                "Message from " + ParseUser.getCurrentUser().getUsername() + " sent to " + selectedUser,
                                Toast.LENGTH_SHORT,
                                FancyToast.SUCCESS,
                                true)
                                .show();
                        chatsList.add(ParseUser.getCurrentUser().getUsername() + ": " + edtChat_Message.getText().toString());
                        adapter.notifyDataSetChanged();
                        edtChat_Message.setText("");

                    }
                }
            });
        }
    }
}
