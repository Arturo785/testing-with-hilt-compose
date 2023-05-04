package com.plcoding.cleanarchitecturenoteapp

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication


// this is like creating our
/*
@HiltAndroidApp
class NoteApp : Application()
* */
// but now used for testing
// needs to be registered in the gradle file
class HiltTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}