package com.example.android.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.example.android.R;
import com.example.android.classi.CartItem;
import com.example.android.classi.FasciaOraria;
import com.example.android.classi.Ingrediente;
import com.example.android.classi.Ordine;
import com.example.android.classi.Piadina;
import com.example.android.activity.CartActivity;
import com.example.android.adapters.FasceOrarioAdapter;
import com.example.android.utility.CustomRequest;
import com.example.android.utility.DBHelper;
import com.example.android.utility.GenericCallback;
import com.example.android.utility.JSONHelper;
import com.example.android.utility.OnlineHelper;
import com.example.android.utility.SessionManager;
import com.example.android.utility.VolleyCallback;
import com.example.android.utility.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FasceOrarioFragment extends Fragment {

    private final static String URL_GET_FASCE = "http://piadinapp.altervista.org/get_fasce.php";
    FasceOrarioAdapter fasceOrarioAdapter;
    ArrayList<CartItem> cartItems;

    RecyclerView recyclerViewFasce;
    LinearLayout linlaHeaderProgress;
    RelativeLayout layoutFasce;
    Button buttonOrder;

    // Attributi ordine
    HashMap<String, String> utente;
    String dataRichiesta;
    int quantitaRichiesta;
    double totaleOrdine;
    ArrayList<Piadina> piadineOrdine;
    String notaOrdine;
    long lastlastUpdateOrdine;
    Ordine ordine;
    int idFasciaSelezionata;
    String orarioRitiro;

    DBHelper helper;
    ArrayList<FasciaOraria> fasceOrarie;
    Context mContext;
    VolleyCallback fasceCallBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = super.getContext();
        utente = SessionManager.getUserDetails(mContext);
        helper = new DBHelper(mContext);

        if (getArguments() != null) {
            dataRichiesta = getArguments().getString("dataRichiesta");
            quantitaRichiesta = getArguments().getInt("quantitaCarrello");
            fasceOrarie = getArguments().getParcelableArrayList("fasceOrarie");
            cartItems = getArguments().getParcelableArrayList("cartItems");
            totaleOrdine = getArguments().getDouble("totaleOrdine");
            Log.d("QUANTITA", "" + quantitaRichiesta);
        }

        linlaHeaderProgress = getActivity().findViewById(R.id.linear_spinner);
        layoutFasce = (RelativeLayout) getActivity().findViewById(R.id.layout_fasce_orari);

        getDayFasceRequest(dataRichiesta, quantitaRichiesta);

        /* FASCE ORARIE */
        fasceCallBack = new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try{
                    JSONObject response = new JSONObject(result);
                    if(response.getInt("success") == 0){
                        fasceOrarie.remove(0);
                        fasceOrarioAdapter.notifyItemRemoved(0);
                        fasceOrarioAdapter.notifyItemRangeChanged(0, fasceOrarie.size());

                        getActivity().setProgressBarIndeterminateVisibility(false);

                        fasceOrarioAdapter.notifyDataSetChanged();
                        getActivity().setProgressBarIndeterminateVisibility(false);
                        linlaHeaderProgress.setVisibility(View.GONE);
                    }else{
                        fasceOrarie.remove(0);
                        fasceOrarioAdapter.notifyItemRemoved(0);
                        fasceOrarioAdapter.notifyItemRangeChanged(0, fasceOrarie.size());

                        getActivity().setTitle("Scegli una fascia d'orario");
                        JSONArray fasce = response.getJSONArray("fasce");
                        for(int i = 0; i < fasce.length(); i++){
                            JSONObject fascia = fasce.getJSONObject(i);

                            int idFascia = fascia.getInt("id_fascia");
                            String inizioFascia = fascia.getString("inizio");
                            String fineFascia = fascia.getString("fine");
                            boolean isOccupata = fascia.getBoolean("occupata");
                            int coloreFascia = fascia.getInt("colore");

                            FasciaOraria fasciaOraria = new FasciaOraria(idFascia, inizioFascia, fineFascia, isOccupata, coloreFascia);
                            Log.d("FASCIA", fasciaOraria.toString());
                            fasceOrarie.add(fasciaOraria);
                        }

                        fasceOrarioAdapter.notifyDataSetChanged();
                        getActivity().setProgressBarIndeterminateVisibility(false);
                        linlaHeaderProgress.setVisibility(View.GONE);
                        recyclerViewFasce.setVisibility(View.VISIBLE);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onSuccessMap(int duration) {

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fasce_orarie_layout, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Scegli una fascia d'orario");*/
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

/*        android.support.v7.widget.Toolbar toolbar = getView().findViewById(R.id.fragment_fasce_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });*/

        fasceOrarie = new ArrayList<FasciaOraria>();

        recyclerViewFasce = getView().findViewById(R.id.recycler_fasce);
        recyclerViewFasce.setHasFixedSize(true);
        recyclerViewFasce.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.piadina_divider));
        recyclerViewFasce.addItemDecoration(itemDecorator);

        fasceOrarie.add(new FasciaOraria(0,"","",true,1));
        fasceOrarioAdapter = new FasceOrarioAdapter(mContext, fasceOrarie);

        recyclerViewFasce.setAdapter(fasceOrarioAdapter);

        buttonOrder = getView().findViewById(R.id.button_ordina);

        buttonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FasciaOraria fasciaSelezionata = fasceOrarioAdapter.getFasciaSelezionata();
                if(fasciaSelezionata != null){
                    idFasciaSelezionata = fasciaSelezionata.getIdFascia();
                    addNotaOrdine();
                }else{
                    Snackbar snackbar = Snackbar
                            .make(getActivity().findViewById(android.R.id.content), "Nessuna fascia selezionata! Scegli e continua!", Snackbar.LENGTH_LONG);
                    // Changing action button text color
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();
                }
            }
        });

    }

    public void addNotaOrdine(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_nota, null);
        dialogBuilder.setView(dialogView);

        final EditText editText = dialogView.findViewById(R.id.testo_nota);

        dialogBuilder.setTitle("Nota dell'ordine:");
        dialogBuilder.setIcon(R.drawable.ic_note_add_black_24dp);
        dialogBuilder.setMessage("È data la possibilità di scrivere una eventuale nota per il gestore.");
        dialogBuilder.setPositiveButton("Ok, ordina", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                notaOrdine = editText.getText().toString().trim();

                // Procediamo con l'ordine!!
                final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Evasione dell'ordine...");
                progressDialog.show();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                finishOrder();
                                progressDialog.dismiss();
                            }
                        }, 3000);
            }
        });

        dialogBuilder.setNegativeButton("Annulla ordine", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void finishOrder(){
        String emailUtente = utente.get("email");
        String telefonoUtente = utente.get("phone");
        piadineOrdine = creaPiadineOrdine(cartItems);
        lastlastUpdateOrdine = 0;

        ordine = new Ordine(0, emailUtente, telefonoUtente, dataRichiesta, totaleOrdine, piadineOrdine,
                notaOrdine, lastlastUpdateOrdine);

        // Aggiunta db esterno ed interno
        GenericCallback manageOrderCallback = new GenericCallback() {
            @Override
            public void onSuccess(JSONObject resultData)
            {
                Log.d("JSON", resultData.toString());

                boolean success = JSONHelper.getSuccessResponseValue(resultData);
                String timestamp = JSONHelper.getStringFromObj(resultData,"timestamp_fine");
                ((CartActivity) mContext).setOrarioRitiro(timestamp);

                Log.d("JSON", "success: " + success);

                if(success) {
                    addUserOrderRequest(ordine);
                } else {
                    Toast.makeText(mContext, "Oh no :(", Toast.LENGTH_SHORT).show();
                }
            }
        };

        OnlineHelper onlineHelper = new OnlineHelper(mContext);
        onlineHelper.addManageOrder(ordine,dataRichiesta,idFasciaSelezionata,quantitaRichiesta,
                emailUtente, manageOrderCallback);
        //getActivity().finish();

    }

    public ArrayList<Piadina> creaPiadineOrdine(ArrayList<CartItem> cartItems){
        ArrayList<Piadina> piadineOrdine = new ArrayList<>();

        for(CartItem item: cartItems){
            String nomePiadina = item.getNome();
            String formatoPiadina = item.getFormato();
            String impastoPiadina = item.getImpasto();
            ArrayList<Ingrediente> ingredientiPiadina = item.getIngredienti();
            double prezzoPiadina = item.getPrezzo();
            int quantitaPiadina = item.getQuantita();
            int ratingPiadina = item.getRating();

            Piadina piadina = new Piadina(nomePiadina, formatoPiadina, impastoPiadina, ingredientiPiadina,
                    prezzoPiadina, quantitaPiadina, ratingPiadina);

            piadineOrdine.add(piadina);
        }

        return piadineOrdine;
    }

    //PRIVATE FUNCTIONS------------------------------------------
    private void getDayFasceRequest(final String date, final int itemCount) {

        linlaHeaderProgress.setVisibility(View.VISIBLE);

        Map<String,String> params = new HashMap<>();
        params.put("data",date);
        params.put("quantita",String.valueOf(itemCount));

        CustomRequest jsonRequest = new CustomRequest(Request.Method.POST, URL_GET_FASCE, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("FASCE/GET", response.toString());
                        fasceCallBack.onSuccess(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError){
                            Toast.makeText(mContext, "TimeOut Error!", Toast.LENGTH_SHORT).show();
                        }else if (error instanceof NoConnectionError) {
                            getActivity().setProgressBarIndeterminateVisibility(false);
                            linlaHeaderProgress.setVisibility(View.GONE);

                            Snackbar snackbar = Snackbar
                                    .make(getActivity().findViewById(android.R.id.content), "Nessuna connessione a Internet!", Snackbar.LENGTH_LONG)
                                    .setAction("Riprova!", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            getDayFasceRequest(date, itemCount);
                                        }
                                    });

                            // Changing message text color
                            snackbar.setActionTextColor(Color.RED);

                            // Changing action button text color
                            View sbView = snackbar.getView();
                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);
                            snackbar.show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(mContext, "Authentication Error!", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(mContext, "Server Side Error!", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ParseError) {
                            error.printStackTrace();
                            Toast.makeText(mContext, "Parse Error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonRequest);
    }
    //-----------------------------------------------------------

    private void addUserOrderRequest(final Ordine ordine) {
        GenericCallback orderCallback = new GenericCallback() {
            @Override
            public void onSuccess(JSONObject resultData)
            {
                Log.d("JSON", resultData.toString());

                boolean success = JSONHelper.getSuccessResponseValue(resultData);
                String timestamp = JSONHelper.getStringFromObj(resultData,"timestamp");
                ordine.setTimestampOrdine(timestamp);

                Log.d("JSON", "success: " + success);

                if(success) {
                    Toast.makeText(mContext, "Ordine effettuato!", Toast.LENGTH_SHORT).show();
                    DBHelper helper = new DBHelper(mContext);
                    helper.insertOrdine(ordine);

                    // Notifica
                    ((CartActivity) mContext).locationAndNotification();
                    ((CartActivity) mContext).svuotaCarrello();
                } else {
                    Toast.makeText(mContext, "Oh no :(", Toast.LENGTH_SHORT).show();
                }
            }
        };

        OnlineHelper onlineHelper = new OnlineHelper(mContext);
        onlineHelper.addUserOrder(ordine,orderCallback);
    }
    //-----------------------------------------------------------

}