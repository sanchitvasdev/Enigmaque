package com.sanchit.enigmaque.hangman

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class HangmanViewModel(application: Application): AndroidViewModel(application){
    private var _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    init {
        val sharedPreferences = application.getSharedPreferences("hangmanGame",Context.MODE_PRIVATE)
        _score.value = sharedPreferences.getInt("score",0)
    }

    fun increaseScore(){
        _score.value = _score.value!!.plus(1)
    }

    fun reset(){
        _score.value = 0
    }
}