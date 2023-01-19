package com.example.filmaficionado2;


import java.sql.*;

// IMPLEMENTS THE METHODS IN THE BestInCatDao INTERFACE BY GIVING THEM A BODY AND BY COMMUNICATING WITH THE DATABASE
// THROUGH SQL STATEMENTS. THIS ALLOWS THE MODEL CONTROLLER TO ADD, DELETE, UPDATE AND RETRIEVE INFORMATION FROM
// BestInCat TABLE IN THE DATABASE.
public class BestInCatDaoImpl implements BestInCatDao {

    private Connection con; // CONNECTS TO DATABASE

    public BestInCatDaoImpl() {
        try {
            con = DriverManager.getConnection("jdbc:sqlserver://LAPTOP-F11OIRMM:1433;databaseName=FilmAficionado2;" +
                    "userName=sa;password=123456;encrypt=true;trustServerCertificate=true");
        } catch (SQLException e) {
            System.err.println("cannot create connection (BestInCatDaoImpl)" + e.getMessage());
        }
        System.out.println("BestInCatDaoImpl connected to the database... ");
    }

    // ADDS MOVIE TO BestInCat (AFTER DELETING PREVIOUS MOVIE, IF ANY)
    @Override
    public void addBestInCat(int categoryID, int movieID) {
        try {
            PreparedStatement prs = con.prepareStatement("DELETE FROM BestInCat WHERE categoryID = ?;");
            prs.setInt(1, categoryID);
            prs.executeUpdate();

            PreparedStatement ps = con.prepareStatement("INSERT INTO BestInCat VALUES(?,?);");
            ps.setInt(1, movieID);
            ps.setInt(2, categoryID);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("cannot insert record (BestInCat) " + e.getMessage());
        }
    }

    // REMOVES MOVIE FROM A BestInCat
    public void removeBestInCat(Movie movie){
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM BestInCat WHERE movieID = ?;");
            ps.setInt(1, (movie.getMovieID()));

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Cannot delete movie (CatMovie) " + e.getMessage());
        }
    }


    public String showBestInCat(int categoryID) {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM BestInCat JOIN Movie ON BestInCat.movieID = Movie.movieID WHERE categoryID = ?");
            ps.setInt(1, categoryID);
            ResultSet rs = ps.executeQuery();
            rs.next();
            String title = rs.getString("title");
            return title;

        } catch (SQLException e) {
            System.err.println("cannot insert record (BestInCat) " + e.getMessage());
        }
        return "none chosen";
    }
}