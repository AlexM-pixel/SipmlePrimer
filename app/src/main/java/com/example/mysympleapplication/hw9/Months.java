package com.example.mysympleapplication.hw9;

import com.example.mysympleapplication.R;

import java.util.ArrayList;

public enum Months {
    JANUARY(R.drawable.icon_january_48, "01","Январь"),
    FEBRUARY(R.drawable.icon_febr_48, "02","Февраль"),
    MARCH(R.drawable.icon_mart_48, "03","Март"),
    APRIL(R.drawable.icon_apr_48, "04","Апрель"),
    MAY(R.drawable.icon_may_48, "05","Май"),
    JUNE(R.drawable.icon_jun_48, "06","Июнь"),
    JULY(R.drawable.icon_july_48,"07","Июль"),
    AUGUST(R.drawable.icon_aug_48,"08","Август"),
    SEPTEMBER(R.drawable.icon_september_48,"09","Сентябрь"),
    OCTOBER(R.drawable.icon_october_48,"10","Октябрь"),
    NOVEMBER(R.drawable.icon_november_48,"11","Ноябрь"),
    DECEMBER(R.drawable.icon_dec_48,"12","Декабрь"),
    UNKNOWN_MONTH(R.drawable.ic_delete_forever_black_24dp,"--//--","дата отсутствует!");
    private int imageMonth;
    private String dateMonth;
    private String nameMonth;

    Months(int image, String month,String nameMonth) {
        this.imageMonth = image;
        this.dateMonth = month;
        this.nameMonth=nameMonth;
    }

   public static Months getMonth(String month) {
        for (Months months:Months.values()) {
            if (months.dateMonth.equals(month.substring(0,2))) {
               return months;
            }
        }
        return UNKNOWN_MONTH;
    }

    public int getImageMonth() {
        return imageMonth;
    }
    public String getNameMonth() {
        return nameMonth;
    }
}
