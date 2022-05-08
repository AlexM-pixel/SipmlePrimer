package com.example.mysympleapplication.hw9.newDesign.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mysympleapplication.R
import dagger.android.support.DaggerAppCompatActivity

class MainActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
    }
}