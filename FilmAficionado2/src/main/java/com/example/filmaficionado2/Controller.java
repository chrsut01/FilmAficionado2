
package com.example.filmaficionado2;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;



public class Controller {
    @FXML
    private Pane programPane;
    @FXML
    private TableView<Movie> movieTV = new TableView<Movie>();
    @FXML
    private TableColumn<Movie, Integer> myRating = new TableColumn();
    @FXML
    private TableColumn<Movie, String> title = new TableColumn();
    @FXML
    private TableColumn<Movie, String> imdbRating = new TableColumn();

    @FXML
    private ListView<Movie> catMovieLV;
    @FXML
    private ListView<Category> categoryLV;
    private boolean afspiller = false;
    @FXML
    private TextField filterText;
    @FXML
    private Label bestInCat;
    @FXML
    private Label nowPlaying;
    @FXML
    private MediaView mediaView;
    private MovieDao movieDao = new MovieDaoImpl();
    private CategoryDao categoryDao = new CategoryDaoImpl();
    private CatMovieDao catMovieDao = new CatMovieDaoImpl();
    private BestInCatDao bestInCatDao = new BestInCatDaoImpl();


    @FXML
    void initialize() {
        refreshMovieTV();
        refreshCategoryLV();
        showOldies();
    }

    // SETS UP A PRELIMINARY DIALOG AT THE VERY START OF PROGRAM, SUGGESTING A LIST OF MOVIES TO
    // POSSIBLY DELETE (EITHER WITH A LOW RATING OR NOT VIEWED IN THE LAST TWO YEARS)
    private void showOldies() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setHeaderText("Some movies you might want to delete...");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        Label lowRatingLabel = new Label();
        lowRatingLabel.setText("   ...you either don't watch or gave a low rating:");
        Label blankLabel = new Label();
        blankLabel.setText("");

