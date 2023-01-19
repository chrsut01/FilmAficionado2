package com.example.filmaficionado2;

// CREATES MOVIE CLASS
public class Movie {
    private int movieID;
    private String title;
    private int myRating;
    private String imdbRating;
    private String filelink;
    private String lastview;

    public String toString() { return myRating + " - " + title;}

// CREATES MOVIE OBJECT
    public Movie(int movieID, String title, int myRating, String imdbRating, String filelink, String lastview) {
        this.movieID = movieID;
        this.title = title;
        this.myRating = myRating;
        this.imdbRating = imdbRating;
        this.filelink = filelink;
        this.lastview = lastview;
    }

    public int getMovieID() { return this.movieID;
    }
    public String getMovieTitle() { return this.title;
    }
    public int getMyRating() { return this.myRating;
    }
    public String getImdbRating() { return this.imdbRating;
    }
    public String getFilelink() { return this.filelink;
    }
}
