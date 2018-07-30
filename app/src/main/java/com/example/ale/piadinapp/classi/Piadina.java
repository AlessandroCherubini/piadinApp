package com.example.ale.piadinapp.classi;

import android.util.Log;

import java.util.ArrayList;

public class Piadina {

    private long id;
    private String nome;
    private ArrayList<Ingrediente> ingredienti;
    private double price;
    //private int rating;
    private long lastUpdated;


    public Piadina(long id, String nome, ArrayList<Ingrediente> ingredienti, double price, long lastUpdated) {
        this.id = id;
        this.nome = nome;
        this.ingredienti = ingredienti;
        this.price = price;
        //this.rating = rating;
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

    /*public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }*/

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
        Log.d("STRINGA", stringaFinale);

        return stringaFinale;
    }

    public void printDettagliIngredienti(){
        for(int i = 0; i < ingredienti.size(); i++){
            Log.d("DETTAGLI", " " + ingredienti.get(i).getIdIngrediente());
            Log.d("DETTAGLI", ingredienti.get(i).getName());
            Log.d("DETTAGLI", " " + ingredienti.get(i).getPrice());
            Log.d("DETTAGLI", " " + ingredienti.get(i).getLastUpdated());
        }

    }

}