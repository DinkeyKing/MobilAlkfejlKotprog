package com.example.kotprog;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class ShoppingItem {
    private String id;
    private String name;
    private String desc;
    private String price;
    private float rating;
    private int imageResource;
    private String type;
    private int cartedCount;
    private String imagePath;

    public ShoppingItem(){}
    public ShoppingItem(String name, String desc, String price, float rating, int imageResource, String type, int cartedCount, String imagePath) {
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.rating = rating;
        this.imageResource = imageResource;
        this.type = type;
        this.cartedCount = cartedCount;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getPrice() {
        return price;
    }

    public float getRating() {
        return rating;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getType() {
        return type;
    }

    public int getCartedCount() {
        return cartedCount;
    }

    public String _getId(){
        return id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setId(String id) {
        this.id = id;
    }
}
