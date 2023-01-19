package com.example.filmaficionado2;


import java.util.List;
// INTERFACE OR ABSTRACT CLASS THAT DECLARES METHODS TO BE APPIED TO MOVIE OBJECTS
public interface MovieDao {
    void saveMovie(String title, int myRating, String imdbRating, String filelink, String lastview); // MUST I ALSO HAVE LASTVIEW HERE???????
    void deleteMovie(Movie movie);

    void updateMyRating(String title, int myRating);
    List<Movie> getAllMovies();

    List<Movie> getOldMovies();

    List<Movie> getFilterMovies(String movie);

   void updateLastview(int movieId);

}


