package com.arkay.gujaratquiz.model;

public class HomeCardModel {

    private String title;
    private int imageRes; // for local
    private String imageUrl; // for API (future)

    private String jsonFileName;

    public HomeCardModel(String title, int imageRes, String jsonFileName) {
        this.title = title;
        this.imageRes = imageRes;
        this.jsonFileName = jsonFileName;
    }

    // Future API constructor
    public HomeCardModel(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public int getImageRes() {
        return imageRes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getJsonFileName() {
        return jsonFileName;
    }
}
