package com.example.filmaficionado2;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// IMPLEMENTS THE METHODS IN THE CatMovieDao INTERFACE BY GIVING THEM A BODY AND BY COMMUNICATING WITH THE DATABASE
// THROUGH SQL STATEMENTS. THIS ALLOWS THE MODEL CONTROLLER TO ADD, DELETE AND RETRIEVE INFORMATION FROM
// THE CatMovie TABLE IN THE DATABASE.
public class CatMovieDaoImpl implements CatMovieDao{


    private Connection con; // CONNECTS TO DATABASE

    public CatMovieDaoImpl() {
        try {
            con = DriverManager.getConnection("jdbc:sqlserver://LAPTOP-F11OIRMM:1433;databaseName=FilmAficionado2;userName=sa;password=123456;encrypt=true;trustServerCertificate=true");
        } catch (SQLException e) {
            System.err.println("cannot create connection (CatMovieDaoImpl)" + e.getMessage());
        }
        System.out.println("CatMovieDaoImpl connected to the database... ");
    }
// ADDS A SELECTED MOVIE TO A SELECTED CATEGORY
    public void addMovieCat(int categoryID, int movieID) {
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO CatMovie VALUES(?,?);");
            ps.setInt(1, movieID);
            ps.setInt(2, categoryID);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("cannot insert record (CatMovie) " + e.getMessage());
        }
    }

    // REMOVES MOVIE FROM A CATEGORY
    public void removeMovieCat(Movie movie){
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM CatMovie WHERE movieID = ?;");
            ps.setInt(1, (movie.getMovieID()));

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Cannot delete movie (CatMovie) " + e.getMessage());
        }
    }

    // SELECTS ALL MOVIES IN A GIVEN CATEGORY
    @Override
    public List<Movie> getAllMoviesOnCatMovie(Category category) {
        List<Movie> catMovie = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Movie, Category, CatMovie WHERE " +
                    "Category.categoryID = CatMovie.categoryID AND CatMovie.movieID = Movie.movieID AND " +
                    "Category.categoryID = ?;");
            ps.setInt(1, category.getCategoryID());
            ResultSet rs = ps.executeQuery();
            Movie movie;
            while (rs.next()) {
                int movieID = rs.getInt(1);
                String title = rs.getString(2);
                int  myRating = rs.getInt(3);
                String imdbRating = rs.getString(4);
                String filelink = rs.getString(5);
                String lastview = rs.getString(6);

                movie = new Movie(movieID, title, myRating, imdbRating, filelink, lastview);
                catMovie.add(movie);
            }
        } catch (SQLException e) {
            System.err.println("cannot access records (MDaoImpl) " + e.getMessage());
        }
        return catMovie;
    }
}

