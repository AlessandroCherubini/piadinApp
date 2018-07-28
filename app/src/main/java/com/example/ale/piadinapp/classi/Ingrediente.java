package com.example.ale.piadinapp;

public class Ingrediente {

    private long idIngrediente;
    private String name;
    private double price;
    private long lastUpdated;


    public Ingrediente(long idIngrediente, String name, double price, long lastUpdated){
        this.idIngrediente = idIngrediente;
        this.name = name;
        this.price = price;
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
