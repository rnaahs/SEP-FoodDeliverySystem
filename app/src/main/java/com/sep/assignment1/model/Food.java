package com.sep.assignment1.model;

public class Food {
    private String mFoodId;
    private String mFoodName;
    private double mFoodPrice;
    private String mFoodType;

    public Food() {}
    public Food(String foodId, String foodName, double foodPrice, String foodType) {
        mFoodId = foodId;
        mFoodName = foodName;
        mFoodPrice = foodPrice;
        mFoodType = foodType;
    }

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

    public String getFoodType() {
        return mFoodType;
    }

    public void setFoodType(String foodType) {
        mFoodType = foodType;
    }
}