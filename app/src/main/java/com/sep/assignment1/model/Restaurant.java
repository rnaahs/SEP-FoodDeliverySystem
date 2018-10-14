package com.sep.assignment1.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Restaurant implements Parcelable{
    public String Id;
    public String Name;
    public String Type;
    public String Country;
    public String Address;
    public String Status;
    public String ImageUri;
    public String OwnerID;

    public Restaurant(String Id, String name, String type, String country, String image, String status, String ImageUri,String OwnerID) {
        this.Id = Id;
        this.Name = name;
        this.Type = type;
        this.Country = country;
        this.Address = image;
        this.Status = status;
        this.ImageUri = ImageUri;
        this.OwnerID = OwnerID;
    }

    public Restaurant(){

    }

    protected Restaurant(Parcel in) {
        Id = in.readString();
        Name = in.readString();
        Type = in.readString();
        Country = in.readString();
        Address = in.readString();
        Status = in.readString();
        ImageUri = in.readString();
        OwnerID = in.readString();
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Id);
        dest.writeString(Name);
        dest.writeString(Type);
        dest.writeString(Country);
        dest.writeString(Address);
        dest.writeString(Status);
        dest.writeString(ImageUri);
        dest.writeString(OwnerID);
    }
}
