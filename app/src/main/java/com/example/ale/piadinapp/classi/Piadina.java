package com.example.ale.piadinapp.classi;

public class Piadina {

    private long id;
    private String nome;
    private String descrizione;
    private double price;
    //private int rating;
    private long lastUpdated;


    public Piadina(long id, String nome, String descrizione, double price, long lastUpdated) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
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

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
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

}