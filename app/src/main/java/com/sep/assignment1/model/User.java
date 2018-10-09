package com.sep.assignment1.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private String userid;
    private String firstname;
    private String lastname;
    private String email;
    private int role;
    private String address;
    private double balance;
    private int bsb;

    public User(String userId, String firstname, String lastname, String email, int role, String address, double balance, int BSB) {
        this.userid = userId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
        this.address = address;
        this.balance = balance;
        this.bsb = BSB;
    }


    public User(){}

    public int getBsb() {
        return bsb;
    }

    public void setBsb(int bsb) {
        this.bsb = bsb;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
