package com.jatin.musicplayer13.Model;


/**
 * creating a songslist class to get the title,subtitle and path of the song*/
public class SongsList {

    private String title;
    private String subTitle;
    private String path;

    /**
     * gets the path of the song*/
    public String getPath() {
        return path;
    }
    /**
     * sets the path of the song*/
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * using all the three title,subtitle and path in songlisting using a parameterizd constructor*/
    public SongsList(String title, String subTitle, String path) {
        this.title = title;
        this.subTitle = subTitle;
        this.path = path;

    }

    /**
     * gets the title of the song*/
    public String getTitle() {
        return title;
    }

    /**
     * sets the title of the song*/
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * gets the subtitle of the song*/
    public String getSubTitle() {
        return subTitle;
    }

    /**
     * sets the SubTitle of the song*/
    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

}
