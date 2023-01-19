package com.example.filmaficionado2;

// INTERFACE DEFINING METHODS FOR ADDING, REMOVING AND SHOWING BEST MOVIE IN A CATEGORY

public interface BestInCatDao {

        void addBestInCat(int categoryID, int movieID);
        void removeBestInCat(Movie movie);
        String showBestInCat(int categoryID);
}


