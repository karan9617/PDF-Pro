package com.example.myapplication;

public class Contacts {

    int id;
    String filename;
    String size;
    String path;
    String time;
    public Contacts(){

    }
    public Contacts(int id, String filename, String path, String size, String time){
        this.id = id;
        this.filename = filename;
        this.path = path;
        this.size = size;
        this.time = time;

    }
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public String getPath() {
        return path;
    }


}
