package com.example.ale.piadinapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.ale.piadinapp.R;

import butterknife.BindView;

public class EditUserActivity extends AppCompatActivity {

/*    @BindView(R.id.edit_name) EditText _nameText;
    //@BindView(R.id.input_address) EditText _addressText;
    @BindView(R.id.edit_email) EditText _emailText;
    //@BindView(R.id.input_mobile) EditText _mobileText;
    @BindView(R.id.edit_oldpassword) EditText _oldPasswordText;
    @BindView(R.id.edit_password) EditText _passwordText;
    @BindView(R.id.edit_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_edit_user) Button _editUserButton;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
    }
}
