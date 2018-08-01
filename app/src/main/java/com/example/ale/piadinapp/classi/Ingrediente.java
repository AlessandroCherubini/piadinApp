package com.example.ale.piadinapp.classi;

import java.util.ArrayList;

public class Ingrediente {

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
}
