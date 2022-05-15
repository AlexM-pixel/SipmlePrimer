package com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository

import com.example.mysympleapplication.hw9.SumSpendsOfMonth

interface SpendsDataBaseRepository {
  fun  getSumSpendsOfMonth(): List<SumSpendsOfMonth>
}