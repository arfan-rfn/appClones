package com.exception.whatsapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity implements View.OnKeyListener{
    Button loginButton, signupButton;
    EditText usernameText, passwordText, emailText;

    public void logIn(View view){
        signupButton.setTextColor(Color.GRAY);
        loginButton.setTextColor(Color.WHITE);
        emailText.setVisibility(View.INVISIBLE);

    }

    public void signUp(View view){
        loginButton.setTextColor(Color.GRAY);
        signupButton.setTextColor(Color.WHITE);
        emailText.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        loginButton = findViewById(R.id.login);
        signupButton = findViewById(R.id.signup);

        usernameText = findViewById(R.id.username);
        passwordText = findViewById(R.id.password);
        emailText = findViewById(R.id.email);

        passwordText.setOnKeyListener(this);
        emailText.setOnKeyListener(this);

        if (ParseUser.getCurrentUser() != null){
            Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        String email = emailText.getText().toString();

        ParseUser user = new ParseUser();

        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
            if (v.getId() == R.id.password) {
                if (username.equals("") || password.equals("")){
                    Toast.makeText(this, "Username/Password can't be empty", Toast.LENGTH_SHORT).show();
                }else {
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (e == null){
                                Toast.makeText(LoginActivity.this, "Log In Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(LoginActivity.this, "Log In failed, error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }else if (v.getId() == R.id.email){
                if (username.equals("") || password.equals("") || email.equals("")){
                    Toast.makeText(this, "Username/Password/Email can't be empty", Toast.LENGTH_SHORT).show();
                }else {
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(email);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                Toast.makeText(LoginActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(LoginActivity.this, "Sign up Failed, Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }
        return false;
    }
}
