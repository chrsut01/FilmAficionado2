package com.example.filmaficionado2;


import java.util.List;

// INTERFACE DEFINING METHODS FOR ADDING, REMOVING AND RETRIEVING ALL CATEGORIES IN DATABASE

public interface CategoryDao {

    void saveCategory(String categoryName);

    void deleteCategory(Category category);

    List<Category> getAllCategories();

}
