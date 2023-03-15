package com.example.mysympleapplication.hw9.newDesign.utils

import com.chibatching.kotpref.KotprefModel
import com.example.mysympleapplication.R
import java.util.*

object Config {
    const val DEL_SPEND_DIALOG = "dialogToDeleteSpend"
    const val REQUEST_CODE = 1
    const val ALERT_DIALOG_ND = "alert_dialogNd"
    const val COUNT_ERRORS_PASSW = 3
    const val STORAGE_IMAGE = "image"
    const val FEEDBACK_EMAIL_ADDRESS = "pixelm.alex@gmail.com"
    const val FEEDBACK_SUBJECT = "Техподдержка расходы Android"
    const val FEEDBACK_SEND_EMAIL = "Отправить Email"
    const val CHANNEL_ID = "first_id_channel"
    const val DEF_SPEND_NAME = "produkti"
    const val GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL"
}

object MainPrefs : KotprefModel() {
    var mailUser: String by stringPref()
    var firstStart: Boolean by booleanPref(default = false)
    val setBankNames by stringSetPref {
        return@stringSetPref TreeSet<String>()
    }
    var stile: Int by intPref(default = R.style.AppTheme)
}

