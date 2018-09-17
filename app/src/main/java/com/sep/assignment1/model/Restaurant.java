package com.sep.assignment1.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Restaurant {
    public String Id;
    public String Name;
    public String Type;
    public String Country;
    public String Address;
    public String Status;
    public String ImageUri;

    public Restaurant(String Id, String name, String type, String country, String image, String status, String ImageUri) {
        this.Id = Id;
        this.Name = name;
        this.Type = type;
        this.Country = country;
        this.Address = image;
        this.Status = status;
        this.ImageUri = ImageUri;
    }

    public Restaurant(){

    }

}
