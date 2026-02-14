package com.arkay.gujaratquiz.utils;

import com.arkay.gujaratquiz.R;
import com.arkay.gujaratquiz.model.HomeCardModel;

import java.util.ArrayList;
import java.util.List;

public class DummyData {

    public static List<HomeCardModel> getHomeCards() {
        List<HomeCardModel> list = new ArrayList<>();

        list.add(new HomeCardModel("ગુજરાત", R.drawable.gujarat, "Gujarat_questions.json"));
        list.add(new HomeCardModel("ભારત", R.drawable.india, "Bharat_questions.json"));
        list.add(new HomeCardModel("ધર્મ", R.drawable.dharma, "Dharma_questions.json"));
        list.add(new HomeCardModel("રાજનીતિ", R.drawable.politics, "Rajniti_questions.json"));
        list.add(new HomeCardModel("ઇતિહાસ", R.drawable.history, "History_questions.json"));
        list.add(new HomeCardModel("અર્થશાસ્ત્ર", R.drawable.economics, "Arthshastra_questions.json"));
        list.add(new HomeCardModel("ભૂગોળ", R.drawable.geography, "Geography_questions.json"));
        list.add(new HomeCardModel("સૌ પ્રથમ", R.drawable.people, "Firsts_questions.json"));
        list.add(new HomeCardModel("મહાન વ્યક્તિઓ", R.drawable.famous_people, "Great_personalities_questions.json"));

        return list;
    }
}