        ListView oldMoviesLV = new ListView<>();
        List<Movie> movies = movieDao.getOldMovies();
        for (Movie movie : movies) {
            oldMoviesLV.getItems().add(movie);
            VBox box = new VBox(lowRatingLabel, blankLabel, oldMoviesLV);
            dialog.getDialogPane().setContent(box);
        }
        Optional<ButtonType> ok = dialog.showAndWait();
    }

    // UPLOADS ALL MOVIES TO MOVIE TABLEVIEW FROM START OF PROGRAM
    private void refreshMovieTV() {
        movieTV.getItems().clear();
        System.out.println(movieDao.getAllMovies());
        List<Movie> movies = movieDao.getAllMovies();
        for (Movie movie : movies) {
            movieTV.getItems().add(movie);
            myRating.setCellValueFactory(new PropertyValueFactory<Movie, Integer>("myRating"));
            title.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getMovieTitle()));
            imdbRating.setCellValueFactory(new PropertyValueFactory<Movie, String>("imdbRating"));
        }
    }

    // UPLOADS ALL CATEGORIES TO CATEGORY LISTVIEW FROM START OF PROGRAM
    private void refreshCategoryLV() {
        categoryLV.getItems().clear();
        System.out.println(categoryDao.getAllCategories());
        List<Category> categories = categoryDao.getAllCategories();
        for (Category category : categories) {
            categoryLV.getItems().add(category);
        }
    }

    // SETS UP DIALOG BOX TO ADD MOVIE TO DATABASE
    @FXML
    void newMovie(ActionEvent event) throws IOException {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("new movie dialog");
        dialog.setHeaderText("Add a new Movie");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField titleTF = new TextField();
        TextField myRatingTF = new TextField();
        TextField imdbRatingTF = new TextField();
        TextField filelinkTF = new TextField();
        TextField lastviewTF = new TextField();

        Label titleLabel = new Label();
        titleLabel.setText("Enter movie title:");
        Label myRatingLabel = new Label();
        myRatingLabel.setText("Enter rating (from 1-10):");
        Label imdbRatingLabel = new Label();
        imdbRatingLabel.setText("Enter IMDB rating (from 0.0-10.0):");
        Label fileLinkLabel = new Label();
        fileLinkLabel.setText("Enter filelink:");
        Label lastviewLabel = new Label();
        lastviewLabel.setText("Enter Date (YYYY-MM-DD):");

        VBox box = new VBox(titleLabel, titleTF, myRatingLabel, myRatingTF, imdbRatingLabel, imdbRatingTF, fileLinkLabel,
                filelinkTF, lastviewLabel, lastviewTF);
        dialog.getDialogPane().setContent(box);

        Optional<ButtonType> ok = dialog.showAndWait();
        if (ok.get() == ButtonType.OK)
            System.out.println("Title = " + titleTF.getText() + " My rating = " + myRatingTF.getText() + " IMDB rating = "
                    + imdbRatingTF.getText() + " Filelink = " + filelinkTF.getText() + " Lastview = " + lastviewTF.getText());

        int myRating = Integer.parseInt(myRatingTF.getText());
        movieDao.saveMovie(titleTF.getText(), myRating, imdbRatingTF.getText(), filelinkTF.getText(), lastviewTF.getText());
        refreshMovieTV();

        titleTF.clear();
        myRatingTF.clear();
        imdbRatingTF.clear();
        filelinkTF.clear();
    }
    // METHOD TO DELETE MOVIE FROM DATABASE
    @FXML
    void deleteMovie(ActionEvent event) {
        // SETS UP DIALOG TO ASK FOR CONFIRMATION BEFORE DELETING
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Delete movie");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Label confirm = new Label("Are you sure you want to continue?");
        dialog.getDialogPane().setContent(confirm);
        Optional<ButtonType> button = dialog.showAndWait();

        if (button.get() == ButtonType.OK)
            try {
                ObservableList<Integer> chosenIndex = movieTV.getSelectionModel().getSelectedIndices();
                if (chosenIndex.size() == 0)
                    System.out.println("Choose a movie");
                else
                    for (Object index : chosenIndex) {
                        System.out.println("You chose " + movieTV.getSelectionModel().getSelectedItem());
                        Movie m = (Movie) movieTV.getItems().get((int) index);
                        movieTV.getItems().remove(movieTV.getSelectionModel().getSelectedItem());
                        movieDao.deleteMovie(m);

                        // DELETES MOVIE FROM CatMovie LISTVIEW AND BestInCat LABEL
                        catMovieDao.removeMovieCat(m);
                        catMovieLV.getItems().clear();
                        Category category = (Category) categoryLV.getSelectionModel().getSelectedItem();
                        List<Movie> movies = catMovieDao.getAllMoviesOnCatMovie(category);
                        for (Movie movie : movies) {
                            catMovieLV.getItems().add(movie);
                            bestInCat.setText(bestInCatDao.showBestInCat(category.getCategoryID()));
                        }
                    }
            } catch (Exception e) {
                System.out.println("Cannot delete Movie " + e.getMessage());
            }
    }

    // SETS UP DIALOG BOX TO ADD CATEGORY TO DATABASE
    @FXML
    void newCategory(ActionEvent event) throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("new category dialog");
        dialog.setHeaderText("New Category");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField categoryTF = new TextField();
        Label nameLabel = new Label();
        nameLabel.setText("Enter category name:");
        VBox box = new VBox(nameLabel, categoryTF);
        dialog.getDialogPane().setContent(box);

        Optional<ButtonType> ok = dialog.showAndWait();
        if (ok.get() == ButtonType.OK)
            System.out.println("Category name = " + categoryTF.getText());

        Category category = new Category();
        categoryLV.getItems().add(category);

        categoryDao.saveCategory(categoryTF.getText());
        refreshCategoryLV();
        categoryTF.clear();
    }

    // METHOD TO DELETE CATEGORY FROM DATABASE
    @FXML
    void deleteCategory(ActionEvent event) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("delete category");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Label confirm = new Label("Deleting category will not delete any movies");
        dialog.getDialogPane().setContent(confirm);
        Optional<ButtonType> button = dialog.showAndWait();

        if (button.get() == ButtonType.OK)
            try {
                ObservableList<Integer> chosenIndex = categoryLV.getSelectionModel().getSelectedIndices();
                if (chosenIndex.size() == 0)
                    System.out.println("Choose a movie");
                else
                    for (Object index : chosenIndex) {
                        System.out.println("You chose " + categoryLV.getSelectionModel().getSelectedItem());
                        Category c = (Category) categoryLV.getItems().get((int) index);
                        categoryLV.getItems().remove(categoryLV.getSelectionModel().getSelectedItem());

                        categoryDao.deleteCategory(c);
                        catMovieLV.getItems().clear();
                    }
            } catch (Exception e) {
                System.out.println("Cannot delete Category " + e.getMessage());
            }
    }


    // ADDS A SELECTED MOVIE TO A SELECTED CATEGORY
    @FXML
    void addToCat(ActionEvent event) {
        ObservableList<Integer> chosenIndex = movieTV.getSelectionModel().getSelectedIndices();
        if (chosenIndex.size() != 0) {
            ObservableList<Integer> chosenIndex1 = categoryLV.getSelectionModel().getSelectedIndices();
            if (chosenIndex1.size() == 0)
                System.out.println("Choose a category");
            else
                for (Object index : chosenIndex) {
                    System.out.println("You chose " + movieTV.getSelectionModel().getSelectedItem());
                    Movie m = (Movie) movieTV.getItems().get((int) index);
                    Category category = (Category) categoryLV.getSelectionModel().getSelectedItem();
                    catMovieDao.addMovieCat(category.getCategoryID(), m.getMovieID());
                    List<Movie> movies = catMovieDao.getAllMoviesOnCatMovie(category);
                    catMovieLV.getItems().clear();
                    for (Movie movie : movies) {
                        catMovieLV.getItems().add(movie);
                    }
                }
        } else System.out.println("Choose a movie");
    }

    // DELETES A SELECTED MOVIE FROM A SELECTED CATEGORY (AND UPDATES BestInCat IF NECESSARY)
    @FXML
    void removeFromCat(ActionEvent event) {
        ObservableList<Integer> chosenIndex = catMovieLV.getSelectionModel().getSelectedIndices();
        if (chosenIndex.size() != 0) {
            for (Object index : chosenIndex) {
                Movie m = (Movie) catMovieLV.getItems().get((int) index);
                Category category = (Category) categoryLV.getSelectionModel().getSelectedItem();
                catMovieDao.removeMovieCat(m);
                List<Movie> movies = catMovieDao.getAllMoviesOnCatMovie(category);
                catMovieLV.getItems().remove(m);
                // REMOVES FROM DATABASE
                bestInCatDao.removeBestInCat(m);
                // REFRESHES FROM DATABASE TO SHOW: "none chosen" IN BestInCat
                bestInCat.setText(bestInCatDao.showBestInCat(category.getCategoryID()));
            }
        }
    }

    // CHOOSES A MOVIE AS BEST IN CATEGORY (AND DELETES ANY PREVIOUS)
    @FXML
    void BestInCat(MouseEvent event) {
        ObservableList<Integer> chosenIndex = catMovieLV.getSelectionModel().getSelectedIndices();
        if (chosenIndex.size() != 0) {
            ObservableList<Integer> chosenIndex1 = categoryLV.getSelectionModel().getSelectedIndices();
            if (chosenIndex1.size() == 0)
                System.out.println("Choose a category");
            else
                for (Object index : chosenIndex) {
                    System.out.println("You chose " + catMovieLV.getSelectionModel().getSelectedItem());

                    Movie m = (Movie) catMovieLV.getSelectionModel().getSelectedItem();
                    Category category = (Category) categoryLV.getSelectionModel().getSelectedItem();
                    bestInCatDao.addBestInCat(category.getCategoryID(), m.getMovieID());
                    System.out.println("it worked");

                    List<Movie> movies = catMovieDao.getAllMoviesOnCatMovie(category);
                    catMovieLV.getItems().clear();
                    for (Movie movie : movies) {
                        catMovieLV.getItems().add(movie);
                    }
                    bestInCat.setText(bestInCatDao.showBestInCat(category.getCategoryID()));
                }
        } else System.out.println("Choose a movie");
    }


    // SHOWS ALL MOVIES IN CatMovie LISTVIEW WHEN CLICKING ON A CATEGORY IN CategoryLV
    @FXML
    void showCatMovie(MouseEvent event) {
        System.out.println("showCatMovie mouse event works");
        catMovieLV.getItems().clear();
        Category category = (Category) categoryLV.getSelectionModel().getSelectedItem();
        List<Movie> movies = catMovieDao.getAllMoviesOnCatMovie(category);
        for (Movie movie : movies) {
            catMovieLV.getItems().add(movie);
        }
        bestInCat.setText(bestInCatDao.showBestInCat(category.getCategoryID()));
    }

    // SETS UP DIALOG TO CHANGE A MOVIE'S myRating AND SHOWS THE UPDATED CHANGE IN CatMovie LISTVIEW AS WELL AS Movie TABLEVIEW
    @FXML
    void updateRating(ActionEvent event) {
    // SETS UP DIALOG
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setHeaderText("Enter a new rating");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    // SETS UP LABELS SHOWING title, CURRENT myRating, AND TEXTFIELD FOR ADDING A NEW myRating
        Label titleLabel = new Label();
        titleLabel.setText("You selected: ");
        Label titleShow = new Label();
        Label blankLabel = new Label();
        blankLabel.setText("");
        Label myRatingLabel = new Label();
        myRatingLabel.setText("Current rating is: ");
        Label myRatingShow = new Label();
        Label blankLabel2 = new Label();
        blankLabel2.setText("");
        Label myNewRatingLabel = new Label();
        myNewRatingLabel.setText("New rating is: ");
        TextField myNewRatingTF = new TextField();

        VBox box = new VBox(titleLabel, titleShow, blankLabel, myRatingLabel, myRatingShow, blankLabel2, myNewRatingLabel,
                myNewRatingTF);
        dialog.getDialogPane().setContent(box);

        Movie selectedMovie = movieTV.getSelectionModel().getSelectedItem();

        titleShow.setText(selectedMovie.getMovieTitle());
        myRatingShow.setText(String.valueOf(selectedMovie.getMyRating()));
        myNewRatingTF.setText("");

        Optional<ButtonType> ok = dialog.showAndWait();
        if (ok.get() == ButtonType.OK)
            try {
                movieDao.updateMyRating(titleShow.getText(), Integer.parseInt(myNewRatingTF.getText()));
                // UPDATES RATING IN CatMovie LISTVIEW
                catMovieLV.getItems().clear();
                Category category = (Category) categoryLV.getSelectionModel().getSelectedItem();
                List<Movie> movies = catMovieDao.getAllMoviesOnCatMovie(category);
                for (Movie movie : movies) {
                    catMovieLV.getItems().add(movie);
                }
                // UPDATES RATING ALSO IN MOVIE TABLEVIEW
                refreshMovieTV();
                myNewRatingTF.clear();
            }
        catch (Exception e) {
            System.out.println("invalid entry info " + e.getMessage());
            // SETS UP DIALOG IF USER ENTERS A LETTER OR DOUBLE INSTEAD OF AN INTEGER
                Dialog<ButtonType> dialog2 = new Dialog<>();
                dialog2.setTitle("Invalid entry");
                dialog2.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
                Label confirm = new Label("Enter an integer between 1-10");
                dialog2.getDialogPane().setContent(confirm);
                Optional<ButtonType> button = dialog2.showAndWait();
            }
    }

    // FILTERS MOVIES IN MOVIE LISTVIEW BY title OR myRating OR imdbRATING
    @FXML
    void filterMovies(ActionEvent event) {
        movieTV.getItems().clear();
        List<Movie> movies = movieDao.getFilterMovies(filterText.getText());
        for (Movie movie : movies) {
            movieTV.getItems().add(movie);
        }
        filterText.clear();
    }

    // REFRESH BUTTON RELOADS ALL MOVIES TO TABLEVIEW
    @FXML
    void refreshAfterFilter(MouseEvent event) {
        refreshMovieTV();
    }

    // METHOD FOR PLAYING MOVIES
    Media movie;
    MediaPlayer mediaPlayer;
    @FXML
    void playMovie(ActionEvent event) throws IOException {
try {
    System.out.println(getFileFromSelected());

    movie = new Media(String.valueOf(getClass().getResource(getFileFromSelected())));
    mediaPlayer = new MediaPlayer(movie);
    mediaView.setMediaPlayer(mediaPlayer);
    mediaPlayer.seek(mediaPlayer.getStartTime());
    nowPlaying.setText(getTitleFromSelected());
    afspiller = true;
    mediaPlayer.play();
    movieDao.updateLastview(getSelectedMovieID());
    refreshMovieTV();
} catch (Exception e) {
            System.out.println("Cannot delete Movie " + e.getMessage());
        }
    }

    // TO STOP PLAYING MOVIE
    @FXML
    void stopMovie(ActionEvent event) throws IOException {
        if (afspiller)
            mediaPlayer.stop();
        afspiller = false;
        mediaPlayer.dispose();
    }

    // UNSELECTS MOVIE FROM CatMovie LISTVIEW WHEN CLICKING MOVIE TABLEVIEW
    @FXML
    void unselectCatMovieLV(MouseEvent event) {
        catMovieLV.getSelectionModel().clearSelection();
        nowPlaying.setText(null);
    }

    // UNSELECTS MOVIE FROM MOVIE TABLEVIEW WHEN CLICKING IN CatMovie LISTVIEW
    @FXML
    void unselectMovieTV(MouseEvent event) {
        movieTV.getSelectionModel().clearSelection();
        nowPlaying.setText(null);
    }

    // RETURNS MovieID FROM SELECTED MOVIE IN EITHER MOVIE TABLEVIEW OR CatMovie LISTVIEW
    private int getSelectedMovieID() {
        ObservableList<Integer> chosenIndex2 = catMovieLV.getSelectionModel().getSelectedIndices();
        if (chosenIndex2.size() != 0) {
            for (Object index : chosenIndex2) {
                Movie M = (Movie) catMovieLV.getItems().get((int) index);
                return M.getMovieID();
            }
        }
        ObservableList<Integer> chosenIndex1 = movieTV.getSelectionModel().getSelectedIndices();
        if (chosenIndex1.size() != 0) {
            for (Object index : chosenIndex1) {
                Movie m = (Movie) movieTV.getItems().get((int) index);
                return m.getMovieID();
            }
        }
        return 0;
    }
    // RETURNS MOVIE FILE FROM SELECTED MOVIE IN EITHER MOVIE TABLEVIEW OR CatMovie LISTVIEW
        private String getFileFromSelected() {
        ObservableList<Integer> chosenIndex2 = catMovieLV.getSelectionModel().getSelectedIndices();
        if (chosenIndex2.size() != 0) {
            for (Object index : chosenIndex2) {
                Movie M = (Movie) catMovieLV.getItems().get((int) index);
                return M.getFilelink();
            }
        }
        ObservableList<Integer> chosenIndex1 = movieTV.getSelectionModel().getSelectedIndices();
        if (chosenIndex1.size() != 0) {
            for (Object index : chosenIndex1) {
                Movie m = (Movie) movieTV.getItems().get((int) index);
                return m.getFilelink();
            }
        }
        return null;
    }

    // RETURNS TITLE FROM SELECTED MOVIE IN EITHER MOVIE TABLEVIEW OR CatMovie LISTVIEW
     private String getTitleFromSelected() {
        ObservableList<Integer> chosenIndex2 = catMovieLV.getSelectionModel().getSelectedIndices();
        if (chosenIndex2.size() != 0) {
            for (Object index : chosenIndex2) {
                Movie M = (Movie) catMovieLV.getItems().get((int) index);
                return M.getMovieTitle();
            }
        }
        ObservableList<Integer> chosenIndex1 = movieTV.getSelectionModel().getSelectedIndices();
        if (chosenIndex1.size() != 0) {
            for (Object index : chosenIndex1) {
                Movie m = (Movie) movieTV.getItems().get((int) index);
                return m.getMovieTitle();
            }
        }
        return null;
    }

    @FXML
    void closeProgram(ActionEvent event) {
        Stage stage = (Stage) programPane.getScene().getWindow();
        stage.close();
    }
}

