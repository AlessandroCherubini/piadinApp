package com.example.ale.piadinapp.classi;

import java.util.ArrayList;

public class CartItem {

    private String nome;
    private String formato;
    private String impasto;
    private ArrayList<Ingrediente> ingredienti;
    private double prezzo;
    private String identifier;


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public void setImpasto(String impasto)
    {
        this.impasto = impasto;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public ArrayList<Ingrediente> getIngredienti() {
        return ingredienti;
    }

    public String printIngredienti (){
        String stringaIngredienti = "";
        for(int i = 0; i < ingredienti.size(); i++){
            stringaIngredienti = stringaIngredienti + ingredienti.get(i).toString() + ", ";
        }

        String stringaFinale = stringaIngredienti.substring(0, stringaIngredienti.length() - 2);

        return stringaFinale;
    }

    public void setIngredienti(ArrayList<Ingrediente> ingredienti) {
        this.ingredienti = ingredienti;
    }

    public CartItem(String nome, String formato, String impasto, double prezzo, ArrayList<Ingrediente> ingredienti ) {

        this.nome = nome;
        this.formato=formato;
        this.impasto=impasto;
        this.prezzo=prezzo;
        this.ingredienti=ingredienti;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public CartItem(String nome, String formato, String impasto, double prezzo, ArrayList<Ingrediente> ingredienti, String identifier ) {

        this.nome = nome;
        this.formato=formato;
        this.impasto=impasto;
        this.prezzo=prezzo;
        this.ingredienti=ingredienti;
        this.identifier=identifier;
    }

    public Piadina  cartItemToPiadina(CartItem item) {

        Piadina piadina = new Piadina(nome,ingredienti,prezzo);

        return piadina;
    }




}
