package edu.cuhk.csci3310.travelspot.model;

public class Spot {
    private int id;
    private String title;
    private double latitude;
    private double longitude;
    private String note;
    private String location;

    private String color;
    private int finish; //0 or 1 : false or true
    public Spot() {
    }

    public Spot(String title, double latitude, double longitude, String note, String location, String color, int finish) {
    //public Spot(String title, double latitude, double longitude, String note, String location, String color) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.note = note;
        this.location = location;
        this.color = color;
        this.finish = finish;
    }

    public Spot(int id, String title, double latitude, double longitude, String note, String location, String color, int finish) {
    //public Spot(int id, String title, double latitude, double longitude, String note, String location, String color) {
        this.id = id;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.note = note;
        this.location = location;
        this.color = color;
        this.finish = finish;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getFinish() { return finish; }

    public void setFinish(int finish) { this.finish = finish;}
}
