package com.example.mysympleapplication.hw9.newDesign.domain.model

import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.NameSpends

enum class Images(val imageSource: Int, val nameImage: String) {
    SOSEDY(R.drawable.produkti, "produkti"),
    ZOOBAZAR(R.drawable.kotspopoy, "kotspopoy"),
    TELEPHONE(R.drawable.mobilnik, "mobilnik"),
    EUROOPT(R.drawable.evroopt, "evroopt"),
    VITALUR(R.drawable.ribka, "ribka"),
    SANTA(R.drawable.icon_santa, "icon_santa"),
    INTERNET(R.drawable.icon_internet, "icon_internet"),
    GIPPO(R.drawable.gippo_logo, "gippo_logo"),
    MILA(R.drawable.bumaga_wc, "bumaga_wc"),
    CASH(R.drawable.cash_dollars, "cash_dollars"),
    CVETI(R.drawable.orhideya, "orhideya"),
    GEINER(R.drawable.geiner, "geiner"),
    HOUSETELEPHONE(R.drawable.old_telephone, "old_telephone"),
    ELECTROENERGIA(R.drawable.elrctro, "elrctro"),
    APTEKA(R.drawable.tabletki, "tabletki"),
    ZAPRAVKA(R.drawable.azs, "azs"),
    SMS_OPOVESHENIE(R.drawable.sms_ka, "sms_ka"),
    WILDBERRIES(R.drawable.wildberries, "Wildberries"),
    KONKI_POKATUSHKI(R.drawable.konki, "konki"),
    BASMA(R.drawable.parik, "parik"),
    KINOTEATR(R.drawable.kino_3d, "kino_3d"),
    OSTIN(R.drawable.ostin, "ostin"),
    MILE(R.drawable.stroi_shop, "stroi_shop"),
    KOMMUNALNIEPLATEGHI(R.drawable.schetchik, "schetchik"),
    OZZ(R.drawable.books_oz, "books_oz"),
    BUSLIK(R.drawable.busllik, "busllik"),
    FUNTASTIK(R.drawable.toy_lego, "toy_lego"),
    ELEMENT(R.drawable.element, "element"),
    YANDEX(R.drawable.y_music, "y_music"),
    MEGATOP(R.drawable.megatop, "megatop"),
    KRUGHKI(R.drawable.icon_kruzok, "icon_kruzok"),
    KOPEECHKA(R.drawable.kopeechka, "kopeechka"),
    PRIVATIZACIA(R.drawable.priv_house, "priv_house"),
    TRI_TSENY(R.drawable.triceni, "three_ceni"),
    MIR_KOSMETIKI(R.drawable.mir_cosmetiki, "mir_cosmetiki"),
    OSTROV_CHIST(R.drawable.milo, "ostrov_chistoti"),
    SLADKIY_DOMIK(R.drawable.sladkiy_house, "sladkiy_house"),
    SPORTMASTER(R.drawable.sport, "sport"),
    KOFTA(R.drawable.kofta, "kofta"),
    SUMKA_MONEY(R.drawable.money_sumka, "money_sumka"),
    POHOR(R.drawable.pohor, "pohorini"),
    PROTEIN(R.drawable.protein, "protein"),
    SUMKA(R.drawable.sumka, "simple_sumka"),
    ZUB(R.drawable.tooth, "tooth"),
    VANNA(R.drawable.vanna, "vanna");

    companion object {
         fun getImageForItem(spendsName: String?): Int {
            for (image in values()) {
                if (spendsName == image.nameImage) {
                    return image.imageSource
                }
            }
            return SOSEDY.imageSource
        }
    }
}