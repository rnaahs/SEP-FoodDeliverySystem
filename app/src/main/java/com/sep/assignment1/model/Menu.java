package com.sep.assignment1.model;

import java.util.ArrayList;

public class Menu {

    private String mMenuId;
    private String mFoodId1;
    private String mComboMenu;
    private double mMenuPrice;
    private String mDescription;
    private String mMenuImgURL;
    private static ArrayList<Food> mFoodList = new ArrayList<>();

    public Menu(String menuId, String foodId, String comboMenu, String description, String menuImgURL) {
        mMenuId = menuId;
        mFoodId1 = foodId;
        mComboMenu = comboMenu;
        mDescription = description;
        mMenuImgURL = menuImgURL;
        mFoodList.add(new Food("FD0001", "Angus Beef Burger", 22.40, "Burger"));
        mFoodList.add(new Food("FD0002", "Tomato Pasta", 19.50, "Pasta"));
        mFoodList.add(new Food("FD0003", "Vegan Salad", 18.40, "Salad"));
    }

    public Food getFoodById(String foodId){
        Food food;
        for(int i = 0; i< mFoodList.size(); i++){
            food = mFoodList.get(i);
            if(foodId.equals(food.getFoodId())) {
                return food;
            }
        }
        return null;
    }

    public ArrayList<Food> getFoodList(){
        return mFoodList;
    }

    public String getMenuId() {
        return mMenuId;
    }

    public String getFoodId() {
        return mFoodId1;
    }

    public String getComboMenu() {
        return mComboMenu;
    }

    public void setComboMenu(String comboMenu) {
        mComboMenu = comboMenu;
    }

    public double getMenuPrice() {
        return mMenuPrice;
    }

    public void setMenuPrice(Double... menuPrice) {
        for(int i = 0; i<menuPrice.length; i++) mMenuPrice += menuPrice[i];
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getMenuImgURL() {
        return mMenuImgURL;
    }

    public void setMenuImgURL(String menuImgURL) {
        mMenuImgURL = menuImgURL;
    }
}
