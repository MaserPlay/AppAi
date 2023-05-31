package com.maserplay.appai;

import android.util.Log;

public class Product {
    private String name;
    public int who;

    Product(String name, int who){
        this.name = name;
        this.who = who;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
}