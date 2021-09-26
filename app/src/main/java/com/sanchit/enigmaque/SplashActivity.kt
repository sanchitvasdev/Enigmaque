package com.sanchit.enigmaque

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        if(auth.currentUser!=null){
            val intent = Intent(this,GameActivity::class.java)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(intent)
                finishAffinity()
            },2000)
        }else{
            val intent = Intent(this, MainActivity::class.java)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(intent)
                finishAffinity()
            }, 2000)
        }
    }
}