package com.sanchit.enigmaque.sudoku

import android.app.ActionBar
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sanchit.enigmaque.R
import com.sanchit.enigmaque.databinding.FragmentSudokuBinding

class SudokuFragment : Fragment() {
    private lateinit var binding: FragmentSudokuBinding
    private lateinit var viewModel: SudokuViewModel
    private lateinit var sudokuArray: ArrayList<ArrayList<EditText>>
    private lateinit var sharedArray: ArrayList<ArrayList<Int>>
    private lateinit var sudoku: Array<IntArray>
    private lateinit var ans: Array<IntArray>
    private lateinit var listSudoku: ArrayList<Array<IntArray>>
    private lateinit var listAns: ArrayList<Array<IntArray>>
    private var listIndex = 0
    private var flag = 0
    private var t: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.title = "Sudoku Decoding"
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sudoku, container, false)
        val application = requireNotNull(this.activity).application
        val viewModelFactory = SudokuViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(SudokuViewModel::class.java)

        val sharedPreferences = application.getSharedPreferences("sudokuDecoding",Context.MODE_PRIVATE)
        t = sharedPreferences.getInt("time",0)
        binding.countDownTimer.setOnChronometerTickListener { cArg ->
            var time = SystemClock.elapsedRealtime() - cArg.base
            time +=  t
            viewModel.increaseTime(time.toInt())
            val m = (time).toInt() / 60000
            val s = (time - m * 60000).toInt() / 1000
            val mm = if (m < 10) "0$m" else m.toString()
            val ss = if (s < 10) "0$s" else s.toString()
            cArg.text = "$mm:$ss/10:00"
        }
        binding.countDownTimer.base = SystemClock.elapsedRealtime()
        binding.countDownTimer.start()

        listSudoku = arrayListOf()
        listSudoku.ensureCapacity(3)
        listAns = arrayListOf()
        listAns.ensureCapacity(3)

        sudoku = arrayOf(intArrayOf(0,3,0,0,1,0,0,6,0), intArrayOf(7,5,0,0,3,0,0,4,8), intArrayOf(0,0,6,9,8,4,3,0,0), intArrayOf(0,0,3,0,0,0,8,0,0), intArrayOf(9,1,2,0,0,0,6,7,4), intArrayOf(0,0,4,0,0,0,5,0,0), intArrayOf(0,0,1,6,7,5,2,0,0), intArrayOf(6,8,0,0,9,0,0,1,5), intArrayOf(0,9,0,0,4,0,0,3,0))
        ans = arrayOf(intArrayOf(4,3,8,5,1,7,9,6,2), intArrayOf(7,5,9,2,3,6,1,4,8), intArrayOf(1,2,6,9,8,4,3,5,7), intArrayOf(5,7,3,4,6,9,8,2,1), intArrayOf(9,1,2,8,5,3,6,7,4), intArrayOf(8,6,4,7,2,1,5,9,3), intArrayOf(3,4,1,6,7,5,2,8,9), intArrayOf(6,8,7,3,9,2,4,1,5), intArrayOf(2,9,5,1,4,8,7,3,6))

        val sudoku1 = arrayOf(intArrayOf(0,3,0,0,1,0,0,6,0), intArrayOf(7,5,0,0,3,0,0,4,8), intArrayOf(0,0,6,9,8,4,3,0,0), intArrayOf(0,0,3,0,0,0,8,0,0), intArrayOf(9,1,2,0,0,0,6,7,4), intArrayOf(0,0,4,0,0,0,5,0,0), intArrayOf(0,0,1,6,7,5,2,0,0), intArrayOf(6,8,0,0,9,0,0,1,5), intArrayOf(0,9,0,0,4,0,0,3,0))
        val ans1 = arrayOf(intArrayOf(4,3,8,5,1,7,9,6,2), intArrayOf(7,5,9,2,3,6,1,4,8), intArrayOf(1,2,6,9,8,4,3,5,7), intArrayOf(5,7,3,4,6,9,8,2,1), intArrayOf(9,1,2,8,5,3,6,7,4), intArrayOf(8,6,4,7,2,1,5,9,3), intArrayOf(3,4,1,6,7,5,2,8,9), intArrayOf(6,8,7,3,9,2,4,1,5), intArrayOf(2,9,5,1,4,8,7,3,6))
        val sudoku2 = arrayOf(intArrayOf(5,3,0,0,7,0,0,0,0), intArrayOf(6,0,0,1,9,5,0,0,0), intArrayOf(0,9,8,0,0,0,0,6,0), intArrayOf(8,0,0,0,6,0,0,0,3), intArrayOf(4,0,0,8,0,3,0,0,1), intArrayOf(7,0,0,0,2,0,0,0,6), intArrayOf(0,6,0,0,0,0,2,8,0), intArrayOf(0,0,0,4,1,9,0,0,5), intArrayOf(0,0,0,0,8,0,0,7,9))
        val ans2 = arrayOf(intArrayOf(5,3,4,6,7,8,9,1,2), intArrayOf(6,7,2,1,9,5,3,4,8), intArrayOf(1,9,8,3,4,2,5,6,7), intArrayOf(8,5,9,7,6,1,4,2,3), intArrayOf(4,2,6,8,5,3,7,9,1), intArrayOf(7,1,3,9,2,4,8,5,6), intArrayOf(9,6,1,5,3,7,2,8,4), intArrayOf(2,8,7,4,1,9,6,3,5), intArrayOf(3,4,5,2,8,6,1,7,9))
        val sudoku3 = arrayOf(intArrayOf(0,0,2,7,8,0,0,0,3), intArrayOf(0,0,0,0,0,9,8,0,1), intArrayOf(4,0,0,0,0,3,0,7,0), intArrayOf(9,0,5,0,0,8,0,0,0), intArrayOf(0,0,0,0,7,0,0,0,0), intArrayOf(0,0,0,5,0,0,4,0,8), intArrayOf(0,6,0,4,0,0,0,0,7), intArrayOf(3,0,9,8,0,0,0,0,0), intArrayOf(8,0,0,0,3,1,6,0,0))
        val ans3 = arrayOf(intArrayOf(1,9,2,7,8,4,5,6,3), intArrayOf(5,3,7,6,2,9,8,4,1), intArrayOf(4,8,6,1,5,3,9,7,2), intArrayOf(9,1,5,3,4,8,7,2,6), intArrayOf(6,4,8,9,7,2,1,3,5), intArrayOf(7,2,3,5,1,6,4,9,8), intArrayOf(2,6,1,4,9,5,3,8,7), intArrayOf(3,5,9,8,6,7,2,1,4), intArrayOf(8,7,4,2,3,1,6,5,9))
        listSudoku.add(sudoku1)
        listSudoku.add(sudoku2)
        listAns.add(ans1)
        listAns.add(ans2)
        listSudoku.add(sudoku3)
        listAns.add(ans3)

        listIndex = sharedPreferences.getInt("listIndex",0)
        for(i in 0..8){
            sudoku[i] = listSudoku[listIndex][i]
        }
        for(i in 0..8){
            ans[i] = listAns[listIndex][i]
        }

        val activity = requireNotNull(this.activity)
        val arrayOfRows = arrayOf(binding.row1,binding.row2,binding.row3,binding.row4,
                binding.row5,binding.row6,binding.row7,binding.row8,binding.row9)
        sudokuArray = arrayListOf(arrayListOf())
        sudokuArray.ensureCapacity(9)

        sharedArray = arrayListOf(arrayListOf())
        sharedArray.ensureCapacity(9)

        for(i in 0..8){
            val arr = arrayListOf<EditText>()
            arr.ensureCapacity(9)
            val sharedarr = arrayListOf<Int>()
            sharedArray.ensureCapacity(9)
            val temp  = arrayOfRows[i]
            for( j in 0..8){
                val editTextClass = EditText(activity,resources,ans[i][j].toString())
                arr.add(j,editTextClass)
                sharedarr.add(j,sudoku[i][j])
                if((j+1)%3==0&&j!=0){
                    editTextClass.increaseRightStrokeWidth()
                }else if((i+1)%3==0&&i!=0){
                    editTextClass.increaseBottomStrokeWidth()
                }
                if(((j+1)%3==0)&&((i+1)%3==0)){
                    if(i==0||j==0){
                    }else{
                        editTextClass.increaseRightBottomStrokeWidth()
                    }
                }
                if(j==0){
                    if(i==0) {
                        editTextClass.increaseLeftTopStrokeWidth()
                    }else if(i==8){
                        editTextClass.increaseLeftBottomStrokeWidth()
                    }else{
                        if((i+1)%3==0){
                            editTextClass.increaseLeftBottomStrokeWidth()
                        }else{
                            editTextClass.increaseLeftStrokeWidth()
                        }
                    }
                }
                if(i==0){
                    if(j>0&&j<=7){
                        if((j+1)%3==0){
                            editTextClass.increaseRightTopStrokeWidth()
                        }else{
                            editTextClass.increaseTopStrokeWidth()
                        }
                    }else if(j==8){
                        editTextClass.increaseRightTopStrokeWidth()
                    }
                }
                if(sudoku[i][j]==0){
                    editTextClass.changeText("")
                }else{
                    editTextClass.changeText(sudoku[i][j].toString())
                    editTextClass.changeEditable()
                }
                temp.addView(editTextClass.editText)
            }
            sudokuArray.add(i,arr)
            sharedArray.add(i,sharedarr)
        }
        val jsonString = sharedPreferences.getString("sharedArray","")
        if(jsonString=="") else sharedArray = Gson().fromJson(jsonString, object : TypeToken<ArrayList<ArrayList<Int>>>() {}.type)

        for (i in 0..8){
            for(j in 0..8){
                val element = sudokuArray[i][j]
                element.editText.setText(if(sharedArray[i][j]==0) "" else sharedArray[i][j].toString())
                if(element.editText.text.toString()==""){
                    element.editText.setTextColor(ResourcesCompat.getColor(resources,R.color.blue,null))
                }else if(element.editText.text.toString()==element.ans){
                    element.editText.setTextColor(ResourcesCompat.getColor(resources,R.color.blue,null))
                    if(element.editText.text.toString().toInt()==sudoku[i][j]){
                        element.editText.setTextColor(ResourcesCompat.getColor(resources,R.color.secondaryTextColor,null))
                    }
                }else{
                    element.editText.setTextColor(ResourcesCompat.getColor(resources,R.color.brightRed,null))
//                    viewModel.increaseCount()
                }
            }
        }
        if(isSolved()){
            binding.countDownTimer.stop()
            showAlertDialog()
        }

