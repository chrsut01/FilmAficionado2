package com.example.filmaficionado2;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// IMPLEMENTS THE METHODS IN THE MOVIEDAO INTERFACE BY GIVING THEM A BODY AND BY COMMUNICATING WITH THE DATABASE
// THROUGH SQL STATEMENTS. THIS ALLOWS THE MODEL CONTROLLER TO ADD, DELETE, UPDATE AND RETRIEVE INFORMATION FROM
// MOVIE TABLE IN THE DATABASE.
public class MovieDaoImpl implements MovieDao{


    private Connection con;  // CONNECTS TO DATABASE

    public MovieDaoImpl() {
        try { con = DriverManager.getConnection("jdbc:sqlserver://LAPTOP-F11OIRMM:1433;databaseName=FilmAficionado2;userName=sa;password=123456;encrypt=true;trustServerCertificate=true");
        } catch (SQLException e) {
            System.err.println("cannot create connection" + e.getMessage());
        }
        System.out.println("MovieDaoImpl connected to the database... ");
    }

    public void saveMovie(String title, int myRating, String imdbRating, String filelink, String lastview) {
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO Movie VALUES(?,?,?,?,?);");
            ps.setString(1, title);
            ps.setInt(2, myRating);
            ps.setString(3, imdbRating);
            ps.setString(4, filelink);
            ps.setString(5, lastview);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("cannot insert record (saveMovie) " + e.getMessage());
        }
    }

    public void deleteMovie(Movie movie) {
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM BestInCat WHERE movieID = ?");
            ps.setInt(1, (movie.getMovieID()));
            ps.executeUpdate();

            PreparedStatement prs = con.prepareStatement("DELETE FROM CatMovie WHERE movieID = ?");
            prs.setInt(1, (movie.getMovieID()));
            prs.executeUpdate();

            PreparedStatement pr = con.prepareStatement("DELETE FROM Movie WHERE movieID = ?;");
            pr.setInt(1, (movie.getMovieID()));
            pr.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Cannot delete movie "+ e.getMessage());
        }
    }

    @Override
    public void updateMyRating(String title, int myRating) {
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE Movie SET myRating = ? WHERE title = ?;");
            ps.setInt(1, myRating);
            ps.setString(2, title);

            ps.executeUpdate();
            System.err.println("myRating updated successfully: ");

        } catch (SQLException e) {
            System.err.println("cannot insert record: " + e.getMessage());
        }
    }


    @Override
    public void updateLastview(int movieID) {
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE Movie SET lastview = getDate() WHERE movieID = ?;");
            ps.setInt(1, movieID);

            ps.executeUpdate();
            System.err.println("Lastview updated successfully: ");

        } catch (SQLException e) {
            System.err.println("cannot insert record: " + e.getMessage());
        }
    }


    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Movie;");
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
                movies.add(movie);
            }

        } catch (SQLException e) {
            System.err.println("cannot access AllMovies (MovieDaoImpl) " + e.getMessage());
        }
        return movies;
    }

    @Override
    public List<Movie> getOldMovies() {
        List<Movie> movies = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Movie WHERE myRating < 4 OR " +
                    "lastview < DATEADD(year, -2, GETDATE());");

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
                movies.add(movie);
            }
        } catch (SQLException e) {
            System.err.println("cannot access OldMovies (MovieDaoImpl) " + e.getMessage());
        }
        return movies;
    }

    @Override
    public List<Movie> getFilterMovies(String movie) {
        List<Movie> movies = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Movie WHERE title LIKE ? OR myRating LIKE ? OR imdbRating LIKE ?");
            ps.setString(1, movie);
            ps.setString(2, movie);
            ps.setString(3, movie);

            ResultSet rs = ps.executeQuery();

            Movie m;
            while (rs.next()) {
                int movieID = rs.getInt(1);
                String title = rs.getString(2);
                int myRating = rs.getInt(3);
                String imdbRating = rs.getString(4);
                String filelink = rs.getString(5);
                String lastview = rs.getString(6);

                m = new Movie(movieID, title, myRating, imdbRating, filelink, lastview);
                movies.add(m);
                System.err.println("It worked!");
            }
        } catch (SQLException e) {
            System.err.println("cannot access FilterMovies (MovieDaoImpl) " + e.getMessage());
        }
        return movies;
    }
}
