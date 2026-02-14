package com.arkay.gujaratquiz.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class QuizData {
    @SerializedName("category_id")
    private int categoryId;

    private String category;

    @SerializedName("sub_categories")
    private List<SubCategory> subCategories;

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategory() {
        return category;
    }

    public List<SubCategory> getSubCategories() {
        return subCategories;
    }
}
