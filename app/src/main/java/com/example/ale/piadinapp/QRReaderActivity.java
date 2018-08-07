package com.example.ale.piadinapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ale.piadinapp.classi.Timbro;
import com.example.ale.utility.DBHelper;
import com.example.ale.utility.SessionManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;

public class QRReaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrreader);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Scan button
        /*
        Button scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(this).initiateScan();
            }
        });
        */

        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        TextView resultTV = findViewById(R.id.resultTextView);

        if(result == null) {
            super.onActivityResult(requestCode,resultCode,data);
        } else {
            if(result.getContents() == null) {
                Log.d("READQR","Scan action cancelled");
                resultTV.setText("No result");
            } else {
                DBHelper helper = new DBHelper(getApplicationContext());
                Timbro timbro = helper.getTimbroByEmail(result.getContents());
                timbro.numberTimbri = 5;
                helper.updateTimbriNumber(timbro);

                SessionManager sessione = new SessionManager(this);
                final HashMap<String, String> user;
                user = sessione.getUserDetails();
                sessione.createLoginSession(user.get("name"),user.get("email"),user.get("phone"),3,0);
            }
        }
    }
}
