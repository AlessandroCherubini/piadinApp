package com.example.ale.piadinapp;

public class Piadina {

    private int id;
    private String nome;
    private String descrizione;
    private double price;
    private int rating;




    public Piadina(int id, String nome, String descrizione, double price, int rating) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.price = price;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }


}
