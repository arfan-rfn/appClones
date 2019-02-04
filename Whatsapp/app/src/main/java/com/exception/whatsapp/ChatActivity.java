package com.exception.whatsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    EditText messageBox;
    String username;
    ArrayList<String> messageLists = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    ListView listView;

    public void sentMessage(View view){
//        Toast.makeText(this, messageBox.getText().toString(), Toast.LENGTH_SHORT).show();

        ParseObject message = new ParseObject("Message");
        message.put("receiver", username);
        message.put("message", messageBox.getText().toString());
        message.put("sender", ParseUser.getCurrentUser().getUsername());
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    messageLists.add(messageBox.getText().toString());
                    arrayAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(ChatActivity.this, "error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent input = getIntent();
        username = input.getStringExtra("username");
        setTitle(username);

        listView = findViewById(R.id.messagesList);
        messageBox = findViewById(R.id.messageText);

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, messageLists);

        listView.setAdapter(arrayAdapter);

        updateChat();

    }

    public void updateChat(){
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Message");
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Message");
        query1.whereEqualTo("sender", username);
        query2.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("receiver", ParseUser.getCurrentUser().getUsername());
        query2.whereEqualTo("receiver", username);

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.addDescendingOrder("createdBy");
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    for (ParseObject object: objects){
                        String sender = object.getString("sender");
                        if (sender.equals(username)){
                            messageLists.add(">>>  " + object.getString("message"));
                        }else {
                            messageLists.add(object.getString("message"));
                        }
                    }
                    arrayAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(ChatActivity.this, "something when wrong, error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
