package com.exception.twitter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    TextView profileName;
    ArrayList<String> messages = new ArrayList<>();
    ArrayList<String> textSender = new ArrayList<>();
    ArrayList<String> time = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileName = findViewById(R.id.profile);
        profileName.setText(ParseUser.getCurrentUser().getUsername());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
        query.whereEqualTo("receiver", ParseUser.getCurrentUser().getUsername());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    for (ParseObject object: objects){
                        messages.add(object.getString("message"));
                        textSender.add(object.getString("sender"));
                        time.add(object.getCreatedAt().toString());
                    }

                    ListView simpleListView = findViewById(R.id.simpleListView);

                    ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();
                    for (int i =0; i < messages.size(); i++)
                    {
                        HashMap<String,String> hashMap=new HashMap<>();//create a hashmap to store the data in key value pair
                        hashMap.put("sender",textSender.get(i));
                        hashMap.put("tweet",messages.get(i));
                        hashMap.put("time", time.get(i));
                        arrayList.add(hashMap);//add the hashmap into arrayList
                    }
                    String[] from={"sender","tweet", "time"};//string array
                    int[] to={R.id.sender, R.id.tweet, R.id.date};//int array of views id's

                    SimpleAdapter simpleAdapter=new SimpleAdapter(getApplicationContext(), arrayList,R.layout.list_view_items,from,to);//Create object and set the parameters for simpleAdapter
                    simpleListView.setAdapter(simpleAdapter);
                    simpleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(ProfileActivity.this, position+" got it", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}
