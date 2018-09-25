package com.sep.assignment1.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Menu implements Parcelable {

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

    protected Menu(Parcel in) {
        mMenuId = in.readString();
        mMenuName = in.readString();
        mFoodArrayList = in.createTypedArrayList(Food.CREATOR);
        if (in.readByte() == 0) {
            mTotalPrice = null;
        } else {
            mTotalPrice = in.readDouble();
        }
    }

    public static final Creator<Menu> CREATOR = new Creator<Menu>() {
        @Override
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        @Override
        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };

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

    public void setFood(Food food) {
        mFoodArrayList.add(food);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMenuId);
        dest.writeString(mMenuName);
        dest.writeTypedList(mFoodArrayList);
        if (mTotalPrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(mTotalPrice);
        }
    }
}
