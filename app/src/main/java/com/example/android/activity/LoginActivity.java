package com.example.android.activity;

import com.example.android.classi.Ingrediente;
import com.example.android.classi.Ordine;
import com.example.android.classi.Piadina;
import com.example.android.classi.Timbro;
import com.example.android.classi.User;
import com.example.android.utility.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.example.android.R;

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

import java.util.ArrayList;
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
    OnlineHelper onlineHelper;
    Context mContext;

    final String getUserURL = "http://piadinapp.altervista.org/get_user.php";
    final String getUserBadge = "http://piadinapp.altervista.org/get_timbri.php";

    String userExternalName = "";
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mContext = this;

        //onlineHelper = new OnlineHelper();
        onlineHelper = new OnlineHelper(mContext);

        //session = new SessionManager(this);

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
            // cerco nel dB esterno: se esiste allora lo aggiungo nel DB interno e loggo.
            addUserIfInExternalDBExists(email);
            _loginButton.setEnabled(true);
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
        Timbro timbri = helper.getTimbroByEmail(utente.email);

        helper.close();

        SessionManager.createLoginSession(this,
                utente.nickname,
                utente.email,
                utente.phone,
                timbri.numberTimbri,
                timbri.numberOmaggi);

        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login fallito!", Toast.LENGTH_SHORT).show();

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

    public void addUserIfInExternalDBExists(final String emailUtente)
    {
        GenericCallback getUserCallback = new GenericCallback() {
            @Override
            public void onSuccess(JSONObject resultData)
            {
                String username = JSONHelper.getStringFromArrayObj(resultData,"user","name");
                String passwordExt = JSONHelper.getStringFromArrayObj(resultData,"user","password");
                String phoneExt = JSONHelper.getStringFromArrayObj(resultData,"user","phone");

                if(!username.isEmpty()) {
                    // l'utente esiste nel dB esterno: lo aggiungo al dB interno e loggo.
                    // se la password trovata è identica a quella immessa, allora proseguo.
                    if(passwordExt.equals(password)) {
                        User newUser = new User(0, username, password, email, phoneExt);
                        helper.insertUser(newUser);
                        // Gestione della Tessera dell'utente.
                        addUserBadgeExternalDB(email);

                        // todo: gestione della connessione lenta o assente da problemi!
                        if(!helper.existOrdersByEmail(email)){
                            //onlineHelper.getUserOrders(mContext, email);
                            getUserOrdersRequest(email);
                        }

                        // todo: gestione della connessione lenta o assente da problemi!
                        if(!helper.existMiePiadineByEmail(email)){
                            //onlineHelper.getUserPiadine(mContext, email);
                            getUserPiadineRequest(email);
                        }

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
                    } else {
                        Toast.makeText(getBaseContext(), "Password errata!", Toast.LENGTH_SHORT).show();
                        _passwordText.setError("Password errata. Riprova!");
                        _loginButton.setEnabled(true);
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Utente inesistente: ti devi prima registrare!", Toast.LENGTH_LONG).show();
                    _emailText.setError("Utente inesistente");
                    _loginButton.setEnabled(true);
                }
            }
        };

        onlineHelper.getUserData(email,getUserCallback);

        //todo delete--------------------------------------------------
        /*
        Map<String, String> params = new HashMap();
        params.put("email", emailUtente);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getUserURL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray success = response.getJSONArray("user");
                            JSONObject obj = success.getJSONObject(0);
                            String username = obj.getString("name");
                            String passwordEsterna = obj.getString("password");
                            String phoneEsterno = obj.getString("phone");
                            if(!username.isEmpty()){
                                // l'utente esiste nel dB esterno: lo aggiungo al dB interno e loggo.
                                // se la password trovata è identica a quella immessa, allora proseguo.
                                if(passwordEsterna.equals(password)) {
                                    User newUser = new User(0, username, password, email, phoneEsterno);
                                    helper.insertUser(newUser);
                                    // Gestione della Tessera dell'utente.
                                    addUserBadgeExternalDB(email);
                                    // todo: gestione della connessione lenta o assente da problemi!
                                    if(!helper.existOrdersByEmail(email)){
                                        onlineHelper.getUserOrders(mContext, email);
                                    }
                                    // todo: gestione della connessione lenta o assente da problemi!
                                    if(!helper.existMiePiadineByEmail(email)){
                                        onlineHelper.getUserPiadine(mContext, email);
                                    }
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
        });
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        */
    }

    public void addUserBadgeExternalDB(final String emailUtente){

        Map<String, String> params = new HashMap();
        params.put("email", emailUtente);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, getUserBadge, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");

                            if(success == 1) {
                                JSONArray timbro = response.getJSONArray("timbro");

                                JSONObject obj = timbro.getJSONObject(0);
                                int numeroTimbri = obj.getInt("numero_timbri");
                                int omaggiRicevuti = obj.getInt("omaggi_ricevuti");

                                Timbro newTimbro = new Timbro(0, email, numeroTimbri, omaggiRicevuti);
                                helper.insertTimbro(newTimbro);
                            }
                            else{

                                Timbro newTimbro = new Timbro(0, email, 0, 0);
                                helper.insertTimbro(newTimbro);
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
        });
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    //PRIVATE FUNCTIONS-------------------------------------------------------------
    private void getUserOrdersRequest(final String userEmail)
    {
        GenericCallback getUserOrdersCallback = new GenericCallback() {
            @Override
            public void onSuccess(JSONObject resultData) {
                boolean success = JSONHelper.getSuccessResponseValue(resultData);

                if(!success)
                    return;

                //Come usare JSONHelper?
                try {
                    JSONArray ordini = resultData.getJSONArray("ordini");
                    for(int i = 0; i < ordini.length(); i++) {
                        JSONObject ordine = ordini.getJSONObject(i);
                        String dataOrdine = ordine.getString("data_ordine");
                        String phoneOrdine = ordine.getString("user_phone");
                        String timestampOrdine = ordine.getString("timestamp_ordine");
                        double totaleOrdine = ordine.getDouble("prezzo");
                        String descrizioneOrdine = ordine.getString("descrizione");
                        ArrayList<Piadina> piadineOrdine = helper.getPiadineFromDescrizioneOrdine(descrizioneOrdine);
                        String notaOrdine = ordine.getString("nota");
                        String fasciaOrdine = ordine.getString("fascia");
                        int coloreOrdine = ordine.getInt("colore_fascia");
                        long lastUpdateOrdine = ordine.getLong("lastupdate_ordine");

                        Ordine ordineInterno = new Ordine(0, dataOrdine, userEmail, phoneOrdine, timestampOrdine,
                                totaleOrdine,piadineOrdine,notaOrdine,lastUpdateOrdine, fasciaOrdine, coloreOrdine);

                        helper.insertOrdine(ordineInterno);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        onlineHelper.getUserOrders(userEmail,getUserOrdersCallback);
    }

    private void getUserPiadineRequest(String userEmail)
    {
        GenericCallback getUserPiadineCallback = new GenericCallback() {
            @Override
            public void onSuccess(JSONObject resultData)
            {
                boolean succes = JSONHelper.getSuccessResponseValue(resultData);

                if(!succes)
                    return;

                //Come usare JSONHelper?
                try {
                    JSONArray piadine = resultData.getJSONArray("piadine");

                    long lastUpdatePiadine = JSONHelper.getLongFromObj(resultData,"timestamp");

                    for(int i = 0; i < piadine.length(); i++) {
                        JSONObject piadina = piadine.getJSONObject(i);
                        int idEsternoPiadina = piadina.getInt("id_piadina");
                        String emailUtente = piadina.getString("email_utente");
                        String nomePiadina = piadina.getString("nome");
                        String descrizionePiadina = piadina.getString("descrizione");
                        ArrayList<Ingrediente> ingredientiPiadina = helper.getIngredientiFromString(descrizionePiadina);
                        double prezzoPiadina = piadina.getDouble("prezzo");
                        String formatoPiadina = piadina.getString("formato");
                        String impastoPiadina = piadina.getString("impasto");
                        int quantitaPiadina = piadina.getInt("quantita");
                        int votoPiadina = piadina.getInt("voto");

                        Piadina piadinaVotata = new Piadina(0,nomePiadina,formatoPiadina,impastoPiadina,
                                ingredientiPiadina,prezzoPiadina,quantitaPiadina,
                                votoPiadina,idEsternoPiadina,lastUpdatePiadine);

                        helper.insertPiadinaVotata(piadinaVotata,emailUtente);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        onlineHelper.getUserPiadine(userEmail,getUserPiadineCallback);
    }
    //------------------------------------------------------------------------------
}