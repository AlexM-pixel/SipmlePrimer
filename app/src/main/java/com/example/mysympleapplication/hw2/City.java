package com.example.mysympleapplication.hw2;

import java.util.Arrays;
import java.util.List;

class City {
    private String title;
    private String description;

    City(String title, String description) {
        this.title = title;
        this.description = description;
    }

    static List<City> cityList = Arrays.asList(new City("Minsk", "Minsk is the city with a rich history. It was first mentioned in chronicles in 1067."),
            new City("London", "its political, economic and cultural centre. It's one of the largest cities in the world. Its population "));

    String getTitle() {
        return title;
    }

    String getDescription() {
        return description;
    }
}