package com.marsht21.restaurantpicker;
/*
 * cards
 * Acts as an array to populate the swipe cards view
 */
public class cards {
    private String name;

    public cards(){

    }

    public cards(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
}
