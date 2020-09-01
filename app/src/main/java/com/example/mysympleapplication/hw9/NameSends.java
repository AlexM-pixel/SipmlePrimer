package com.example.mysympleapplication.hw9;

import com.example.mysympleapplication.R;

public enum NameSends {
    SOSEDY(R.drawable.icon_shop_48, "SOSEDI"," Соседи"),
    ZOOBAZAR(R.drawable.icon_zoo_48, "ZOOBAZAR"," Зообазар"),
    TELEPHONE(R.drawable.icon_mobile_48, "PO N TELEFONA:","моб. телефон"),
    EUROOPT(R.drawable.icon_shop_48, "EVROOPT"," Евроопт"),
    ZORINA(R.drawable.icon_shop_48, "ZORINA"," Зорина"),
    VITALUR(R.drawable.icon_fish_48, "VITALUR"," Виталюр"),
    INTERNET(R.drawable.icon_internet_48, "1703017277003","Интернет"),
    GIPPO(R.drawable.icon_shop_48, "GIPPO"," Гиппо"),
    MILA(R.drawable.icon_mila_48, "MILA"," Мила"),
    CASH(R.drawable.icon_cash_48, "Vydacha nalichnyh","Снятие \n наличных"),
    HOUSETELEPHONE(R.drawable.icon_houstelephone_48, "SERVICE/TELEFON","дом. телефон"),
    ELECTROENERGIA(R.drawable.icon_flash_48, "SERVICE/ELEKTROENERGIJA","Электроэнергия"),
    APTEKA(R.drawable.icon_apteka_48, "/APTEKA","Аптека"),
    ZAPRAVKA(R.drawable.icon_azs_48, "AZS","Заправка"),
    SMS_OPOVESHENIE(R.drawable.icon_sms_48, "SMS OPOVESCHENIE","Смс-оповещение"),
    WILDBERRIES(R.drawable.icon_wildberris_48, "WILDBERRIES","Wildberries"),
    KONKI_POKATUSHKI(R.drawable.icon_konki_48, "SDYUSHOR PO FIG. KATANIYU","Массовые\n катания "),
    BASMA(R.drawable.icon_basma_48, "BASMA","BASMA"),
    KINOTEATR(R.drawable.icon_kino_48, "KINOTEATR","Кинотеатр"),
    RUBLEVSRKY(R.drawable.icon_shop_48, "RUBLEVSKIY","Рублевский"),
    OSTIN(R.drawable.icon_ostin_48, "OSTIN","OSTIN \n магазин"),
    MILE(R.drawable.icon_mile_48, "MILE","MILE \n стройМагаз"),
    BIGZZ(R.drawable.icon_shop_48, "HYPERMARKET BIGZZ","BIGZZ \n магазин"),
    KOMMUNALNIEPLATEGHI(R.drawable.icon_kommunalnie_48, "KOMMUNALNIE PLATEGHI","Коммунальные \n платежи"),
    OZZ(R.drawable.icon_atrbook, "PRINOSIM RADOST","OZ"),
    SPORTMASTER(R.drawable.sport, "SPORTMASTER","Спорт Мастер");
    private int image;
    private String nameSpand;
    private String russianName;

    NameSends(int imageR, String nameSpand,String russianS) {
        this.image = imageR;
        this.nameSpand = nameSpand;
        this.russianName=russianS;

    }

    static int getImageForItem(String spendsName) {
        NameSends[] nameSends = NameSends.values();
      //  NameSends nameSend = null;
        for (NameSends spend : nameSends) {
            if (spendsName.equals(spend.russianName)) {
               // nameSend = spend;
                return spend.image;
            }
        }
       // return nameSend.image;
        return ZORINA.image;
    }

    public String getNameSpand() {
        return nameSpand;
    }

    public String getRussianName() {
        return russianName;
    }

    static boolean checkPay(String body) {
        NameSends[] nameSends = NameSends.values();
        for (int i = 0; i < nameSends.length; i++) {
            if (body.contains(nameSends[i].nameSpand)) {
                return true;
            }
        }
        return false;
    }

    static String getNameSpend(String body) {
        NameSends[] nameSends = NameSends.values();
        NameSends nameSend = null;
        for (NameSends spend : nameSends) {
            if (body.contains(spend.nameSpand)) {
                nameSend=spend;
                break;
            }
        }
        return nameSend.nameSpand;
    }
}
