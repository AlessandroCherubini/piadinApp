package com.example.ale.piadinapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.User;
import com.example.ale.utility.DBHelper;
import com.example.ale.utility.SessionManager;

import java.util.HashMap;

import butterknife.BindView;

public class EditUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        Toolbar toolbar = findViewById(R.id.toolbar);

        //Get user informations
        final EditText editUsername = (EditText) findViewById(R.id.username_edit);
        final EditText editPhone = (EditText) findViewById(R.id.phone_edit);
        final HashMap<String,String> userData = SessionManager.getUserDetails(this);
        if(userData != null) {
            //Username hint
            editUsername.setHint(userData.get(SessionManager.KEY_NAME));
            //Phone hint
            editPhone.setHint(userData.get(SessionManager.KEY_PHONE));
        } else {
            editUsername.setHint("Nome Cognome");
            editPhone.setHint("1234567890");
        }

        //Edit confirm button
        Button editButton = (Button) findViewById(R.id.btn_edit_user_confirm);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int result;
                DBHelper helper = new DBHelper(EditUserActivity.this);
                //Get user account data
                User userAccount = helper.getUserByEmail(userData.get(SessionManager.KEY_EMAIL));
                if(!editUsername.getText().toString().isEmpty()) {
                    userAccount.nickname = editUsername.getText().toString();
                    result = helper.updateUserName(userAccount);
                    Log.d("UPDATE_USER","User name: " + editUsername.getText().toString() + ", rows changed: " + result);
                }

                if(!editPhone.getText().toString().isEmpty()) {
                    userAccount.phone = editPhone.getText().toString();
                    result = helper.updateUserPhone(userAccount);
                    Log.d("UPDATE_USER","User phone: " + editPhone.getText().toString() + " , rows changed: " + result);
                }

                //todo update external DB and Shared Pref
            }
        });
    }
}
