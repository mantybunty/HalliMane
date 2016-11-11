package com.example.nata.hallimane;

/**
 * Created by nata on 28/5/16.
 */
public class Category {

    private int id;
    private String name;

    public Category(){}

    public Category(int id, String name){
        this.id = id;
        this.name = name;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

}
