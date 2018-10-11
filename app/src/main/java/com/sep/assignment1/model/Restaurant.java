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

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }
}
