package com.sanchit.enigmaque.hangman

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.sanchit.enigmaque.R
import com.sanchit.enigmaque.databinding.FragmentHangmanBinding
import java.lang.reflect.Type

class HangmanFragment : Fragment() {

    private lateinit var binding: FragmentHangmanBinding
    private lateinit var hangmanViewModel: HangmanViewModel
    private var currentIndex = 1
    private var fillIndex = 1
    private var imageIndex = 1
    private lateinit var listOfWords: ArrayList<String>
    private lateinit var listOfHints: ArrayList<String>
    private lateinit var mapOfWords: HashMap<Int,String>
    private lateinit var mapOfHints: HashMap<Int,String>
    private lateinit var listOfAlphabetTextView: ArrayList<MaterialTextView>
    private lateinit var listOfAlphabets: ArrayList<Char>
    private lateinit var hangmanImageSet: ArrayList<Int>
    private lateinit var arrayOfVisibilities: Array<Int>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.title = "Hangman"
        val activity = requireNotNull(this.activity)
        val application = activity.application
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_hangman, container, false)
        val viewModelFactory = HangmanViewModelFactory(application)
        hangmanViewModel = ViewModelProvider(this,viewModelFactory).get(HangmanViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = hangmanViewModel

        listOfAlphabetTextView = arrayListOf(binding.aTextView,binding.bTextView,binding.cTextView,binding.dTextView, binding.eTextView,
                binding.fTextView,binding.gTextView,binding.hTextView,binding.iTextView, binding.jTextView,binding.kTextView,
                binding.lTextView,binding.mTextView,binding.nTextView, binding.oTextView,binding.pTextView,binding.qTextView,
                binding.rTextView,binding.sTextView, binding.tTextView,binding.uTextView,binding.vTextView,binding.wTextView,
                binding.xTextView, binding.yTextView,binding.zTextView)

        hangmanImageSet = arrayListOf(R.drawable.hangman_image_1,R.drawable.hangman_image_2,R.drawable.hangman_image_3,R.drawable.hangman_image_4,R.drawable.hangman_image_5,R.drawable.hangman_image_6,R.drawable.hangman_image_7)

        listOfAlphabets = arrayListOf('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z')

        listOfWords = arrayListOf("Amazing","Beautiful","Crooked","Difference", "Delicious",
                "Dangerous","Funny","Happiness","Important","Interesting","Keeping","Mischievous","Predicament",
                "Scared","Trouble")

        listOfHints = arrayListOf("Hint: Incredible, Unbelievable, Astonishing",
                "Hint: Gorgeous, Dazzling, Magnificent","Hint: Bent, Twisted, Hooked","Hint: Disagreement, Inequity, Dissimilarity",
                "Hint: Savory, Delectable, Luscious","Hint: Perilous, Hazardous, Uncertain",
                "Hint: Humorous, Amusing, Laughable","Hint: Pleased, Contented, Delighted",
                "Hint: Necessary, Vital, Indispensable","Hint: Fascinating, Bright, Animated",
                "Hint: Holding, Maintaining, Supporting", "Hint: Prankish, Waggish, Sportive",
                "Hint: Quandary, Dilemma, Spot","Hint: Panicked, Fearful, Insecure", "Hint: Distress, Anguish, Wretchedness"
        )
        arrayOfVisibilities = arrayOf(View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,
                View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,
                View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,
                View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE, View.VISIBLE)

        mapOfWords = hashMapOf()
        mapOfHints = hashMapOf()
        for(i in 1..15){
            val temp = StringBuilder("")
            var low = 0
            while(low<listOfWords[i-1].length){
                if(low%2==0) {
                    temp.append(listOfWords[i - 1].get(low))
                }else{
                    temp.append("_")
                }
                low ++
            }
            mapOfWords[i] = temp.toString()
        }
        for(i in 1..15){
            mapOfHints[i] = listOfHints[i-1]
        }

        val sharedPreferences = application.getSharedPreferences("hangmanGame",Context.MODE_PRIVATE)
        imageIndex = sharedPreferences.getInt("imageIndex",0)
        fillIndex = sharedPreferences.getInt("fillIndex",1)
        currentIndex = sharedPreferences.getInt("currentIndex",1)
        val jsonString = sharedPreferences.getString("mapOfWords","")
        val jsonString2 = sharedPreferences.getString("arrayOfVisibilities","")
        if(jsonString=="") else mapOfWords = Gson().fromJson(jsonString,object : TypeToken<HashMap<Int, String>>() {}.type)
        if(jsonString2=="") else arrayOfVisibilities = Gson().fromJson(jsonString2,object : TypeToken<Array<Int>>() {}.type)

        binding.wordTextView.text = mapOfWords[currentIndex]
        binding.hintTextView.text = mapOfHints[currentIndex]
        for(i in 0..25){
            listOfAlphabetTextView[i].visibility = arrayOfVisibilities[i]
        }
        binding.hangmanImageView.setImageResource(hangmanImageSet[imageIndex])
        imageIndex++
        if(checkWin()){
            showAlertDialog()
        }

        for (i in 0..25){
            val temp = listOfAlphabetTextView[i]
            temp.setOnClickListener {
                if(imageIndex==hangmanImageSet.size-1){
                    binding.hangmanImageView.setImageResource(hangmanImageSet[6])
                    AlertDialog.Builder(activity)
                            .setTitle("Game lost!!")
                            .setMessage("You lost the game. Wanna try again?")
                            .setCancelable(false)
                            .setPositiveButton("Reset"){dialogInterface: DialogInterface, _: Int ->
                                reset()
                                dialogInterface.dismiss()
                            }.show()
                }else{
                    var flag = false
                    if(isCorrect(listOfAlphabets[i])){
                        flag = checkCompleted()
                        if(checkWin()){
                            flag = true
                            showAlertDialog()
                        }
                    }
                    if(!flag){
                        val s = listOfWords[currentIndex-1]
                        for(idx in fillIndex..s.length-1 step 2){
                            if(s.get(idx)==listOfAlphabets[i])
                                flag = true
                        }
                    }
                    if(!flag){
                        arrayOfVisibilities[i] = View.INVISIBLE
                        temp.visibility = View.INVISIBLE
                    }
                }
            }
        }
        return binding.root
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(activity)
                .setTitle("You won!!")
                .setMessage("You won the game. Wanna play again?")
                .setCancelable(false)
                .setPositiveButton("Reset"){dialogInterface: DialogInterface, _: Int ->
                    reset()
                    dialogInterface.dismiss()
                }.show()
    }

    private fun reset() {
        for(i in 1..15){
            val temp = StringBuilder("")
            var low = 0
            while(low<listOfWords[i-1].length){
                if(low%2==0) {
                    temp.append(listOfWords[i - 1].get(low))
                }else{
                    temp.append("_")
                }
                low ++
            }
            mapOfWords[i] = temp.toString()
        }
        for(i in 1..15){
            mapOfHints[i] = listOfHints[i-1]
        }
        imageIndex = 0
        binding.hangmanImageView.setImageResource(hangmanImageSet[imageIndex])
        imageIndex++
        fillIndex = 1
        currentIndex = 1
        binding.hintTextView.text = mapOfHints[currentIndex]
        binding.wordTextView.text = mapOfWords[currentIndex]
        hangmanViewModel.reset()
        for(i in 0..25){
            listOfAlphabetTextView[i].visibility = View.VISIBLE
            arrayOfVisibilities[i] = View.VISIBLE
        }
    }

    private fun checkWin(): Boolean{
        if(currentIndex==16){
            return true
        }
        return false
    }
    private fun checkCompleted(): Boolean{
        if(listOfWords[currentIndex-1] == mapOfWords[currentIndex]){
            imageIndex = 0
            binding.hangmanImageView.setImageResource(hangmanImageSet[imageIndex])
            imageIndex++
            currentIndex++
            binding.hintTextView.text = mapOfHints[currentIndex]
            binding.wordTextView.text = mapOfWords[currentIndex]
            hangmanViewModel.increaseScore()
            for(i in 0..25){
                listOfAlphabetTextView[i].visibility = View.VISIBLE
                arrayOfVisibilities[i] = View.VISIBLE
            }
            fillIndex = 1
            return true
        }
        return false
    }

    private fun isCorrect(ch: Char): Boolean{
        val sb = StringBuilder(mapOfWords[currentIndex]!!)
        if(listOfWords[currentIndex-1].get(fillIndex)==ch){
            sb.setCharAt(fillIndex,ch)
            val s = sb.toString()
            mapOfWords[currentIndex] = s
            binding.wordTextView.text = s
            fillIndex += 2
            return true
        }
        binding.hangmanImageView.setImageResource(hangmanImageSet[imageIndex])
        imageIndex++
        return false
    }

    override fun onPause() {
        val application = requireNotNull(this.activity).application
        val sharedPreferences = application.getSharedPreferences("hangmanGame",Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("score",hangmanViewModel.score.value!!)
        editor.putInt("imageIndex",imageIndex-1)
        editor.putInt("fillIndex",fillIndex)
        editor.putInt("currentIndex",currentIndex)

        val builder = GsonBuilder()
        val gson: Gson = builder.enableComplexMapKeySerialization().setPrettyPrinting().create()
        val type: Type = object : TypeToken<HashMap<Int, String>>() {}.type
        val json:String = gson.toJson(mapOfWords,type)
        val gson2 = Gson()
        val json2:String = gson2.toJson(arrayOfVisibilities)
        editor.putString("mapOfWords",json)
        editor.putString("arrayOfVisibilities",json2)
        editor.apply()
        super.onPause()
    }
}