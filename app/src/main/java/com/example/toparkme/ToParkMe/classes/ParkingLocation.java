package com.example.toparkme.ToParkMe.classes;

import android.util.Log;

import com.google.gson.Gson;

public class ParkingLocation {

    private String addressName;
    private double longitude;
    private double latitude;

    public ParkingLocation() {
        setLongitude(0);
        setLatitude(0);
        setAddressName("UNKNOWN");
    }
    public ParkingLocation(double longitude, double latitude, String addressName) {
        setLongitude(longitude);
        setLatitude(latitude);
        setAddressName(addressName);
    }


    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setAddressName(String addressName){ this.addressName = addressName; }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getAddressName(){ return this.addressName; }



}
