package com.example.toparkme.ToParkMe.classes;


import android.util.Log;

import com.google.gson.Gson;

public class Person {

    private String name;
    private ParkingLocation parkingLocation;
    private User user;

    public Person() {
        setName("Name");
        setParkingLocation(new ParkingLocation());
        setUser(new User());
    }


    public Person(String name, ParkingLocation parkingLocation, User user) {
        this.name = name;
        this.parkingLocation = parkingLocation;
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ParkingLocation getParkingLocation() {
        return parkingLocation;
    }

    public void setParkingLocation(ParkingLocation parkingLocation) {
        this.parkingLocation = parkingLocation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
