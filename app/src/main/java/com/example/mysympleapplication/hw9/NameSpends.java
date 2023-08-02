package com.example.mysympleapplication.hw9;

import com.example.mysympleapplication.R;

public enum NameSpends {
    SOSEDY(R.drawable.icon_shop_48, "SOSEDI", " Соседи", "produkti"),
    ZOOBAZAR(R.drawable.icon_zoo_48, "ZOOBAZAR", "Зообазар", "kotspopoy"),
    TELEPHONE(R.drawable.icon_mobile_48, "PO N TELEFONA:", "моб. телефон", "mobilnik"),
    EUROOPT(R.drawable.icon_shop_48, "EVROOPT", " Евроопт", "evroopt"),
    ZORINA(R.drawable.icon_shop_48, "ZORINA", " Зорина", "produkti"),
    VITALUR(R.drawable.icon_fish_48, "VITALUR", " Виталюр", "ribka"),
    SANTA(R.drawable.icon_santa, "SANTA", " SANTA", "icon_santa"),
    INTERNET(R.drawable.icon_internet_48, "1703017277003", "Интернет", "icon_internet"),
    INTERNET_A1(R.drawable.icon_internet_48, "ONLINE SERVICE/INTERNET", "Интернет", "icon_internet"),
    INTERNET_M(R.drawable.icon_internet_48, "ERIP TELECOM SERV", "Интернет", "icon_internet"),
    GIPPO(R.drawable.icon_shop_48, "GIPPO", " Гиппо", "produkti"),
    MILA(R.drawable.icon_mila_48, "MILA", " Мила", "bumaga_wc"),
    CASH(R.drawable.icon_cash_48, "Vydacha nalichnyh", "Снятие \n наличных", "cash_dollars"),
    CASH_BELINVESTBANK(R.drawable.icon_cash_48, "nalichye", "Снятие наличных", "cash_dollars"),
    CVETI(R.drawable.icon_flower_48, "cveti", "Цветы", "orhideya"),
    GEINER(R.drawable.icon_geiner_96, "GEINER", "Гейнер", "geiner"),
    HOUSETELEPHONE(R.drawable.icon_houstelephone_48, "SERVICE/TELEFON", "дом. телефон", "old_telephone"),
    ELECTROENERGIA(R.drawable.icon_flash_48, "SERVICE/ELEKTROENERGIJA", "Электроэнергия", "elrctro"),
    APTEKA(R.drawable.icon_apteka_48, "/APTEKA", "Аптека", "tabletki"),
    ZAPRAVKA(R.drawable.icon_azs_48, "AZS", "Заправка", "azs"),
    SMS_OPOVESHENIE(R.drawable.icon_sms_48, "SMS OPOVESCHENIE", "Смс-оповещение", "sms_ka"),
    WILDBERRIES(R.drawable.icon_wildberris_48, "WILDBERRIES", "Wildberries", "Wildberries"),
    KONKI_POKATUSHKI(R.drawable.icon_konki_48, "SDYUSHOR PO FIG. KATANIYU", "Массовые\n катания ", "konki"),
    BASMA(R.drawable.icon_basma_48, "BASMA", "BASMA", "parik"),
    KINOTEATR(R.drawable.icon_kino_48, "KINOTEATR", "Кинотеатр", "kino_3d"),
    RUBLEVSRKY(R.drawable.icon_shop_48, "RUBLEVSKIY", "Рублевский", "produkti"),
    OSTIN(R.drawable.icon_ostin_48, "OSTIN", "OSTIN \n магазин", "ostin"),
    MILE(R.drawable.icon_mile_48, "MILE", "MILE \n стройМагаз", "stroi_shop"),
    MATERIK(R.drawable.icon_mile_48, "MATERIK", "MATERIK \n стройМагаз", "stroi_shop"),
    BIGZZ(R.drawable.icon_shop_48, "HYPERMARKET BIGZZ", "BIGZZ \n магазин", "produkti"),
    KOMMUNALNIEPLATEGHI(R.drawable.icon_kommunalnie_48, "KOMMUNALNIE PLATEGHI", "Коммунальные \n платежи", "schetchik"),
    OZZ(R.drawable.icon_atrbook, "PRINOSIM RADOST", "OZ", "books_oz"),
    BUSLIK(R.drawable.icons_buslik, "BUSLIK", "Буслик", "busllik"),
    FUNTASTIK(R.drawable.icons_lego, "FUNTASTIK", "FUNTASTIK  магазин", "toy_lego"),
    APTEK(R.drawable.icon_apteka_48, " BELFARMATSIYA APTEK", "Аптека", "tabletki"),
    ELEMENT(R.drawable.element, "5 ELEMENT", "5 ЭЛЕМЕНТ", "element"),
    YANDEX(R.drawable.music, "Yandex", "Yandex музыка", "y_music"),
    MEGATOP(R.drawable.icon_megatop, "MEGATOP", "MEGATOP", "megatop"),
    KRUGHKI(R.drawable.icon_kruzok, "KRUGHKI V DETSKIH SADAH", "Д/С \nКружок", "icon_kruzok"),
    KOPEECHKA(R.drawable.kopeechka, "KOPEECHKA", "Копеечка", "kopeechka"),
    KOMMUNALNIEPLATEGHI_M(R.drawable.icon_kommunalnie_48, "ERIP UTILITIES I-BANK", "Коммунальные \n платежи", "schetchik"),
    PRIVATIZACIA(R.drawable.house, "ERIP GOVERNMENT PAY", "Приватизация", "priv_house"),
    TRI_TSENY(R.drawable.triceni, "TRI TSENY", "Три цены", "three_ceni"),
    MIR_KOSMETIKI(R.drawable.belita, "MIR KOSMETIKI", "Белита", "mir_cosmetiki"),
    NA_NEMIGE(R.drawable.icon_shop_48, "NA NEMIGE", "На Немиге", "produkti"),
    OSTROV_CHIST(R.drawable.ostrov, "OSTROV CHIST", "Остров Чистоты", "ostrov_chistoti"),
    GASTRONOM_SUPERPROD(R.drawable.icon_shop_48, "GASTRONOM SUPERPROD", "Магазин Суперпрод", "produkti"),
    SLADKIY_DOMIK(R.drawable.sladkiy, "SLADKIY DOMIK", "Сладкий домик", "sladkiy_house"),
    SMS_INFORM(R.drawable.icon_sms_48, "SMS-INFORM", "Смс-оповещение", "sms_ka"),
    SPORTMASTER(R.drawable.sport, "SPORTMASTER", "Спорт Мастер", "sport"),
    OMA(R.drawable.icon_mile_48, "STROITELN.GIPERMARKET 'OMA'", "ОМА", "stroi_shop"),
    OTHER(R.drawable.icon_shop_48,"OTHER","Другое","produkti");
    private int image;
    private String nameSpand;
    private String russianName;
    private String imageName;

