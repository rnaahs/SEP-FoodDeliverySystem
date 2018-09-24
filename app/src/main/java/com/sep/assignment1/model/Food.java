package com.sep.assignment1.model;
//fod food2bnbgjh
public class Food {
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
}