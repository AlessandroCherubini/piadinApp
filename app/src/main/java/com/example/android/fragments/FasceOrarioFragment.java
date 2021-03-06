package com.example.android.fragments;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
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
import com.carteasy.v1.lib.Carteasy;
import com.example.android.R;
import com.example.android.classi.CartItem;
import com.example.android.classi.FasciaOraria;
import com.example.android.classi.Ingrediente;
import com.example.android.classi.Ordine;
import com.example.android.classi.Piadina;
import com.example.android.activity.CartActivity;
import com.example.android.adapters.FasceOrarioAdapter;
import com.example.android.services.NotificationService;
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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class FasceOrarioFragment extends Fragment {

    private final static String URL_GET_FASCE = "http://piadinapp.altervista.org/get_fasce.php";
    FasceOrarioAdapter fasceOrarioAdapter;
    ArrayList<CartItem> cartItems;

    RecyclerView recyclerViewFasce;
    LinearLayout linlaHeaderProgress;
    RelativeLayout layoutFasce;
    Button buttonOrder;
    Button tempoFascia;

    // Attributi ordine
    HashMap<String, String> utente;
    ArrayList<FasciaOraria> fasceOrarie;
    String dataRichiesta;
    int quantitaRichiesta;
    double totaleOrdine;
    ArrayList<Piadina> piadineOrdine;
    String notaOrdine;
    long lastlastUpdateOrdine;
    Ordine ordine;
    FasciaOraria fasciaSelezionata;
    String fasciaOrariaString;
    int idFasciaSelezionata;
    int coloreFasciaSelezionata;
    boolean setNotification;

    private android.support.v7.widget.Toolbar toolbarFragment;
    private CartActivity cartActivity;

    DBHelper helper;
    Context mContext;
    VolleyCallback fasceCallBack;

    public FasceOrarioFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        cartActivity = ((CartActivity) context);
    }

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

                        //getActivity().setTitle("Scegli una fascia d'orario");
                        JSONArray fasce = response.getJSONArray("fasce");
                        for(int i = 0; i < fasce.length(); i++){
                            JSONObject fascia = fasce.getJSONObject(i);

                            int idFascia = fascia.getInt("id_fascia");
                            String inizioFascia = fascia.getString("inizio");
                            String fineFascia = fascia.getString("fine");
                            boolean isOccupata = fascia.getBoolean("occupata");
                            int coloreFascia = fascia.getInt("colore");

                            FasciaOraria fasciaOraria = new FasciaOraria(idFascia, inizioFascia, fineFascia, isOccupata, coloreFascia);
                            fasceOrarie.add(fasciaOraria);
                        }

                        fasceOrarioAdapter.notifyDataSetChanged();
                        recyclerViewFasce.swapAdapter(fasceOrarioAdapter, true);
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
        View v = inflater.inflate(R.layout.fasce_orarie_layout, container, false);
        toolbarFragment = (android.support.v7.widget.Toolbar) v.findViewById(R.id.toolbar123);
        setupToolbar();

        return v;
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
        fasceOrarie = new ArrayList<>();

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
                fasciaSelezionata = fasceOrarioAdapter.getFasciaSelezionata();
                if(fasciaSelezionata != null){
                    idFasciaSelezionata = fasciaSelezionata.getIdFascia();
                    fasciaOrariaString = fasciaSelezionata.getInizioFascia() + " - " + fasciaSelezionata.getFineFascia();
                    coloreFasciaSelezionata = fasciaSelezionata.getColoreBadge();

                    addInfoOrder();
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

        final Button infoButton = getView().findViewById(R.id.info_fasce);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fasceOrarioAdapter.infoButtonFasce(infoButton);
            }
        });


    }

    public void addInfoOrder(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_nota, null);
        dialogBuilder.setView(dialogView);

        final Switch switchNota = dialogView.findViewById(R.id.switch_nota);
        final EditText editText = dialogView.findViewById(R.id.testo_nota);
        final TextInputLayout textInputLayout = dialogView.findViewById(R.id.text_input_layout);

        final Switch switchNotification = dialogView.findViewById(R.id.switch_notification);
        final RelativeLayout infoLayoutNotification = dialogView.findViewById(R.id.info_layout_notification);
        final TextView infoTextNotification = dialogView.findViewById(R.id.info_text_notification);

        infoTextNotification.setText("Questo servizio offre la possibilità di ricevere una notifica " +
                "che ti avvisa quando dovrai partire per ritirare l'ordine.\n" +
                "E' richiesto il permesso di geolocalizzazione.");

        setNotification = SessionManager.getNotificationOption(mContext);
        switchNotification.setChecked(setNotification);

        if(setNotification == true){
            infoLayoutNotification.setVisibility(View.VISIBLE);
        }else{
            infoLayoutNotification.setVisibility(View.GONE);
        }

        switchNota.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    textInputLayout.setVisibility(View.VISIBLE);
                }else{
                    textInputLayout.setVisibility(View.GONE);
                }
            }
        });

        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    infoLayoutNotification.setVisibility(View.VISIBLE);
                    setNotification = true;
                }else{
                    infoLayoutNotification.setVisibility(View.GONE);
                    setNotification = false;
                }
            }
        });

        dialogBuilder.setTitle("Opzioni Aggiuntive:");
        //dialogBuilder.setIcon(R.drawable.ic_note_add_black_24dp);
        //dialogBuilder.setMessage("È data la possibilità di scrivere una eventuale nota per il gestore.");
        dialogBuilder.setPositiveButton("Ok, ordina", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                notaOrdine = editText.getText().toString().trim();
                SessionManager.saveNotificationOption(mContext, setNotification);

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
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        DecimalFormat df = new DecimalFormat("#.##", otherSymbols);

        String totaleStringa = df.format(totaleOrdine);
        double totaleTroncato = Double.valueOf(totaleStringa);

        ordine = new Ordine(0, dataRichiesta, emailUtente, telefonoUtente, "", totaleTroncato, piadineOrdine,
                notaOrdine, lastlastUpdateOrdine, fasciaOrariaString, coloreFasciaSelezionata);

        // Aggiunta db esterno ed interno
        GenericCallback manageOrderCallback = new GenericCallback() {
            @Override
            public void onSuccess(JSONObject resultData)
            {
                Log.d("JSON", resultData.toString());

                boolean success = JSONHelper.getSuccessResponseValue(resultData);
                Log.d("JSON", "success: " + success);

                if(success) {
                    String orarioRitiro = JSONHelper.getStringFromObj(resultData,"timestamp_fine");
                    int manageID = JSONHelper.getIntFromObj(resultData, "manage");
                    ((CartActivity) mContext).setOrarioRitiro(orarioRitiro);
                    addUserOrderRequest(ordine, manageID);
                } else {
                    Toast.makeText(mContext, "Oh no :(", Toast.LENGTH_SHORT).show();
                }
            }
        };

        OnlineHelper onlineHelper = new OnlineHelper(mContext);
        onlineHelper.addManageOrder(ordine, dataRichiesta, idFasciaSelezionata, quantitaRichiesta,
                emailUtente, manageOrderCallback);

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

                            getActivity().setProgressBarIndeterminateVisibility(false);
                            linlaHeaderProgress.setVisibility(View.GONE);

                            Snackbar snackbar = Snackbar
                                    .make(getActivity().findViewById(android.R.id.content), "Errore nella richiesta!", Snackbar.LENGTH_LONG)
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

    private void addUserOrderRequest(final Ordine ordine, int manageID) {
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

                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String inputDateStr = dataRichiesta;
                    Date date = null;
                    try {
                        date = inputFormat.parse(inputDateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String outputDateStr = outputFormat.format(date);
                    ordine.setDataOrdine(outputDateStr);
                    helper.insertOrdine(ordine);

                    ((CartActivity) mContext).svuotaCarrello();

                    // Notifica
                    if(setNotification){
                        Log.d("ALARM", "Nuovo servizio");
                        ((CartActivity) mContext).locationAndNotification();
                    }else{
                        ((CartActivity) mContext).finish();
                    }


                } else {
                    Toast.makeText(mContext, "Oh no :(", Toast.LENGTH_SHORT).show();
                }
            }
        };

        OnlineHelper onlineHelper = new OnlineHelper(mContext);
        onlineHelper.addUserOrder(ordine, manageID, orderCallback);
    }
    //-----------------------------------------------------------

    private void setupToolbar(){
        toolbarFragment.setTitle("Scegli una fascia oraria");
        cartActivity.getSupportActionBar().hide();
        cartActivity.setSupportActionBar(toolbarFragment);
    }

}