    NameSpends(int imageR, String nameSpand, String russianS, String imageName) {
        this.image = imageR;
        this.nameSpand = nameSpand;
        this.russianName = russianS;
        this.imageName = imageName;
    }

    static int getImageForItem(String spendsName) {
        NameSpends[] nameSends = NameSpends.values();
        //  NameSends nameSend = null;
        for (NameSpends spend : nameSends) {
            if (spendsName.equals(spend.russianName)) {
                // nameSend = spend;
                return spend.image;
            }
        }
        // return nameSend.image;
        return ZORINA.image;
    }                             //     создавать при добавлении в ручную новую Спенду и новую Нэймспенд(категорию) категорию наверное создавать не надо
       // можно просто написать русское имя твоей Спенды и сумму затем в логике прохожу по всему списку СпендНеймов и
         //   есле тм в руНеймах(ruName) такой нет сохздаю новую категорию где в nameSpend просто продублирую ruName. Автозаполненме!

    public String getNameSpand() {
        return nameSpand;
    }

    public String getRussianName() {
        return russianName;
    }

    public String getImageName() {
        return imageName;
    }

    static boolean checkPay(String body) {
        NameSpends[] nameSends = NameSpends.values();
        for (int i = 0; i < nameSends.length; i++) {
            if (body.contains(nameSends[i].nameSpand)) {
                return true;
            }
        }
        return false;
    }

    static String getNameSpend(String body) {
        NameSpends[] nameSends = NameSpends.values();
        NameSpends nameSend = null;
        for (NameSpends spend : nameSends) {
            if (body.contains(spend.nameSpand)) {
                nameSend = spend;
                break;
            }
        }
        return nameSend.nameSpand;
    }
}
