package com.example.android.piadinapp.classi;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class Piadina implements Parcelable{

    private long id;
    private String nome;
    private ArrayList<Ingrediente> ingredienti;
    private double price;
    private String formato;
    private String impasto;
    private int quantita;
    private int rating;
    private int idEsterno;
    private long lastUpdated;


    public Piadina(long id, String nome, ArrayList<Ingrediente> ingredienti, double price,
                   String formato, String impasto, int quantita, int rating, long lastUpdated) {
        this.id = id;
        this.nome = nome;
        this.ingredienti = ingredienti;
        this.price = price;
        this.formato = formato;
        this.impasto = impasto;
        this.quantita = quantita;
        this.rating = rating;
        this.lastUpdated = lastUpdated;
    }

    // Costruttore per le piadine del menù e degli Ordini
    public Piadina(String nome, String formato, String impasto, ArrayList<Ingrediente> ingredienti,
                   double price, int quantita, int rating) {
        this.nome = nome;
        this.ingredienti = ingredienti;
        this.formato = formato;
        this.impasto = impasto;
        this.price = price;
        this.quantita = quantita;
        this.rating = rating;
    }

    // Costruttore per "Le mie Piadine"
    public Piadina(long id, String nome, String formato, String impasto, ArrayList<Ingrediente> ingredienti,
                   double price, int quantita, int rating, int idEsterno, long lastUpdated) {
        this.id = id;
        this.nome = nome;
        this.ingredienti = ingredienti;
        this.formato = formato;
        this.impasto = impasto;
        this.price = price;
        this.quantita = quantita;
        this.rating = rating;
        this.idEsterno = idEsterno;
        this.lastUpdated = lastUpdated;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<Ingrediente> getIngredienti() {
        return ingredienti;
    }

    public void setIngredienti(ArrayList<Ingrediente> ingredienti) {
        this.ingredienti = ingredienti;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getImpasto() {
        return impasto;
    }

    public void setImpasto(String impasto) {
        this.impasto = impasto;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getIdEsterno() {
        return idEsterno;
    }

    public void setIdEsterno(int idEsterno) {
        this.idEsterno = idEsterno;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


    public String printIngredienti (){
        String stringaIngredienti = "";
        for(int i = 0; i < ingredienti.size(); i++){
            stringaIngredienti = stringaIngredienti + ingredienti.get(i).toString() + ", ";
        }

        String stringaFinale = stringaIngredienti.substring(0, stringaIngredienti.length() - 2);

        return stringaFinale;
    }

    public void printDettagliIngredienti(){
        for(int i = 0; i < ingredienti.size(); i++){
            Log.d("DETTAGLI", "ID ingrediente " + ingredienti.get(i).getIdIngrediente());
            Log.d("DETTAGLI", "Nome ingrediente " + ingredienti.get(i).getName());
            Log.d("DETTAGLI", "Prezzo ingrediente " + ingredienti.get(i).getPrice());
            Log.d("DETTAGLI", "Categoria ingrediente " + ingredienti.get(i).getCategoria());
            Log.d("DETTAGLI", "Aggiornamento ingrediente " + ingredienti.get(i).getLastUpdated());
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}