package com.sep.assignment1.model;

public class Menu {

    private String mMenuId;
    private String mComboMenu;
    private String mDescription;
    private String mMenuImgURL;
    private Food mFood;

    public Menu(){}
    public Menu(String menuId, Food food, String comboMenu, String description, String menuImgURL) {
        mMenuId = menuId;
        mFood = food;
        mComboMenu = comboMenu;
        mDescription = description;
        mMenuImgURL = menuImgURL;
    }

    public String getMenuId() {
        return mMenuId;
    }

    public String getComboMenu() {
        return mComboMenu;
    }

    public void setComboMenu(String comboMenu) {
        mComboMenu = comboMenu;
    }

    public Food getFood() {
        return mFood;
    }

    public void setFood(Food food) {
        mFood = food;
    }

    /*public double getMenuPrice() {
        return mMenuPrice;
    }

    public void setMenuPrice(Double... menuPrice) {
        for(int i = 0; i<menuPrice.length; i++) mMenuPrice += menuPrice[i];
    }*/

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
