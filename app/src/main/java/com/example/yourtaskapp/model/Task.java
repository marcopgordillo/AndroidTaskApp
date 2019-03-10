package com.example.yourtaskapp.model;

import java.util.Date;

public class Task {

    private String title;
    private String note;
    private Date date;

    public Task() {
    }

    public Task(String title, String note, Date date) {
        this.title = title;
        this.note = note;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
