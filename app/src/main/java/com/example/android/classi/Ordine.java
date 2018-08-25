package com.example.android.classi;

import java.util.ArrayList;

public class Ordine {

    private long idOrdine;
    private String emailUtente;
    private String telefonoUtente;
    private String timestampOrdine;
    private double prezzoOrdine;
    private ArrayList<Piadina> cartItems;
    private String notaOrdine;
    private long lastUpdated;

    public Ordine(long idOrdine, String emailUtente, String telefonoUtente, String timestampData,
                  double prezzoOrdine, ArrayList<Piadina> piadineOrdinate, String notaOrdine, long lastUpdated){
        this.idOrdine = idOrdine;
        this.emailUtente = emailUtente;
        this.telefonoUtente = telefonoUtente;
        this.prezzoOrdine = prezzoOrdine;
        this.timestampOrdine = timestampData;
        this.cartItems = piadineOrdinate;
        this.notaOrdine = notaOrdine;
        this.lastUpdated = lastUpdated;
    }

    public long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(long idOrdine) {
        this.idOrdine = idOrdine;
    }

    public String getEmailUtente() {
        return emailUtente;
    }

    public void setEmailUtente(String emailUtente) {
        this.emailUtente = emailUtente;
    }

    public String getTelefonoUtente() {
        return telefonoUtente;
    }

    public void setTelefonoUtente(String telefonoUtente) {
        this.telefonoUtente = telefonoUtente;
    }

    public String getTimestampOrdine() {
        return timestampOrdine;
    }

    public void setTimestampOrdine(String timestampOrdine) {
        this.timestampOrdine = timestampOrdine;
    }

    public ArrayList<Piadina> getCartItems() {
        return cartItems;
    }

    public void setCartItems(ArrayList<Piadina> cartItems) {
        this.cartItems = cartItems;
    }

    public String getNotaOrdine() {
        return notaOrdine;
    }

    public void setNotaOrdine(String notaOrdine) {
        this.notaOrdine = notaOrdine;
    }

    public double getPrezzoOrdine() {
        return prezzoOrdine;
    }

    public void setPrezzoOrdine(double prezzoOrdine) {
        this.prezzoOrdine = prezzoOrdine;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String printPiadine(){
        String piadineOrdine ="";
        String separatorePiadine =" - ";
        String separatoreAttributi ="; ";

        for(Piadina piadina:cartItems){
            String stampaIngredienti = piadina.printIngredienti();

            String piadinaOrdine = "[" + piadina.getNome() + separatoreAttributi +
                    piadina.getFormato() + separatoreAttributi +
                    piadina.getImpasto() + separatoreAttributi +
                    stampaIngredienti + separatoreAttributi +
                    piadina.getPrice() + separatoreAttributi +
                    piadina.getQuantita() + "]";

            piadineOrdine = piadineOrdine + piadinaOrdine + separatorePiadine;
        }

        String piadineOrdineString = piadineOrdine.substring(0, piadineOrdine.length() - 3);

        return piadineOrdineString;
    }
}
