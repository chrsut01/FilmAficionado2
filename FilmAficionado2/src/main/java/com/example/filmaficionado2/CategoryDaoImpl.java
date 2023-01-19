package com.example.filmaficionado2;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// IMPLEMENTS THE METHODS IN THE CategoryDao INTERFACE BY GIVING THEM A BODY AND BY COMMUNICATING WITH THE DATABASE
// THROUGH SQL STATEMENTS. THIS ALLOWS THE MODEL CONTROLLER TO ADD, DELETE AND RETRIEVE INFORMATION FROM
// THE DATABASE WITH REGARD TO CATEGORIES.
public class CategoryDaoImpl implements CategoryDao{

        private Connection con; // CONNECTS TO DATABASE

        public CategoryDaoImpl() {
            try {
                con = DriverManager.getConnection("jdbc:sqlserver://LAPTOP-F11OIRMM:1433;databaseName=FilmAficionado2;userName=sa;password=123456;encrypt=true;trustServerCertificate=true");
            } catch (SQLException e) {
                System.err.println("cannot create connection (CategoryDaoImpl)" + e.getMessage());
            }

            System.out.println("CategoryDaoImpl connected to the database... ");
        }

        public void saveCategory(String categoryName) {
            try {
                PreparedStatement ps = con.prepareStatement("INSERT INTO Category VALUES(?);");
                ps.setString(1, categoryName);

                ps.executeUpdate();

            } catch (SQLException e) {
                System.err.println("cannot insert record: " + e.getMessage());
            }
        }


    public void deleteCategory(Category category) {
            try {
                PreparedStatement pr = con.prepareStatement("DELETE FROM CatMovie WHERE categoryID = ?;");
                pr.setInt(1, (category.getCategoryID()));
                pr.executeUpdate();

                PreparedStatement ps = con.prepareStatement("DELETE FROM Category WHERE categoryID = ?;");
                ps.setInt(1, (category.getCategoryID()));
                ps.executeUpdate();

            } catch (SQLException e) {
                System.err.println("cannot delete category " + e.getMessage());
            }
        }

        public List<Category> getAllCategories() {
            List<Category> categories = new ArrayList();
            try {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM Category;");
                ResultSet rs = ps.executeQuery();

                Category category;
                while (rs.next()) {
                    int categoryID = rs.getInt(1);
                    String categoryName = rs.getString(2);

                    category = new Category(categoryID, categoryName);
                    categories.add(category);
                }

            } catch (SQLException e) {
                System.err.println("cannot access records (CategoryDaoImpl) " + e.getMessage());

            }
            return categories;
        }

}
