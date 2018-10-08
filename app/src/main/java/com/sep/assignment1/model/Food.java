package com.sep.assignment1.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Food implements Parcelable {
    private String mFoodId;
    private String mFoodName;
    private double mFoodPrice;
    private String mFoodDescription;
    private String mFoodImgURL;
    public Food() {}
    public Food(String foodId, String foodName, double foodPrice, String foodDescription, String foodImgURL) {
        mFoodId = foodId;
        mFoodName = foodName;
        mFoodPrice = foodPrice;
        mFoodDescription = foodDescription;
        mFoodImgURL = foodImgURL;
    }

    protected Food(Parcel in) {
        mFoodId = in.readString();
        mFoodName = in.readString();
        mFoodPrice = in.readDouble();
        mFoodDescription = in.readString();
        mFoodImgURL = in.readString();
    }

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    public String getFoodId() {
        return mFoodId;
    }

    public void setFoodId(String foodId) {
        mFoodId = foodId;
    }

    public String getFoodName() {
        return mFoodName;
    }

    public void setFoodName(String foodName) {
        mFoodName = foodName;
    }

    public double getFoodPrice() {
        return mFoodPrice;
    }

    public void setFoodPrice(double foodPrice) {
        mFoodPrice = foodPrice;
    }

    public String getFoodDescription() {
        return mFoodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        mFoodDescription = foodDescription;
    }

    public String getFoodImgURL() {
        return mFoodImgURL;
    }

    public void setFoodImgURL(String foodImgURL) {
        mFoodImgURL = foodImgURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFoodId);
        dest.writeString(mFoodName);
        dest.writeDouble(mFoodPrice);
        dest.writeString(mFoodDescription);
        dest.writeString(mFoodImgURL);
    }
}