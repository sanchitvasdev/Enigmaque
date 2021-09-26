package com.sanchit.enigmaque.TicTacToe

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.sanchit.enigmaque.R
import com.sanchit.enigmaque.databinding.FragmentTicTacToeBinding

class TicTacToeFragment : Fragment(), View.OnClickListener {
    var PLAYER = true
    var TURN_COUNT = 0
    var boardStatus = Array(3){IntArray(3)}
    private lateinit var binding: FragmentTicTacToeBinding
    private lateinit var board: Array<Array<ShapeableImageView>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.title = "Tic Tac Toe"
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_tic_tac_toe, container, false)

        board = arrayOf(arrayOf(binding.button1,binding.button2,binding.button3),
                arrayOf(binding.button4,binding.button5,binding.button6),
                arrayOf(binding.button7,binding.button8,binding.button9))

        for(i in board){
            for(shapeableImageView in i){
                shapeableImageView.setOnClickListener(this)
            }
        }

        reset()

        AlertDialog.Builder(activity as AppCompatActivity)
                .setTitle("Note!!")
                .setMessage("You change the player names by clicking on them and then typing the names you want to display.")
                .setCancelable(true)
                .setPositiveButton("Ok!"){ dialogInterface: DialogInterface, _: Int ->
                    Toast.makeText(activity,resources.getString(R.string.toastText),Toast.LENGTH_SHORT).show()
                    dialogInterface.dismiss()
                }.show()

        binding.vsTextView1.setOnClickListener {
            showAlertDialog("Name","Enter the name you want to display",1)
        }

        binding.vsTextView2.setOnClickListener {
            showAlertDialog("Name","Enter the name you want to display",2)
        }

        binding.resetBtn2.setOnClickListener {
            PLAYER = true
            TURN_COUNT = 0
            reset()
        }
        return binding.root
    }

    private fun reset() {
        for (i in 0..2) {
            for (j in 0..2) {
                boardStatus[i][j] = -1
            }
        }
        for (i in board) {
            for (shapeableImageView in i) {
                shapeableImageView.isEnabled = true
                shapeableImageView.setImageResource(R.drawable.blank_image)
            }
        }
        binding.vsTextView1.text = resources.getString(R.string.player1_v)
        binding.vsTextView2.text = resources.getString(R.string.s_player2)
        binding.vsTextView1.textSize = 35F
        binding.vsTextView2.textSize = 28F
        binding.vsTextView1.isEnabled = true
        binding.vsTextView2.isEnabled = true
    }

    override fun onClick(v: View) {
        when(v){
            board[0][0] -> {
                updateValue(0,0,PLAYER)
            }
            board[0][1] -> {
                updateValue(0,1,PLAYER)
            }
            board[0][2] -> {
                updateValue(0,2,PLAYER)
            }
            board[1][0] -> {
                updateValue(1,0,PLAYER)
            }
            board[1][1] -> {
                updateValue(1,1,PLAYER)
            }
            board[1][2] -> {
                updateValue(1,2,PLAYER)
            }
            board[2][0] -> {
                updateValue(2,0,PLAYER)
            }
            board[2][1] -> {
                updateValue(2,1,PLAYER)
            }
            board[2][2] -> {
                updateValue(2,2,PLAYER)
            }
        }
        TURN_COUNT++
        PLAYER = !PLAYER
        if (PLAYER) {
            updateDisplay(1)
        } else {
            updateDisplay(2)
        }
        if (TURN_COUNT == 9) {
            binding.vsTextView1.text = "Game "
            binding.vsTextView2.text = "Draw"
            binding.vsTextView1.isEnabled = false
            binding.vsTextView2.isEnabled = false
            binding.vsTextView1.textSize = 35F
            binding.vsTextView2.textSize = 35F
        }
        checkWinner()
    }

    private fun updateDisplay(turn: Int) {
        when(turn){
            1 -> {
                binding.vsTextView1.textSize = 35F
                binding.vsTextView2.textSize = 28F
            }
            2 -> {
                binding.vsTextView1.textSize = 28F
                binding.vsTextView2.textSize = 35F
            }
            3 -> {
                disable()
                val text = binding.vsTextView1.text
                binding.vsTextView1.text = resources.getString(R.string.wins,text.substring(0,text.length-2))
                binding.vsTextView2.text = ""
                binding.vsTextView1.textSize = 35F
            }
            4 -> {
                disable()
                binding.vsTextView1.text = ""
                binding.vsTextView2.text = resources.getString(R.string.wins,binding.vsTextView2.text.substring(2))
                binding.vsTextView2.textSize = 35F
            }
        }
    }

    private fun checkWinner() {
        //Horizontal Rows
        for (i in 0..2) {
            if (boardStatus[i][0] == boardStatus[i][1] && boardStatus[i][0] == boardStatus[i][2]) {
                if (boardStatus[i][0] == 1) {
                    updateDisplay(3)
                    break
                } else if (boardStatus[i][0] == 0) {
                    updateDisplay(4)
                    break
                }
            }
        }
        //Vertical columns
        for (i in 0..2) {
            if (boardStatus[0][i] == boardStatus[1][i] && boardStatus[0][i] == boardStatus[2][i]) {
                if (boardStatus[0][i] == 1) {
                    updateDisplay(3)
                    break
                } else if (boardStatus[0][i] == 0) {
                    updateDisplay(4)
                    break
                }
            }
        }
        //First Diagonal
        if (boardStatus[0][0] == boardStatus[1][1] && boardStatus[0][0] == boardStatus[2][2]) {
            if (boardStatus[0][0] == 1) {
                updateDisplay(3)
            } else if (boardStatus[0][0] == 0) {
                updateDisplay(4)
            }
        }
        //Second Diagonal
        if (boardStatus[0][2] == boardStatus[1][1] && boardStatus[0][2] == boardStatus[2][0]) {
            if (boardStatus[0][2] == 1) {
                updateDisplay(3)
            } else if (boardStatus[0][2] == 0) {
                updateDisplay(4)
            }
        }
    }

    private fun disable(){
        for(i in board){
            for(shapeableImageView in i){
                shapeableImageView.isEnabled = false
            }
        }
    }

    private fun showAlertDialog(title: String,message: String,view: Int) {
        val input = TextInputEditText(requireActivity())
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        input.layoutParams = lp
        AlertDialog.Builder(activity as AppCompatActivity)
                .setTitle(title)
                .setMessage(message)
                .setView(input)
                .setCancelable(true)
                .setPositiveButton("Done"){dialogInterface: DialogInterface, _: Int ->
                    if(input.text.toString()!=""){
                        if(view==1){
                            binding.vsTextView1.text = resources.getString(R.string.player1_name,input.text.toString())
                        }else{
                            binding.vsTextView2.text = resources.getString(R.string.player2_name,input.text.toString())
                        }
                    }
                    dialogInterface.dismiss()
                }.show()
    }

    private fun updateValue(row: Int, col: Int, player: Boolean) {
        val value = if(player) 1 else 0
        if(player){
            board[row][col].apply {
                isEnabled = false
                setImageResource(R.drawable.cross)
            }
        }else{
            board[row][col].apply {
                isEnabled = false
                setImageResource(R.drawable.circle)
            }
        }
        boardStatus[row][col] = value
    }
}