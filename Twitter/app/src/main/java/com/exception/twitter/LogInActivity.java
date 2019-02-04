package com.exception.twitter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class LogInActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener{
    private EditText usernameEditText, passwordEditText;
    private Button logInButton;
    private SharedPreferences sharedPreferences;


    private void logIn(View view){
        final String username = String.valueOf(usernameEditText.getText());
        final String password = String.valueOf(passwordEditText.getText());

        if (username.equals("") || password.equals("")){
            Toast.makeText(getApplicationContext(), "username/password can't be empty", Toast.LENGTH_SHORT).show();
        }else {
            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    // sign up
                    if (e == null){
                        Toast.makeText(getApplicationContext(), "Sign up Successful", Toast.LENGTH_SHORT).show();
                        logInSuccess();
                        logInBefore(true);
                    }
                    // log in
                    else if (e.getMessage().equals("Account already exists for this username.")) {
                        ParseUser.logInInBackground(username, password, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (e == null){
                                    if (user != null){
                                        Toast.makeText(LogInActivity.this, "Log in Successful", Toast.LENGTH_SHORT).show();
                                        logInSuccess();
                                        logInBefore(true);
                                    }else {
                                        Toast.makeText(LogInActivity.this, "log in Failed! try again later", Toast.LENGTH_SHORT).show();
                                        logInBefore(false);
                                    }
                                }else {
                                    Toast.makeText(LogInActivity.this, "log in Failed! error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                    logInBefore(false);
                                }
                            }
                        });
                    }// error!
                    else {
                        Toast.makeText(LogInActivity.this, "Sign up Failed, error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("sign up", e.getMessage());
                        logInBefore(false);
                    }
                }
            });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        logInButton = findViewById(R.id.logIn);

        // press enter to log in
        passwordEditText.setOnKeyListener(this);

        // hide keyboard when click outside of the text box
        RelativeLayout layout = findViewById(R.id.background);
        ImageView logo = findViewById(R.id.logo);
        layout.setOnClickListener(this);
        logo.setOnClickListener(this);

        // check if user logged in before
        sharedPreferences = this.getSharedPreferences("com.exception.twitter", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("logInBefore", false)){
            logInSuccess();
        }

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        // press enter to log in/sign up
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
            logIn(logInButton);
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        // hide keyboard when click outside of the text box
        if (v.getId() == R.id.background || v.getId() == R.id.logo){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            assert getCurrentFocus().getWindowToken() != null;
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void logInSuccess(){
        Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
        startActivity(intent);
    }

    private void logInBefore(boolean userLoggedIn){
        sharedPreferences.edit().putBoolean("logInBefore", userLoggedIn).apply();
    }
}
