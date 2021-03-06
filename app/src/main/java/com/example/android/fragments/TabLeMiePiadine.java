package com.example.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.R;
import com.example.android.adapters.LeMiePiadineNonVotateAdapter;
import com.example.android.classi.Ordine;
import com.example.android.classi.PiadinApp;
import com.example.android.classi.Piadina;
import com.example.android.adapters.LeMiePiadineAdapter;
import com.example.android.utility.DBHelper;
import com.example.android.utility.GenericCallback;
import com.example.android.utility.JSONHelper;
import com.example.android.utility.OnlineHelper;
import com.example.android.utility.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;


public class TabLeMiePiadine extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList<Piadina> piadineVotate;
    ArrayList<Ordine> ordiniUtente;
    ArrayList<Piadina> piadineNonVotate;
    LeMiePiadineAdapter adapterPiadineVotate;
    LeMiePiadineNonVotateAdapter adapterPiadineNonVotate;
    Map<String, String> utente;
    String emailUtente;
    Context mContext;
    DBHelper helper;
    OnlineHelper onlineHelper;

    TextView testoPiadineVuote;
    TextView testoPiadineVuoteNonVotate;

    //the recyclerview
    RecyclerView recyclerViewPiadineVotate;
    RecyclerView recyclerViewPiadineNonVotate;


    private OnFragmentInteractionListener mListener;

    public TabLeMiePiadine() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabLeMiePiadine.
     */
    // TODO: Rename and change types and number of parameters
    public static TabLeMiePiadine newInstance(String param1, String param2) {
        TabLeMiePiadine fragment = new TabLeMiePiadine();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        utente = SessionManager.getUserDetails(mContext);
        helper = new DBHelper(mContext);
        onlineHelper = new OnlineHelper(mContext);

        emailUtente = utente.get("email");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_mie_piadine, container, false);
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

    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        testoPiadineVuote = getView().findViewById(R.id.text_voto_piadina);
        testoPiadineVuoteNonVotate = getView().findViewById(R.id.text_ordini_vuoti_classifica);

        // Spiegazione Pagina
        final Button infoButton = getView().findViewById(R.id.info_le_mie_piadine);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialShowcaseView.Builder((Activity) mContext).withCircleShape()
                        .setTarget(infoButton)
                        .setTitleText("Informazioni")
                        .setDismissText("OK! HO CAPITO!")
                        .setDismissOnTouch(true)
                        .setContentTextColor(Color.parseColor("#ffffff"))
                        .setContentText("Vota le piadine preferite per avercele sempre a portata di mano!" +
                                "\nLa classifica è totalmente privata e personale." +
                                "\nTrascina verso sinistra o destra le piadine votate per toglierle dalla classifica.")
                        .setDelay(200)
                        .useFadeAnimation()
                        .show();
            }
        });

        // La Mia Classifica RecyclerView
        recyclerViewPiadineVotate = getView().findViewById(R.id.recycler_miepiadine);
        recyclerViewPiadineVotate.setHasFixedSize(true);
        recyclerViewPiadineVotate.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.piadina_divider));
        recyclerViewPiadineVotate.addItemDecoration(itemDecorator);

        // Inizializzo le due classifiche
        piadineVotate = helper.getLeMiePiadineByEmail(emailUtente);
        riordinaClassifica();
        ordiniUtente = helper.getOrdiniByEmail(emailUtente);
        piadineNonVotate = getPiadineNonVotate(piadineVotate, ordiniUtente);

        //creating recyclerview adapter
        adapterPiadineVotate = new LeMiePiadineAdapter(getActivity(), piadineVotate, this);
        //setting adapter to recyclerview
        recyclerViewPiadineVotate.setAdapter(adapterPiadineVotate);


        // Eliminazione delle piadine dalla Classifica
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                final int posizione = viewHolder.getAdapterPosition();
                final Piadina piadinaRimossa = piadineVotate.get(posizione);
                piadinaRimossa.setRating(0);
                piadineVotate.remove(posizione);

                GenericCallback callback = new GenericCallback() {
                    @Override
                    public void onSuccess(JSONObject resultData) {

                        boolean success = JSONHelper.getSuccessResponseValue(resultData);
                        if(success){
                            helper.deletePiadinaVotata(piadinaRimossa.getIdEsterno());
                        }else{
                            Log.d("ERRORE", resultData.toString());
                        }
                    }
                };
                onlineHelper.deleteRatedPiadinaInExternalDB(piadinaRimossa.getIdEsterno(), callback);

                adapterPiadineVotate.notifyItemRemoved(viewHolder.getAdapterPosition());
                recyclerViewPiadineVotate.swapAdapter(adapterPiadineVotate,true);

                piadineNonVotate.add(piadinaRimossa);
                adapterPiadineNonVotate.notifyItemInserted(piadineNonVotate.size() - 1);
                recyclerViewPiadineNonVotate.swapAdapter(adapterPiadineNonVotate, true);

                setEmptyMessage();
                Snackbar.make(recyclerViewPiadineVotate, "Elemento rimosso dalla classifica!", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewPiadineVotate);

        // Le Piadine Non Votate RecyclerView
        recyclerViewPiadineNonVotate = getView().findViewById(R.id.recyler_piadine_non_votate);
        recyclerViewPiadineNonVotate.setHasFixedSize(true);
        recyclerViewPiadineNonVotate.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration itemDecoratorOrdini = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecoratorOrdini.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.piadina_divider));
        recyclerViewPiadineNonVotate.addItemDecoration(itemDecoratorOrdini);

        adapterPiadineNonVotate = new LeMiePiadineNonVotateAdapter(mContext, piadineNonVotate, this);
        recyclerViewPiadineNonVotate.setAdapter(adapterPiadineNonVotate);

        setEmptyMessage();

    }

    public void addPiadinaVotataInAdapter(Piadina piadina) {

        piadineVotate.add(piadina);
        adapterPiadineVotate.notifyDataSetChanged();
        /*riordinaClassifica();*/
    }

    public void removePiadinaNonVotataInAdapter(int position){
        piadineNonVotate.remove(position);
        adapterPiadineNonVotate.notifyDataSetChanged();
    }

    public void riordinaClassifica(){
        Comparator objComparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {

                int voto1 = ((Piadina) o1).getRating();
                int voto2 = ((Piadina) o2).getRating();

                return  voto1 > voto2 ? -1 : voto1 == voto2 ? 0 : 1;
            }

        };
        Collections.sort(piadineVotate, objComparator);
    }

    public ArrayList<Piadina> getPiadineNonVotate(ArrayList<Piadina> piadineVotate, ArrayList<Ordine> ordiniUtente){
        ArrayList<Piadina> piadineNonVotate = new ArrayList<>();

        ArrayList<Piadina> piadineOrdini = getPiadineNonDoppie(ordiniUtente);

        for(Piadina piadinaOrdini: piadineOrdini){

            if(!isAlreadyVotata(piadinaOrdini, piadineVotate)){
                piadinaOrdini.setQuantita(1);
                double prezzoSingolo = piadinaOrdini.getPrice() /  piadinaOrdini.getQuantita();
                piadinaOrdini.setPrice(prezzoSingolo);
                piadineNonVotate.add(piadinaOrdini);
            }
        }

        return piadineNonVotate;
    }

    public ArrayList<Piadina> getPiadineNonDoppie(ArrayList<Ordine> ordiniUtente){
        ArrayList<Piadina> piadineNonDoppie = new ArrayList<>();
        ArrayList<Piadina> piadineAppoggio = new ArrayList<>();

        for(Ordine ordine: ordiniUtente){
            piadineAppoggio.addAll(ordine.getPiadineSingoleOrdine());
        }

        Set<Piadina> piadineOrdine = new HashSet<>(piadineAppoggio);
        piadineNonDoppie.addAll(piadineOrdine);
        return piadineNonDoppie;
    }

    public boolean isAlreadyVotata(Piadina piadinaDaControllare, ArrayList<Piadina> piadineVotate){
        boolean votata = false;
        for(Piadina piadinaVotata: piadineVotate){
            String formatoVotata = piadinaVotata.getFormato();
            String impastoVotata = piadinaVotata.getImpasto();
            String ingredientiVotata = piadinaVotata.printIngredienti();

            String formatoDaControllare = piadinaDaControllare.getFormato();
            String impastoDaControllare = piadinaDaControllare.getImpasto();
            String ingredientiDaControllare = piadinaDaControllare.printIngredienti();

            if((formatoVotata.equals(formatoDaControllare)) && (impastoVotata.equals(impastoDaControllare)) &&
                    (ingredientiVotata.equals(ingredientiDaControllare))){
                return true;
            }
        }

        return votata;
    }

    public void setEmptyMessage(){
        if(piadineVotate.isEmpty()){
            testoPiadineVuote.setVisibility(View.VISIBLE);
        }else{
            testoPiadineVuote.setVisibility(View.GONE);
            ((RelativeLayout.LayoutParams) recyclerViewPiadineVotate.getLayoutParams()).addRule(RelativeLayout.BELOW, R.id.la_mia_classifica);
        }

        if(piadineNonVotate.isEmpty()){
            testoPiadineVuoteNonVotate.setVisibility(View.VISIBLE);
        }else{
            ((RelativeLayout.LayoutParams) recyclerViewPiadineNonVotate.getLayoutParams()).addRule(RelativeLayout.BELOW, R.id.le_piadine_non_votate);
        }
    }
}
