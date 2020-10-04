package com.example.myapplication;

public class ContactsAll {

    int id;
    String filename;
    String size;
    String path;

    public ContactsAll(){

    }
    public ContactsAll(int id, String filename, String path, String size){
        this.id = id;
        this.filename = filename;
        this.path = path;
        this.size = size;

    }
    public ContactsAll(String filename, String path, String size){
        this.id = id;
        this.filename = filename;
        this.path = path;
        this.size = size;

    }
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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
