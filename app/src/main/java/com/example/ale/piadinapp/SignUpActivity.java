package com.example.ale.piadinapp;
import com.example.ale.piadinapp.classi.User;
import com.example.ale.utility.*;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_name) EditText _nameText;
    //@BindView(R.id.input_address) EditText _addressText;
    @BindView(R.id.input_email) EditText _emailText;
    //@BindView(R.id.input_mobile) EditText _mobileText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;

    String name, email, password, reEnterPassword;
    final String urlCrea = "http://piadinapp.altervista.org/create_user.php";
    private RequestQueue queue;
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        queue = Volley.newRequestQueue(this);
        session = new SessionManager(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Registrazione");

        name = _nameText.getText().toString();
        //String address = _addressText.getText().toString();
        email = _emailText.getText().toString();
        //String mobile = _mobileText.getText().toString();
        password = _passwordText.getText().toString();
        reEnterPassword = _reEnterPasswordText.getText().toString();

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creazione dell'account...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {

        DBHelper dbHelper = new DBHelper(this);

        User newUser = new User(0, name, password, email);
        dbHelper.insertUser(newUser);
        dbHelper.close();

        insertUserInExternalDB(name, password, email);

    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login Fallito!", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;


        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("Il nickname deve contenere almeno 3 caratteri");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (name.isEmpty() || name.length() > 25) {
            _nameText.setError("Il nickname deve essere al massimo di 25 caratteri");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Inserisci un indirizzo email valido");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Deve contenere dai 4 ai 10 caratteri alfanumerici");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password sbagliata. Re-inserisci la password digitata sopra");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }


    public void insertUserInExternalDB(final String nickname, final String password, final String email){
        Map<String, String> params = new HashMap();
        //nickname = nickname.replaceAll("%20"," ");
        params.put("nickname", nickname);
        params.put("password", password);
        params.put("email", email);

        JSONObject parameters = new JSONObject(params);
        Log.d("JSON", parameters.toString());

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, urlCrea, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("JSON", response.toString());
                        try{
                            String success = response.getString("success");
                            Log.d("JSON", "success: " + success);
                            if(success.equals("1")){
                                // sessione per l'utente.
                                session.setLoggedIn(true);
                                session.createLoginSession(name, email);

                                _signupButton.setEnabled(true);
                                setResult(RESULT_OK, null);

                                // redirect alla Home Activity
                                Toast.makeText(getApplicationContext(), "Registrato! Benvenuto!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                                finish();
                            }
                            else{
                                int error = response.getInt("error");
                                if(error == 1062){
                                    _emailText.setError("Indirizzo email già in uso");
                                    //valid = false;
                                }
                                Toast.makeText(getApplicationContext(), "Errore nella creazione dell'utente.", Toast.LENGTH_SHORT).show();
                                _signupButton.setEnabled(true);
                            }
                        }catch(JSONException e){
                            e.fillInStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError){
                    Toast.makeText(getApplicationContext(), "TimeOut Error!", Toast.LENGTH_SHORT).show();
                }else if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "NoConnection Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), "Authentication Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Server Side Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), "Parse Error!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        queue.add(jsObjRequest);
    }
}
