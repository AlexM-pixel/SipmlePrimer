package com.example.mysympleapplication.hw2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Cities {
    private String title;
    private String description;

    Cities(String title, String description) {
        this.title = title;
        this.description = description;
    }

    static List<Cities> citiesList = Arrays.asList(new Cities("Minsk", "Minsk is the city with a rich history. It was first mentioned in chronicles in 1067."),
            new Cities("London", "its political, economic and cultural centre. It's one of the largest cities in the world. Its population "));

    String getTitle() {
        return title;
    }

    String getDescription() {
        return description;
    }
}