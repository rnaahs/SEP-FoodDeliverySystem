package com.sep.assignment1.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Cart implements Parcelable {
    private String mCartID;
    private ArrayList<Food> mFoodArrayList;
    private String mQuantity;
    private String mPrice;
    private String mDiscount;

    public Cart(){}

    public Cart(String CartID, ArrayList<Food> FoodArrayList, String Quantity, String Price, String Discount) {
        this.mCartID = CartID;
        this.mFoodArrayList = FoodArrayList;
        this.mQuantity = Quantity;
        this.mPrice = Price;
        this.mDiscount = Discount;
    }

    protected Cart(Parcel in) {
        this.mCartID = in.readString();
        this.mFoodArrayList = in.createTypedArrayList(Food.CREATOR);
        this.mQuantity = in.readString();
        this.mPrice = in.readString();
        this.mDiscount = in.readString();
    }

    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };

    public String getmCartID() {
        return mCartID;
    }

    public void setmCartID(String mCartID) {
        this.mCartID = mCartID;
    }

    public ArrayList<Food> getmFoodArrayList() {
        return mFoodArrayList;
    }

    public void setmFoodArrayList(ArrayList<Food> mFoodArrayList) {
        this.mFoodArrayList = mFoodArrayList;
    }

    public String getmQuantity() {
        return mQuantity;
    }

    public void setmQuantity(String mQuantity) {
        this.mQuantity = mQuantity;
    }

    public String getmPrice() {
        return mPrice;
    }

    public void setmPrice(String mPrice) {
        this.mPrice = mPrice;
    }

    public String getmDiscount() {
        return mDiscount;
    }

    public void setmDiscount(String mDiscount) {
        this.mDiscount = mDiscount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCartID);
        dest.writeTypedList(mFoodArrayList);
        dest.writeString(mQuantity);
        dest.writeString(mPrice);
        dest.writeString(mDiscount);
        }
    }
