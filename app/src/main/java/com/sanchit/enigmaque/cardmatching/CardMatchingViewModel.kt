package com.sanchit.enigmaque.cardmatching

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CardMatchingViewModel(application: Application): AndroidViewModel(application){
    private var _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    var mapCount: HashMap<Int,ArrayList<Int>>

    init {
        val sharedPreferences = application.getSharedPreferences("cardSituation",Context.MODE_PRIVATE)
        _score.value = sharedPreferences.getInt("score",0)
        val jsonString = sharedPreferences.getString("map","")
        if(jsonString==""){
            mapCount = HashMap()
        }else{
            mapCount = Gson().fromJson(jsonString, object : TypeToken<HashMap<Int, ArrayList<Int>>>() {}.type)
        }
    }

   fun check(image: Int,id: Int): ArrayList<Int>{
        if(mapCount.get(image)==null){
            val list = arrayListOf<Int>()
            list.add(id)
            mapCount.put(image,list)
            return list
        }else{
            val list = mapCount.get(image)!!
            list.add(id)
            mapCount.put(image,list)
            _score.value = _score.value!!.plus(1)
            return list
        }
    }

    fun reset(){
        mapCount.entries.clear()
        mapCount.clear()
        _score.value = 0
    }
}