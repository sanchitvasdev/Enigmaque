package com.sanchit.enigmaque

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.sanchit.enigmaque.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var image: String
    private val storage by lazy {
        FirebaseStorage.getInstance()
    }
    val cm by lazy {
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        binding.photoImageView.setOnClickListener {
            checkPermissionForImage()
        }

        binding.doneBtn.setOnClickListener {
            if(binding.nameEditText.text.toString().length<8){
                Toast.makeText(this,"You haven't entered your name correctly. Your name should have minimum of 8 characters",Toast.LENGTH_LONG).show()
            }else{
                if(binding.photoImageView.drawable.constantState==ResourcesCompat.getDrawable(resources,R.drawable.ic_profile,null)!!.constantState){
                    Toast.makeText(this,"You haven't selected a profile image",Toast.LENGTH_LONG).show()
                }else{
                    val user = auth.currentUser
                    if(updateUI(user)){
                        val intent = Intent(this, GameActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
        binding.signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) && (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)) {
                val permissionread = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                val permissionWrite = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(
                    permissionread,
                    1001
                )
                requestPermissions(
                    permissionWrite,
                    1002
                )
            } else {
                pickImageFromGallery()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(
            intent,
            1000
        )
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.i(TAG,"firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.i(TAG, "Google sign in failed", e)
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == 1000) {
            data?.data?.let {
                binding.photoImageView.setImageURI(it)
                startUpload(it)
            }
        }
    }

    private fun startUpload(it: Uri){
        binding.doneBtn.isEnabled = false
        val ref = storage.reference.child("uploads/" + auth.uid.toString())
        val uploadTask = ref.putFile(it)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                image = task.result.toString()
                binding.doneBtn.isEnabled = true
            } else {
                binding.doneBtn.isEnabled = true
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"Sign in successful", Toast.LENGTH_SHORT).show()
                    Log.i(TAG, "signInWithCredential:success")
                    binding.doneBtn.isEnabled = true
//                    val user = auth.currentUser
//                    updateUI(user)
                } else {
                    Log.i(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(binding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT)
                        .show()
//                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?): Boolean {
        var flag = false
        val database: FirebaseFirestore? = FirebaseFirestore.getInstance()
        try {
            val data = mutableMapOf<String,Any>()
            data.put("name", binding.nameEditText.text.toString().trim())
            data.put("photo", image)
            val doc = database?.collection("users")?.document(auth.uid.toString())
            doc!!.set(data).addOnSuccessListener {
                flag = true
                Toast.makeText(this, "Your data has been saved.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Log.i(TAG,it.toString())
                Toast.makeText(this,"Some error occurred. Try again.",Toast.LENGTH_SHORT).show()
            }
        }catch (e: Exception){
            Toast.makeText(this,"Some error occurred. Try again.",Toast.LENGTH_SHORT).show()
            Log.i(TAG,e.toString())
        }
        return flag
    }

    override fun onStart() {
        requestInternetpermission()
        super.onStart()
        when {
            isInternetpermissiongranted() -> {
                when{
                    isInternetEnabled() -> mainactivitycall()
                    else -> showInternetNotEnableDialog()
                }
            }
            else -> requestInternetpermission()
        }
    }
    private fun mainactivitycall() {
    }

    private fun requestInternetpermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(
                arrayOf(Manifest.permission.INTERNET),
                999
            )
        }
    }

    private fun isInternetpermissiongranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
        } else {
            TODO("VERSION.SDK_INT < M")
        }
    }

    fun isInternetEnabled(): Boolean {
        var status = false
        val networkCapabilities = cm.activeNetwork
        val actNw = cm.getNetworkCapabilities(networkCapabilities)
        if (actNw != null) {
            if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                status = true
            } else if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                status = true
            }
        } else {
            status = false
        }
        return status
    }

    private fun showInternetNotEnableDialog() {
        AlertDialog.Builder(this)
            .setTitle("Enable Internet")
            .setMessage("Internet is required for this application")
            .setCancelable(false)
            .setPositiveButton("Enable Now") { dialogInterface: DialogInterface, i: Int ->
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                dialogInterface.dismiss()
            }
            .show()
    }
    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}