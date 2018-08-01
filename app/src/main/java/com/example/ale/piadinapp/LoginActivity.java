package com.example.ale.piadinapp;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.ale.piadinapp.classi.User;
import com.example.ale.utility.*;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
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
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;

    String email, password;
    DBHelper helper;
    final String getUserURL = "http://piadinapp.altervista.org/get_user.php";
    String userExternalName = "";
    //boolean wait = true;
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        session = new SessionManager(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        helper = new DBHelper(this);
        User internalUser = helper.getUserByEmail(email);
        Log.d("UTENTE/PASSWORD1", "password interna: " + internalUser.password);
        Log.d("UTENTE/PASSWORD2", "password immessa: " + password);
        Log.d("UTENTE/EMAIL1", "email interna: " + internalUser.email);
        Log.d("UTENTE/EMAIL2", "email inserita: " + email);

        Log.d("UTENTE", internalUser.toString());

        // controllo che l'utente esista nel dB interno.
        if(!internalUser.password.isEmpty()){
            // L'utente esiste ma la password inserita è errata!
            if(!internalUser.password.equals(password)){
                Toast.makeText(getBaseContext(), "Login fallito! Password errata!", Toast.LENGTH_LONG).show();
                _passwordText.setError("Password errata");
                _loginButton.setEnabled(true);
                return;
            }else{
                // Tutto ok! email e password corretti! Procediamo con il login!
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Autenticazione in corso...");
                progressDialog.show();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                onLoginSuccess();
                                // onLoginFailed();
                                progressDialog.dismiss();
                            }
                        }, 3000);
            }
        }else{
            // Utente inesistente!
            Log.d("UTENTE/INESISTENTE", "Utente inesistente nel dB interno!");
            // cerco nel dB esterno: se esiste allora lo aggiungo nel DB interno e loggo.
            addUserIfInExternalDBExists(email);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);

        User utente = helper.getUserByEmail(_emailText.getText().toString());
        Log.d("UTENTE/CREDENTIANLS", utente.nickname + " " + utente.email);

        helper.printLoginsTable();
        helper.close();

        session.setLoggedIn(true);
        session.createLoginSession(utente.nickname, utente.email, utente.phone);

        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login fallito!", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Indirizzo email non valido");
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

        return valid;
    }

    public void addUserIfInExternalDBExists(final String emailUtente){

        Map<String, String> params = new HashMap();
        params.put("email", emailUtente);

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject parameters = new JSONObject(params);
        Log.d("JSON", parameters.toString());

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getUserURL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("UTENTE/CONTROLLO_UTENTE", response.toString());
                        try{
                            Log.d("UTENTE/DENTRO", "Sono dentro il try");
                            JSONArray success = response.getJSONArray("user");

                            Log.d("UTENTE/DENTRO", success.toString());
                            JSONObject obj = success.getJSONObject(0);
                            String username = obj.getString("name");
                            String passwordEsterna = obj.getString("password");
                            String phoneEsterno = obj.getString("phone");

                            Log.d("UTENTE/NomeEsterno", username);

                            Log.d("UTENTE/DBESTERNO", "Ho cercato nel dB esterno l'utente");

                            if(!username.isEmpty()){
                                // l'utente esiste nel dB esterno: lo aggiungo al dB interno e loggo.
                                // se la password trovata è identica a quella immessa, allora proseguo.
                                if(passwordEsterna.equals(password)) {

                                    User newUser = new User(0, username, password, email, phoneEsterno);
                                    helper.insertUser(newUser);
                                    Log.d("UTENTE/DBINTERNO", "Ho aggiunto l'utente al db interno perché esiste nel db esterno");
                                    // Procediamo con il login!
                                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                                            R.style.AppTheme_Dark_Dialog);
                                    progressDialog.setIndeterminate(true);
                                    progressDialog.setMessage("Autenticazione in corso...");
                                    progressDialog.show();

                                    new android.os.Handler().postDelayed(
                                            new Runnable() {
                                                public void run() {
                                                    // On complete call either onLoginSuccess or onLoginFailed
                                                    onLoginSuccess();
                                                    // onLoginFailed();
                                                    progressDialog.dismiss();
                                                }
                                            }, 3000);
                                }else{
                                    Toast.makeText(getBaseContext(), "Password errata!", Toast.LENGTH_SHORT).show();
                                    _passwordText.setError("Password errata. Riprova!");
                                    _loginButton.setEnabled(true);
                                    return;
                                }
                            }else{
                                Toast.makeText(getBaseContext(), "Utente inesistente: ti devi prima registrare!", Toast.LENGTH_LONG).show();
                                _emailText.setError("Utente inesistente");
                                _loginButton.setEnabled(true);
                                return;
                            }

                        }catch(JSONException e){
                            e.printStackTrace();
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
        }) {
            @Override
            public Request.Priority getPriority() {
                return Priority.IMMEDIATE;
            }
        };

        queue.add(jsObjRequest);

    }

}
