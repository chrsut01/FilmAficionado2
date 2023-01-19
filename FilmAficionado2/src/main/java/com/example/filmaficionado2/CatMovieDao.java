package com.example.filmaficionado2;


import java.util.List;

// INTERFACE DEFINING METHODS FOR ADDING, REMOVING AND SHOWING ALL MOVIES IN A CATEGORY

public interface CatMovieDao {

        void addMovieCat(int categoryID, int movieID);

        void removeMovieCat(Movie movie);

        List<Movie> getAllMoviesOnCatMovie(Category category);

    }
