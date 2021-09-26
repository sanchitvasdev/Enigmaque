package com.sanchit.enigmaque

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sanchit.enigmaque.databinding.ActivityGameBinding
import com.squareup.picasso.Picasso

class GameActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private val database by lazy {
        FirebaseFirestore.getInstance()
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityGameBinding>(this,R.layout.activity_game)
        drawerLayout = binding.drawerLayout

        val navController = this.findNavController(R.id.myNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
        NavigationUI.setupWithNavController(binding.navView,navController)

        val navHeaderView: View = binding.navView.getHeaderView(0)
        val navHeaderName = navHeaderView.findViewById(R.id.nameTextView) as MaterialTextView
        val navHeaderImage = navHeaderView.findViewById(R.id.profileImageView) as ImageView

        val docRef = database.collection("users").document(auth.uid.toString())
        docRef.get().addOnSuccessListener { documentSnapshot ->
            if(documentSnapshot!=null){
                navHeaderName.setText(documentSnapshot.data?.get("name").toString())
                Picasso.get().load(documentSnapshot.data?.get("photo").toString())
                    .into(navHeaderImage)
            }else{
                Toast.makeText(this, "Some error occurred", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener{
            Log.i("TAG",it.toString())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController,drawerLayout)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        when(item.itemId){
            R.id.cardMatchingFragment -> NavigationUI.onNavDestinationSelected(item,navController)
            R.id.hangmanFragment -> NavigationUI.onNavDestinationSelected(item, navController)
            R.id.sudokuFragment -> NavigationUI.onNavDestinationSelected(item, navController)
            R.id.ticTacToeFragment -> NavigationUI.onNavDestinationSelected(item, navController)
        }
        return true
    }
}