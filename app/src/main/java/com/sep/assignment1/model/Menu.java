package com.sep.assignment1.model;

import java.util.ArrayList;

public class Menu {

    private String mMenuId;
    private String mMenuName;
    private ArrayList<Food> mFoodArrayList;
    private Double mTotalPrice;

    public Menu(){}
    public Menu(String menuId, String menuName, ArrayList<Food> foodArrayList, double totalPrice) {
        mMenuId = menuId;
        mMenuName = menuName;
        mFoodArrayList = foodArrayList;
        mTotalPrice = totalPrice;
    }

    public String getMenuId() {
        return mMenuId;
    }

    public void setMenuId(String menuId) {
        mMenuId = menuId;
    }

    public String getMenuName() {
        return mMenuName;
    }

    public void setMenuName(String menuName) {
        mMenuName = menuName;
    }

    public Double getTotalPrice() {
        return mTotalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        mTotalPrice = totalPrice;
    }

    public ArrayList<Food> getFoodArrayList() {
        return mFoodArrayList;
    }

    public void setFoodArrayList(ArrayList<Food> foodArrayList) {
        mFoodArrayList = foodArrayList;
    }

}
