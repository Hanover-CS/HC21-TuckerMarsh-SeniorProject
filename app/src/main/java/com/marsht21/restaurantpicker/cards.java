package com.marsht21.restaurantpicker;

public class cards { // Basically acts as an array to populate the swipe cards
    private String restaurantID;
    private String name;

    public cards(){

    }

    public cards(String restaurantID, String name){
        this.restaurantID = restaurantID;
        this.name = name;
    }

    public String getRestaurantID(){
        return restaurantID;
    }
    public void setRestaurantID(String restaurantID){
        this.restaurantID = restaurantID;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
}
