package com.example.mysympleapplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cities {
    String title;
    String description;

    Cities(String title, String description) {
        this.title= title;
        this.description= description;
    }
  public static   List<Cities> citiesList = Arrays.asList(new Cities("Minsk", "Minsk is the city with a rich history. It was first mentioned in chronicles in 1067."),
            new Cities("London", "its political, economic and cultural centre. It's one of the largest cities in the world. Its population "));

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
