package com.arkay.gujaratquiz.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SubCategory {
    @SerializedName("subcategory_id")
    private int subcategoryId;

    private String type;

    private List<Question> questions;

    public int getSubcategoryId() {
        return subcategoryId;
    }

    public String getType() {
        return type;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
