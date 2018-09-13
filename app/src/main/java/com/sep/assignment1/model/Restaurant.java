package com.sep.assignment1.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Restaurant {
    public String Id;
    public String Name;
    public String Type;
    public String Country;
    public String Image;
    public String Status;

    public Restaurant(String Id, String name, String type, String country, String image, String status) {
        this.Id = Id;
        this.Name = name;
        this.Type = type;
        this.Country = country;
        this.Image = image;
        this.Status = status;
    }

    public Restaurant(){

    }

}
