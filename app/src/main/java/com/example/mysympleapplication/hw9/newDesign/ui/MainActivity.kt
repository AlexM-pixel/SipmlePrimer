package com.example.mysympleapplication.hw9.newDesign.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import dagger.android.support.DaggerAppCompatActivity
import kotlin.math.log

class MainActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(MainPrefs.stile)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
    }
}