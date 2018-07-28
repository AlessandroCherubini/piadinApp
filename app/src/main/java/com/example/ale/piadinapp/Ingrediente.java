package com.example.ale.piadinapp;

public class Ingrediente {

    private String name;
    private double price;


    public Ingrediente(String name, double price){

        this.name=name;
        this.price=price;

    }

    public Ingrediente(String name){

        this.name=name;
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

    @Override
    public String toString() {
        return name;
    }
}


