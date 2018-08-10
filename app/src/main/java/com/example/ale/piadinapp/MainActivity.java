package com.example.ale.piadinapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.ale.utility.DBHelper;
import com.example.ale.utility.SessionManager;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.ale.piadinapp.MESSAGE";

    // Session Manager Class
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Session class instance
        //session = new SessionManager(getApplicationContext());

        //session = new SessionManager(this);
        
        // Se sei loggato, allora vai diretto nella Home Activity, senza mostrare la Main.
        /*
        if(session.loggedIn()){
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }
        */
        if(SessionManager.loggedIn(this)) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }
/*        if(!session.loggedIn()){
            logout();
        }*/

    }

    /** Called when the user taps the login button */
    public void loginActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void registerActivity(View view){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);

    }
/*
    private void logout(){
        session.setLoggedIn(false);
        finish();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }*/
}
