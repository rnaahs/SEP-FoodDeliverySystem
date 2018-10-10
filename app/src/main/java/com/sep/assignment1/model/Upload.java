package com.sep.assignment1.model;

public class Upload {
    private String mName;
    private String mImageUir;

    public Upload(){}

    public Upload(String name, String imageUir) {
        if(name.trim().equals("")) name = "No Name";
        mName = name;
        mImageUir = imageUir;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUir() {
        return mImageUir;
    }

    public void setImageUir(String imageUir) {
        mImageUir = imageUir;
    }
}
