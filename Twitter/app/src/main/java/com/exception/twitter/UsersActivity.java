package com.exception.twitter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity{
    Button logOutButton;
    ImageButton tweetButton;
    ListView userListView;
    ArrayList<String> userList = new ArrayList<>();
    ArrayList<String> checkedUser = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    String currReceiver = "";

    public void newTweet(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        alert.setTitle("Send a Tweet...");

        alert.setView(edittext);

        alert.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String tweet = edittext.getText().toString();
                sendTweet(tweet, checkedUser);
            }
        });

        alert.setNegativeButton("cancel", null);

        alert.show();

    }


    public void logOut(View view){
        ParseUser.logOut();
        Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.exception.twitter", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("logInBefore", false).apply();
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        logOutButton = findViewById(R.id.logOut);
        tweetButton = findViewById(R.id.newTweet);

        userListView = findViewById(R.id.usersListView);
        userListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        userList.add("user list loading...");
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, userList);
        userListView.setAdapter(arrayAdapter);

        // find the user list on parse
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null){
                    userList.clear();
                    if (objects.size()>0) {
                        for (ParseUser user : objects) {  // add users to the list
                            userList.add(user.getUsername());
                        }
                    }else {  // if no user found on the database
                        userList.add("no followers");
                    }
                    arrayAdapter.notifyDataSetChanged();
                }else {
                    Log.i("User list", "error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Something went wrong, Please try again later. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SparseBooleanArray sp = userListView.getCheckedItemPositions();
                checkedUser.clear();
                for(int i=0;i<sp.size();i++) {
                    if (sp.valueAt(i)) {
                        checkedUser.add(userList.get(sp.keyAt(i)));
                    }
                }
            }
        });

        RelativeLayout background = findViewById(R.id.background);

        userListView.setOnTouchListener(new OnSwipeTouchListener(UsersActivity.this) {
            @Override
            public void onSwipeLeft() {
                Toast.makeText(UsersActivity.this, "left", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }

        });

    }

    public  void sendTweet(String tweet, ArrayList<String> receiver){
        if (receiver.size() > 0){
            for (String rcv: receiver){
                ParseObject object = new ParseObject("Message");
                object.put("sender", ParseUser.getCurrentUser().getUsername());
                object.put("message", tweet);
                object.put("receiver", rcv);
                currReceiver = rcv;
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            Toast.makeText(UsersActivity.this, "Tweet sent", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(UsersActivity.this, "unable to sent tweet to " + currReceiver+", please try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }else {
            Toast.makeText(getApplicationContext(), "No user checked, No tweet sent! Check users first.", Toast.LENGTH_SHORT).show();
        }
    }

}