//        if(viewModel.wrongAttempts.value!!>0){
//            viewModel.decreaseCount()
//        }
        for(i in 0..8){
            for(j in 0..8){
                val element = sudokuArray[i][j]
                element.editText.addTextChangedListener {
                    if(it.toString()==""){
                        element.editText.setTextColor(ResourcesCompat.getColor(resources,R.color.blue,null))
                        sharedArray[i][j] = 0
                    }else if(it.toString()==element.ans){
                        element.editText.setTextColor(ResourcesCompat.getColor(resources,R.color.blue,null))
                        sharedArray[i][j] = it.toString().toInt()
                        if(isSolved()){
                            binding.countDownTimer.stop()
                            showAlertDialog()
                        }
                    }else{
                        if(flag==2||flag==0){
                            element.editText.setTextColor(ResourcesCompat.getColor(resources,R.color.brightRed,null))
                            sharedArray[i][j] = it.toString().toInt()
                            viewModel.increaseCount()
                        }
                    }
                }
            }
        }

        viewModel.wrongAttempts.observe(viewLifecycleOwner, Observer {
            binding.wrongAttemptsTextView.text = resources.getString(R.string.wrong_attempts,it)
            if(it==3){
                AlertDialog.Builder(activity)
                        .setTitle("Ran out of attempts")
                        .setMessage("You have ran out of attempts. Wanna try it again?")
                        .setCancelable(false)
                        .setPositiveButton("Try Again!") { dialogInterface: DialogInterface, _: Int ->
                            resetSudoku(2)
                            dialogInterface.dismiss()
                        }
                        .show()
            }
        })
        viewModel.time.observe(viewLifecycleOwner, Observer {
            if(it>=570000){
                binding.countDownTimer.setTextColor(ResourcesCompat.getColor(resources,R.color.brightRed,null))
            }
            if(it>=600000&&it<=601000){
                binding.countDownTimer.stop()
                AlertDialog.Builder(activity)
                            .setTitle("Times Up!!")
                            .setMessage("You have ran out of time. Wanna try it again?")
                            .setCancelable(false)
                            .setPositiveButton("Try Again!") { dialogInterface: DialogInterface, _: Int ->
                                resetSudoku(2)
                                dialogInterface.dismiss()
                            }
                            .show()
            }
        })
        return binding.root
    }

    fun showAlertDialog(){
        AlertDialog.Builder(activity)
                .setTitle("Congratulations!!!")
                .setMessage("You have decoded this sudoku puzzle. Wanna reset it?")
                .setCancelable(false)
                .setPositiveButton("Reset") { dialogInterface: DialogInterface, _: Int ->
                    resetSudoku(1)
                    dialogInterface.dismiss()
                }
                .show()
    }

    class EditText(activity: FragmentActivity, val resources: Resources, var ans: String) {
        var editText: TextInputEditText = TextInputEditText(ContextThemeWrapper(activity,R.style.sudokuEditTextStyle),null,0)
                .apply {
                    width = 112
                    height = 112
                }
        fun increaseRightStrokeWidth(){
            editText.setBackgroundResource(R.drawable.right_border)
        }
        fun increaseLeftStrokeWidth(){
            editText.setBackgroundResource(R.drawable.left_border)
        }
        fun increaseTopStrokeWidth(){
            editText.setBackgroundResource(R.drawable.top_border)
        }
        fun increaseBottomStrokeWidth(){
            editText.setBackgroundResource(R.drawable.bottom_border)
        }
        fun increaseRightTopStrokeWidth(){
            editText.setBackgroundResource(R.drawable.right_top_border)
        }
        fun increaseRightBottomStrokeWidth(){
            editText.setBackgroundResource(R.drawable.right_bottom_border)
        }
        fun increaseLeftTopStrokeWidth(){
            editText.setBackgroundResource(R.drawable.left_top_border)
        }
        fun increaseLeftBottomStrokeWidth(){
            editText.setBackgroundResource(R.drawable.left_bottom_border)
        }
        fun changeText(value: String){
            editText.setText(value)
            if(value==""){
                editText.setTextColor(ResourcesCompat.getColor(resources,R.color.blue,null))
            }else{
                editText.setTextColor(ResourcesCompat.getColor(resources,R.color.secondaryTextColor,null))
            }
        }
        fun changeEditable(){
            editText.isEnabled = false
        }
    }

    fun isSolved(): Boolean{
        for(i in 0..8){
            for(j in 0..8){
                if(sudokuArray[i][j].editText.text.toString()==""){
                    return false
                }else if(sudokuArray[i][j].editText.text.toString().toInt()!=ans[i][j]){
                    return false
                }
            }
        }
        return true
    }

    fun resetSudoku(value: Int){
        if(value==1){
            listIndex++
            if(listIndex==listSudoku.size)
                listIndex = 0
            for(i in 0..8){
                sudoku[i] = listSudoku[listIndex][i]
            }
            for(i in 0..8){
                ans[i] = listAns[listIndex][i]
            }
        }
        t = 0
        viewModel.reset()
        flag = 1
        binding.countDownTimer.setTextColor(ResourcesCompat.getColor(resources,R.color.secondaryLightColor,null))
        binding.countDownTimer.base = SystemClock.elapsedRealtime()
        for(i in 0..8){
            for(j in 0..8){
                val element = sudokuArray[i][j]
                if(sudoku[i][j]==0){
                    element.changeText("")
                    sharedArray[i][j] = 0
                }else{
                    element.changeText(sudoku[i][j].toString())
                    sharedArray[i][j] = sudoku[i][j]
                }
                element.ans = ans[i][j].toString()
            }
        }
        flag = 2
        viewModel.reset()
        binding.countDownTimer.start()
    }

    override fun onPause() {
        val application = requireNotNull(this.activity).application
        val sharedPreferences = application.getSharedPreferences("sudokuDecoding", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json: String = gson.toJson(sharedArray)
        editor.putInt("wrongAttempts",viewModel.wrongAttempts.value!!)
        editor.putInt("time",viewModel.time.value!!)
        editor.putString("sharedArray",json)
        editor.putInt("listIndex",listIndex)
        editor.apply()
        super.onPause()
    }

    override fun onStop() {
        val imm: InputMethodManager = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        super.onStop()
    }
}