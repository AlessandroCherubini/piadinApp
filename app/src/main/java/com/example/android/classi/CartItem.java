package com.example.android.classi;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CartItem implements Parcelable {

    private String nome;
    private String formato;
    private String impasto;
    private ArrayList<Ingrediente> ingredienti;
    private double prezzo;
    private int quantita;
    private int rating;
    private String identifier;

    public CartItem(String nome, String formato, String impasto, double prezzo, int quantita, ArrayList<Ingrediente> ingredienti ) {
        this.nome = nome;
        this.formato=formato;
        this.impasto=impasto;
        this.prezzo=prezzo;
        this.quantita = quantita;
        this.ingredienti=ingredienti;
    }

    public CartItem(String nome, String formato, String impasto, double prezzo, int quantita, int rating, ArrayList<Ingrediente> ingredienti, String identifier ) {
        this.nome = nome;
        this.formato=formato;
        this.impasto=impasto;
        this.prezzo=prezzo;
        this.quantita = quantita;
        this.rating = rating;
        this.ingredienti=ingredienti;
        this.identifier=identifier;
    }

    public Piadina cartItemToPiadina() {

        String nomePiadina = nome;
        String formatoPiadina = formato;
        String impastoPiadina = impasto;
        double prezzoPiadina = prezzo;
        ArrayList<Ingrediente> ingredientiPiadina = ingredienti;
        int quantitaPiadina = quantita;
        int ratingPiadina = rating;

        Piadina piadina = new Piadina(nomePiadina, formatoPiadina, impastoPiadina,ingredientiPiadina,
                prezzoPiadina, quantitaPiadina, ratingPiadina);

        return piadina;
    }


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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

    private CartItem(Parcel in) {
        nome = in.readString();
        formato = in.readString();
        impasto = in.readString();
        ingredienti = in.readArrayList(null);
        prezzo = in.readDouble();
        quantita = in.readInt();
        rating = in.readInt();
        identifier = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeString(formato);
        dest.writeString(impasto);
        dest.writeList(ingredienti);
        dest.writeDouble(prezzo);
        dest.writeInt(quantita);
        dest.writeInt(rating);
        dest.writeString(identifier);
    }

    public static final Parcelable.Creator<CartItem> CREATOR = new Parcelable.Creator<CartItem>() {
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };
}
