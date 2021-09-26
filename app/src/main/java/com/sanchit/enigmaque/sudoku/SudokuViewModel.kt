package com.sanchit.enigmaque.sudoku

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SudokuViewModel(application: Application) : AndroidViewModel(application){
    private var _wrongAttempts = MutableLiveData<Int>()
    val wrongAttempts: LiveData<Int>
        get() = _wrongAttempts

    private var _time = MutableLiveData<Int>()
    val time: LiveData<Int>
        get() = _time

    init {
        val sharedPreferences = application.getSharedPreferences("sudokuDecoding", Context.MODE_PRIVATE)
        _wrongAttempts.value = sharedPreferences.getInt("wrongAttempts",0)
        _time.value = sharedPreferences.getInt("time",0)
    }

    fun increaseCount(){
        _wrongAttempts.value = _wrongAttempts.value!!.plus(1)
    }

    fun decreaseCount(){
        _wrongAttempts.value = _wrongAttempts.value!!.minus(1)
    }

    fun increaseTime(t: Int){
        _time.value = t
    }

    fun reset(){
        _wrongAttempts.value = 0
        _time.value = 0
    }

}