package com.sanchit.enigmaque.sudoku

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class SudokuViewModelFactory(val app: Application) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SudokuViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return SudokuViewModel(app) as T
        }
        throw IllegalArgumentException("Unable to build View Model")
    }

}