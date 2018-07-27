package com.example.ale.piadinapp;

import java.util.ArrayList;
import java.util.List;

public class Piadina {

    private int id;
    private String nome;
    private String descrizione;
    private double price;
    private int rating;
    List<Ingrediente> ingredienti = new ArrayList<>();



    public Piadina(int id, String nome, List<Ingrediente>ingredienti, double price, int rating) {
        this.id = id;
        this.nome = nome;
        this.ingredienti=ingredienti;
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

    public List<Ingrediente> getIngredienti() {
        return ingredienti;
    }

    public void setIngredienti(List<Ingrediente> ingredienti) {
        this.ingredienti = ingredienti;
    }

    @Override
    public String toString() {
        return "Piadina{" +
                "nome='" + nome + '\'' +
                '}';
    }


}
