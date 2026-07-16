package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.PrefManager

class N8nApplication : Application() {
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        PrefManager.init(this)
        database = AppDatabase.getDatabase(this)
    }
}
