package com.exception.whatsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {

    ArrayList<String> userNames = new ArrayList<>();
    ArrayList<String> userPic = new ArrayList<>();
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    ListView listView;
    SimpleAdapter simpleAdapter;


    public void chatView(View view){
        allChats();
    }

    public void contactsView(View view){
        allContact();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = this.getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);  // <main_menu/> is the name of the recourse file

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:  // item on the menu
                Toast.makeText(getApplicationContext(), "new message", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.logOut:  // item on the menu
                ParseUser.logOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        userPic.add(String.valueOf(R.drawable.profile));
        listView = findViewById(R.id.listView);

        String[] from={"name","picture"};//string array
        int[] to={R.id.profileName, R.id.profilePic};//int array of views id's
        simpleAdapter = new SimpleAdapter(getApplicationContext(), arrayList, R.layout.list_view_items, from, to);//Create object and set the parameters for simpleAdapter
        listView.setAdapter(simpleAdapter);


        allChats();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(HomePageActivity.this, userNames.get(position), Toast.LENGTH_SHORT).show();
                goToChat(userNames.get(position));
            }
        });



    }

    public void allContact(){
        arrayList.clear();
        userNames.clear();
        simpleAdapter.notifyDataSetChanged();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null){
                    for (ParseUser user: objects){
                        userNames.add(user.getUsername());
                    }
                    for (int i =0; i < userNames.size(); i++) {
                        HashMap<String,String> hashMap=new HashMap<>();//create a hashmap to store the data in key value pair
                        hashMap.put("name",userNames.get(i));
                        hashMap.put("picture", userPic.get(0));
                        arrayList.add(hashMap);//add the hashmap into arrayList
                    }
                    simpleAdapter.notifyDataSetChanged();

                }else {
                    Toast.makeText(HomePageActivity.this, "Something went wrong, error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void allChats(){
        arrayList.clear();
        userNames.clear();
        simpleAdapter.notifyDataSetChanged();
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Message");
        query1.whereEqualTo("receiver", ParseUser.getCurrentUser().getUsername());

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Message");
        query2.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());

        ArrayList<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> query = ParseQuery.or(queries);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    for (ParseObject object: objects){
                        String sender = object.getString("sender");
                        if (sender.equals(ParseUser.getCurrentUser().getUsername())){
                            sender = object.getString("receiver");
                        }

                        if (!userNames.contains(sender)) {
                            userNames.add(sender);
                        }
                    }
                    for (int i =0; i < userNames.size(); i++) {
                        HashMap<String,String> hashMap=new HashMap<>();//create a hashmap to store the data in key value pair
                        hashMap.put("name",userNames.get(i));
                        hashMap.put("picture", userPic.get(0));
                        arrayList.add(hashMap);//add the hashmap into arrayList
                    }
                    simpleAdapter.notifyDataSetChanged();

                }else {
                    Toast.makeText(HomePageActivity.this, "Something went wrong, error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void goToChat(String username){
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
