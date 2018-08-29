package com.example.android.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.carteasy.v1.lib.Carteasy;
import com.example.android.R;
import com.example.android.classi.CartItem;
import com.example.android.classi.Ingrediente;
import com.example.android.home.CategorieIngredientiAdapter;
import com.example.android.home.IngredientsAdapter;
import com.example.android.utility.DBHelper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;


public class TabCreaPiadina extends Fragment implements IngredientsAdapter.ItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public final static Double IMPASTO_INTEGRALE = 0.30;
    public final static Double FORMATO_BABY = -1.0;
    public final static Double FORMATO_ROTOLO = 2.0;

    Context mContext;
    DBHelper helper;
    TextView prezzoPiadina;

    RadioButton rb1;
    RadioButton rb2;
    RadioButton rb3;
    RadioButton rb4;
    RadioButton rb5;

    String nomePiadina;
    String formatoPiadina;
    String impastoPiadina;
    int quantitaPiadina;
    int ratingPiadina;
    ArrayList<Ingrediente> ingredientiPiadina;

    IngredientsAdapter adapterIngredienti;
    CategorieIngredientiAdapter adapterCategorieIngredienti;

    Carteasy cs = new Carteasy();
    Map<Integer, Map> data;
    CartItem cartItem;
    String identificatore;

    double totalePiadina = 2.5;
    double totaleImpastoEFormato = 2.5;
    double totaleIngredienti = 0;

    RecyclerView ingredientiCreaPiadina;
    RecyclerView addIngredientiCreaPiadina;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TabCreaPiadina() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabCreaPiadina.
     */
    // TODO: Rename and change types and number of parameters
    public static TabCreaPiadina newInstance(String param1, String param2) {
        TabCreaPiadina fragment = new TabCreaPiadina();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = super.getContext();
        helper = new DBHelper(mContext);
        formatoPiadina = "Piadina";
        impastoPiadina = "Normale";
        ratingPiadina = 0;
        quantitaPiadina = 1;

        ingredientiPiadina = new ArrayList<>();


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crea_piadina, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(View view, int position) {
        double prezzoDaScalare = adapterIngredienti.getArrayList().get(position).getPrice();
        totaleIngredienti = totaleIngredienti - prezzoDaScalare;
        totalePiadina = totaleImpastoEFormato + totaleIngredienti;

        Toast.makeText(mContext, "totale: " + totalePiadina, Toast.LENGTH_SHORT).show();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        //EditText nomePiadina = getView().findViewById(R.id.nome_piadina_crea_piadina);

        prezzoPiadina = getView().findViewById(R.id.prezzoTotalePiadina_crea_piadina);
        setTotalePiadina(totaleImpastoEFormato + totaleIngredienti);

        rb1 = (RadioButton) getView().findViewById(R.id.rb_normale_crea_piadina);
        rb2 = (RadioButton) getView().findViewById(R.id.rb_rotolo_crea_piadina);
        rb3 = (RadioButton) getView().findViewById(R.id.rb_baby_crea_piadina);
        rb4 = (RadioButton) getView().findViewById(R.id.rb_impasto_normale_crea_piadina);
        rb5 = (RadioButton) getView().findViewById(R.id.rb_integrale_crea_piadina);

        rb1.setTypeface(null, Typeface.BOLD_ITALIC);
        rb4.setTypeface(null, Typeface.BOLD_ITALIC);

        Button buttonAggiungi = getView().findViewById(R.id.addKart_crea_piadina);
        buttonAggiungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addNomePiadina();

                //todo: da decidere dove si vuole andare.
            }
        });

        /* TODO: Gestione del recupero degli ingredienti
        if(savedInstanceState == null || !savedInstanceState.containsKey("ingredienti")) {
            ingredientiPiadina = chosenPiadina.getIngredienti();
        }
        else{
            ingredientiPiadina = savedInstanceState.getParcelableArrayList("ingredienti");
        }*/

        ingredientiCreaPiadina = getView().findViewById(R.id.ingredients_crea_piadina);
        ingredientiCreaPiadina.setHasFixedSize(true);
        ingredientiCreaPiadina.setLayoutManager(new LinearLayoutManager(mContext));

        DividerItemDecoration itemDecorator = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.piadina_divider));
        ingredientiCreaPiadina.addItemDecoration(itemDecorator);

        View homeView = getView().findViewById(android.R.id.content);
        adapterIngredienti = new IngredientsAdapter(mContext, ingredientiPiadina, this);
        ingredientiCreaPiadina.setAdapter(adapterIngredienti);
        totaleIngredienti = adapterIngredienti.getPrezzoIngredienti(ingredientiPiadina);

        addIngredientiCreaPiadina = getView().findViewById(R.id.categorie_ingredienti_crea_piadina);
        addIngredientiCreaPiadina.setHasFixedSize(true);
        addIngredientiCreaPiadina.setLayoutManager(new LinearLayoutManager(mContext));

        // Recycle View delle categorie degli ingredienti
        ArrayList<String> categorieIngredienti = helper.getCategorieIngredienti();
        // Divider Recycler Add Ingredienti
        DividerItemDecoration itemDecoratorAdd = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        itemDecoratorAdd.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.piadina_divider));
        addIngredientiCreaPiadina.addItemDecoration(itemDecoratorAdd);

        adapterCategorieIngredienti = new CategorieIngredientiAdapter(mContext, categorieIngredienti, this);
        addIngredientiCreaPiadina.setAdapter(adapterCategorieIngredienti);

        /* Gestione radio buttons */
        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        rb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        rb4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        rb5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        /* Gestione ingredienti */
        if(ingredientiPiadina.isEmpty()){
            getView().findViewById(R.id.greca_ingredienti_crea_piadina).setVisibility(View.GONE);
            getView().findViewById(R.id.ingredienti_crea_piadina).setVisibility(View.GONE);
        }else{
            getView().findViewById(R.id.greca_ingredienti_crea_piadina).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.ingredienti_crea_piadina).setVisibility(View.VISIBLE);
        }

    }

    public void onRadioButtonClicked(View v) {
        double prezzoPiadinaBase = 2.5;

        //now check which radio button is selected
        if (rb1.isChecked() && rb4.isChecked()){

            rb1.setTypeface(null, Typeface.BOLD_ITALIC);
            rb4.setTypeface(null, Typeface.BOLD_ITALIC);
            //set the other two radio buttons text style to default
            rb2.setTypeface(null, Typeface.NORMAL);
            rb5.setTypeface(null, Typeface.NORMAL);
            rb3.setTypeface(null, Typeface.NORMAL);

            formatoPiadina = "Piadina";
            impastoPiadina = "Normale";
            totaleImpastoEFormato = prezzoPiadinaBase;

            setTotalePiadina(totaleImpastoEFormato + totaleIngredienti);
        }
        else if (rb2.isChecked() && rb4.isChecked()){

            rb2.setTypeface(null, Typeface.BOLD_ITALIC);
            rb4.setTypeface(null, Typeface.BOLD_ITALIC);
            //set the other two radio buttons text style to default
            rb1.setTypeface(null, Typeface.NORMAL);
            rb5.setTypeface(null, Typeface.NORMAL);
            rb3.setTypeface(null, Typeface.NORMAL);

            formatoPiadina = "Rotolo";
            impastoPiadina = "Normale";
            totaleImpastoEFormato = prezzoPiadinaBase + FORMATO_ROTOLO;

            setTotalePiadina(totaleImpastoEFormato + totaleIngredienti);
        }
        else if (rb3.isChecked() && rb4.isChecked()){

            rb3.setTypeface(null, Typeface.BOLD_ITALIC);
            rb4.setTypeface(null, Typeface.BOLD_ITALIC);
            //set the other two radio buttons text style to default
            rb1.setTypeface(null, Typeface.NORMAL);
            rb5.setTypeface(null, Typeface.NORMAL);
            rb2.setTypeface(null, Typeface.NORMAL);

            formatoPiadina = "Baby";
            impastoPiadina = "Normale";
            totaleImpastoEFormato = prezzoPiadinaBase + FORMATO_BABY;
            setTotalePiadina(totaleImpastoEFormato + totaleIngredienti);
        }

        else if (rb1.isChecked() && rb5.isChecked()){

            rb1.setTypeface(null, Typeface.BOLD_ITALIC);
            rb5.setTypeface(null, Typeface.BOLD_ITALIC);
            //set the other two radio buttons text style to default
            rb3.setTypeface(null, Typeface.NORMAL);
            rb4.setTypeface(null, Typeface.NORMAL);
            rb2.setTypeface(null, Typeface.NORMAL);

            formatoPiadina = "Piadina";
            impastoPiadina = "Integrale";
            totaleImpastoEFormato = prezzoPiadinaBase + IMPASTO_INTEGRALE;

            setTotalePiadina(totaleImpastoEFormato + totaleIngredienti);
        }

        else if (rb2.isChecked() && rb5.isChecked()){

            rb2.setTypeface(null, Typeface.BOLD_ITALIC);
            rb5.setTypeface(null, Typeface.BOLD_ITALIC);
            //set the other two radio buttons text style to default
            rb1.setTypeface(null, Typeface.NORMAL);
            rb4.setTypeface(null, Typeface.NORMAL);
            rb3.setTypeface(null, Typeface.NORMAL);

            formatoPiadina = "Rotolo";
            impastoPiadina = "Integrale";
            totaleImpastoEFormato = prezzoPiadinaBase + FORMATO_ROTOLO + IMPASTO_INTEGRALE;

            setTotalePiadina(totaleImpastoEFormato + totaleIngredienti);
        }

        else if (rb3.isChecked() && rb5.isChecked()){

            rb3.setTypeface(null, Typeface.BOLD_ITALIC);
            rb5.setTypeface(null, Typeface.BOLD_ITALIC);

            rb1.setTypeface(null, Typeface.NORMAL);
            rb2.setTypeface(null, Typeface.NORMAL);
            rb4.setTypeface(null, Typeface.NORMAL);

            formatoPiadina = "Baby";
            impastoPiadina = "Integrale";
            totaleImpastoEFormato = prezzoPiadinaBase + FORMATO_BABY + IMPASTO_INTEGRALE;

            setTotalePiadina(totaleImpastoEFormato + totaleIngredienti);
        }

    }

    public double getTotalePiadina(){
        return totalePiadina;
    }

    public void setTotalePiadina(double nuovoTotale){
        totalePiadina = nuovoTotale;
        BigDecimal totale = new BigDecimal(totalePiadina);
        totale = totale.setScale(2,BigDecimal.ROUND_HALF_EVEN);

        prezzoPiadina.setText(totale.toPlainString().replace(".", ",") + " €");

    }

    public double getTotaleImpastoEFormato(){
        return totaleImpastoEFormato;
    }

    public double getTotaleIngredienti(){
        return totaleIngredienti;
    }

    public void setTotaleIngredienti(double nuovoTotale){
        totaleIngredienti = nuovoTotale;
    }

    private void addNomePiadina(){

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_add_nome, null);
        final EditText editText = dialogView.findViewById(R.id.testo_add_nota);

        final Button buttonMeno = dialogView.findViewById(R.id.button_quantita_meno);
        final TextView quantitaTesto = dialogView.findViewById(R.id.quantita_testo);
        final Button buttonPiu = dialogView.findViewById(R.id.button_quantita_piu);

        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setTitle("Nome e quantità della Piadina:")
                .setIcon(R.drawable.ic_impasto_tradizionale)
                .setMessage("Dai il nome alla tua piadina e scegli la quantità da ordinare!")
                .setPositiveButton("Ok, ordina", null) //Set to null. We override the onclick
                .setNegativeButton("Annulla", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        nomePiadina = editText.getText().toString().trim();

                        if(nomePiadina.isEmpty()){
                            editText.setError("Hai inserito un nome vuoto. Riprova!");

                        }else{
                            editText.setError(null);
                            aggiungiAlCarrello();
                            dialog.dismiss();
                            getActivity().finish();
                            startActivity(getActivity().getIntent());
                        }

                    }
                });

                Button buttonAnnulla = (dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                buttonAnnulla.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        quantitaPiadina = 1;
                        dialog.dismiss();
                    }
                });

                buttonMeno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(quantitaPiadina != 1) {
                            quantitaPiadina = quantitaPiadina - 1;
                            quantitaTesto.setText("Quantità: " + String.valueOf(quantitaPiadina));
                        }

                    }
                });

                buttonPiu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        quantitaPiadina = quantitaPiadina + 1;
                        quantitaTesto.setText("Quantità: " + String.valueOf(quantitaPiadina));
                    }
                });

            }
        });
        dialog.show();
    }

    private void aggiungiAlCarrello(){

        data = cs.ViewAll(mContext);
        String id;

        if (data == null || data.size() == 0) {
            id = "Piadina " + 1;
        }
        else if ((cs.get(identificatore,"nome", mContext)) != null && identificatore != null){

            id = identificatore;

        }
        else {

            int k = 0;
            for (Map.Entry<Integer, Map> entry : data.entrySet()) {
                k++;
            }

            int numero = k + 1;
            id = "Piadina " + numero;
        }

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        DecimalFormat df = new DecimalFormat("#.##", otherSymbols);
        String totaleStringa = df.format(totalePiadina * quantitaPiadina);
        double totaleTroncato = Double.valueOf(totaleStringa);

        cs.add(id,"nome", nomePiadina);
        cs.add(id, "formato", formatoPiadina);
        cs.add(id,"impasto", impastoPiadina);

        cs.add(id,"prezzo", totaleTroncato);
        cs.add(id,"ingredienti", ingredientiPiadina.toString());
        cs.add(id, "quantita", quantitaPiadina);
        cs.add(id, "rating", ratingPiadina);
        cs.add(id,"identifier", id);
        cs.commit(mContext);

        if(quantitaPiadina > 1){
            Toast.makeText(mContext, "Piadine aggiunte al carrello!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mContext, "Piadina aggiunta al carrello!", Toast.LENGTH_SHORT).show();
        }

    }

}
