package com.example.seongje.l_recorder;

/**
 * Created by SEONGJE on 2017-12-04.
 */

public class ListViewItems {

    private String title;
    private String date;
    private String time;
    private int id;


    public ListViewItems() {
        this.title = null;
        this.date = null;
        this.time = null;

    }

    public ListViewItems(String title, String date, String time) {
        this.date = date;
        this.time = time;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void add(String title, String date, String time) {
        this.title = title;
        this.date = date;
        this.time = time;
    }

}
