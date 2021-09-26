package com.sanchit.enigmaque.hangman

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class HangmanViewModelFactory(val app: Application): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HangmanViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return HangmanViewModel(app) as T
        }
        throw IllegalArgumentException("Unable to build view model")
    }

}