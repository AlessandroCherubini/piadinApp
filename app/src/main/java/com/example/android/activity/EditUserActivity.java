package com.example.android.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.android.R;
import com.example.android.classi.User;
import com.example.android.utility.DBHelper;
import com.example.android.utility.GenericCallback;
import com.example.android.utility.JSONHelper;
import com.example.android.utility.OnlineHelper;
import com.example.android.utility.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;

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
                updateUserDataEverywhere(userData.get(SessionManager.KEY_EMAIL),
                        editUsername.getText().toString(),
                        editPhone.getText().toString());
            }
        });
    }

    //PRIVATE FUNCTIONS-----------------------------------------------------------------------------
    private void updateUserDataEverywhere(String email,String username,String phone)
    {
        if(username.isEmpty() && phone.isEmpty()) {
            return;
        }

        int result;
        DBHelper helper = new DBHelper(this);
        //Get user account data
        User userAccount = helper.getUserByEmail(email);
        if(!username.isEmpty()) {
            userAccount.nickname = username;
            result = helper.updateUserName(userAccount);
            Log.d("UPDATE_USER","User name: " + username + ", rows changed: " + result);
        }

        if(!phone.isEmpty()) {
            userAccount.phone = phone;
            result = helper.updateUserPhone(userAccount);
            Log.d("UPDATE_USER","User phone: " + phone + " , rows changed: " + result);
        }

        //Update external DB
        //Setup callback
        GenericCallback userCallback = new GenericCallback() {
            @Override
            public void onSuccess(JSONObject resultData)
            {
                boolean success = JSONHelper.getSuccessResponseValue(resultData);

                if(success) {
                    Log.d("UPDATE_USER", "External DB update success!");
                } else {
                    Log.d("UPDATE_USER", "External DB update failed! " + JSONHelper.getStringFromObj(resultData,"message"));
                }
            }
        };

        //Do request
        OnlineHelper onlineHelper = new OnlineHelper(this);
        onlineHelper.updateUserInExternalDB(userAccount,userCallback);

        //Update shared prefs
        boolean res = SessionManager.updateUserData(this,username,phone);
        if(!res) {
            Log.d("UPDATE_USER","Update Shared Pref error!");
        }

        //Build result for parent activity
        Intent resultData = new Intent();
        String msg = "Success!";
        resultData.setData(Uri.parse(msg));
        setResult(RESULT_OK,resultData);

        finish();
    }
    //----------------------------------------------------------------------------------------------
}