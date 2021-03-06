package com.sep.assignment1.model;

import java.util.ArrayList;

public class Order {
    private String OrderID;
    private ArrayList<Food> FoodArrayList;
    private String RestaurantAddress;
    private String CustomerAddress;
    private String Price;
    private String StartTime;
    private String EndTime;
    private String CustomerID;
    private String RestaurantID;
    private String Status;
    private String mRestaurantName;
    private String mRestaurantURI;
    private String mRestaurantOwnerID;
    private String mDescription;

    public Order(){

    }

    public Order(String OrderID, ArrayList<Food> FoodArrayList, String RestaurantOwnerID, String RestaurantName, String RestaurantImageURI, String RestaurantAddress, String CustomerAddress, String Price, String StartTime, String EndTime, String CustomerID,String RestaurantID, String Status, String mDescription){
        this.OrderID = OrderID;
        mRestaurantName = RestaurantName;
        mRestaurantURI = RestaurantImageURI;
        this.FoodArrayList = FoodArrayList;
        this.RestaurantAddress = RestaurantAddress;
        this.CustomerAddress = CustomerAddress;
        this.Price = Price;
        this.StartTime = StartTime;
        this.EndTime = EndTime;
        this.CustomerID = CustomerID;
        this.RestaurantID = RestaurantID;
        this.mRestaurantOwnerID = RestaurantOwnerID;
        this.Status = Status;
        this.mDescription = mDescription;
    }

    public String getOrderID() {

        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getRestaurantName() {
        return mRestaurantName;
    }

    public String getRestaurantOwnerID() {
        return mRestaurantOwnerID;
    }

    public void setRestaurantOwnerID(String restaurantOwnerID) {
        mRestaurantOwnerID = restaurantOwnerID;
    }

    public void setRestaurantName(String restaurantName) {
        mRestaurantName = restaurantName;
    }

    public String getRestaurantURI() {
        return mRestaurantURI;
    }

    public void setRestaurantURI(String restaurantURI) {
        mRestaurantURI = restaurantURI;
    }

    public ArrayList<Food> getFoodArrayList() {
        return FoodArrayList;
    }

    public void setFoodArrayList(ArrayList<Food> foodArrayList) {
        FoodArrayList = foodArrayList;
    }

    public String getRestaurantAddress() {
        return RestaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        RestaurantAddress = restaurantAddress;
    }

    public String getCustomerAddress() {
        return CustomerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        CustomerAddress = customerAddress;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getRestaurantID() {
        return RestaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        RestaurantID = restaurantID;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getStartTime() {
        return StartTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public String getmRestaurantName() {
        return mRestaurantName;
    }

    public void setmRestaurantName(String mRestaurantName) {
        this.mRestaurantName = mRestaurantName;
    }

    public String getmRestaurantURI() {
        return mRestaurantURI;
    }

    public void setmRestaurantURI(String mRestaurantURI) {
        this.mRestaurantURI = mRestaurantURI;
    }

    public String getmRestaurantOwnerID() {
        return mRestaurantOwnerID;
    }

    public void setmRestaurantOwnerID(String mRestaurantOwnerID) {
        this.mRestaurantOwnerID = mRestaurantOwnerID;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }
}
