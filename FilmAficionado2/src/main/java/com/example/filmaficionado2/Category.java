package com.example.filmaficionado2;

// CREATES THE CLASS CATEGORY

public class Category {
    private int categoryID;
    private String categoryName;


    public String toString() { return categoryName;}

    // CONSTRUCTOR FOR CREATING CATEGORY OBJECT
    public Category(int categoryID, String categoryName){
        this.categoryID = categoryID;
        this.categoryName = categoryName;
    }
    // OVERLOADED CONSTRUCTOR:
    public Category() {
    }

    public int getCategoryID() { return categoryID; }
}
