package com.example.ale.piadinapp.classi;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Ingrediente implements Parcelable {

    private long idIngrediente;
    private String name;
    private double price;
    private String listaAllergeni;
    private String categoria;
    private long lastUpdated;


    public Ingrediente(long idIngrediente, String name, double price, String listaAllergeni, String categoria, long lastUpdated){
        this.idIngrediente = idIngrediente;
        this.name = name;
        this.price = price;
        this.listaAllergeni = listaAllergeni;
        this.categoria = categoria;
        this.lastUpdated = lastUpdated;

    }

    public Ingrediente(String name){

        this.name = name;
    }

    public long getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(long idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getListaAllergeni() {
        return listaAllergeni;
    }

    public void setListaAllergeni(String listaAllergeni) {
        this.listaAllergeni = listaAllergeni;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {

        return name;
    }

    // Metodo utilizzato per l'interfaccia: serve a salvare l'oggetto Ingrediente quando si ruota il telefono.
    private Ingrediente(Parcel in) {
        idIngrediente = in.readLong();
        name = in.readString();
        price = in.readDouble();
        listaAllergeni = in.readString();
        categoria = in.readString();
        lastUpdated = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(idIngrediente);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(listaAllergeni);
        dest.writeString(categoria);
        dest.writeLong(lastUpdated);
    }

    public static final Parcelable.Creator<Ingrediente> CREATOR = new Parcelable.Creator<Ingrediente>() {
        public Ingrediente createFromParcel(Parcel in) {
            return new Ingrediente(in);
        }

        public Ingrediente[] newArray(int size) {
            return new Ingrediente[size];
        }
    };
}
