package com.example.wandy.waterwastereport.model;

public class PostModel {
    private int id;
    private String image_url;
    private String description;
    private String lat;
    private String lng;
    private String date;

    public PostModel(int id, String image_url, String description, String lat, String lng, String date){
        setId(id);
        setDescription(description);
        setImage_url(image_url);
        setLat(lat);
        setLng(lng);
        setDate(date);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }


}
