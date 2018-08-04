package com.example.ale.piadinapp.classi;

import java.util.ArrayList;

public class Ordine {

    private long idOrdine;
    private long timestampOrdine;
    private ArrayList<Piadina> cartItems;
    private String notaOrdine;
    private double prezzoOrdine;
    private long lastUpdated;

    public void Ordine(long idOrdine, long timestampData, ArrayList<Piadina> piadineOrdinate, String notaOrdine, double prezzoOrdine, long lastUpdated){
        this.idOrdine = idOrdine;
        this.timestampOrdine = timestampData;
        this.cartItems = piadineOrdinate;
        this.notaOrdine = notaOrdine;
        this.prezzoOrdine = prezzoOrdine;
        this.lastUpdated = lastUpdated;
    }

    public long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(long idOrdine) {
        this.idOrdine = idOrdine;
    }


    public long getTimestampOrdine() {
        return timestampOrdine;
    }

    public void setTimestampOrdine(long timestampOrdine) {
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

}
