package com.example.seongje.l_recorder;

/**
 * Created by SEONGJE on 2017-12-04.
 */

public class Items {

    private String title;
    private String time;
    private String date;
    private int id;


    public Items(String title, String date, String time) {
        this.title = title;
        this.date = date;
        this.time = time;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
