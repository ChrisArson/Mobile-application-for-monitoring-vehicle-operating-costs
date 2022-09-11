package com.example.polkar.userauth

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.polkar.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.car_info_activity.*
import kotlinx.android.synthetic.main.register_activity.*
import kotlinx.android.synthetic.main.settings_activity.*

class SettingsActivity : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setSupportActionBar(toolbarSettingsActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mAuth = FirebaseAuth.getInstance();


        toolbarSettingsActivity.setNavigationOnClickListener {
            finish()
        }

        buttonChangePasswrd.setOnClickListener {
            textView68.visibility = View.VISIBLE
            textView72.visibility = View.VISIBLE
            editPassword1.visibility = View.VISIBLE
            editPassword2.visibility = View.VISIBLE
            buttonSavePassword.visibility = View.VISIBLE

            textView73.visibility = View.INVISIBLE
            editName.visibility = View.INVISIBLE
            buttonSaveName.visibility = View.INVISIBLE
        }
        buttonChangeName.setOnClickListener {
            textView73.visibility = View.VISIBLE
            editName.visibility = View.VISIBLE
            buttonSaveName.visibility = View.VISIBLE

            textView68.visibility = View.INVISIBLE
            textView72.visibility = View.INVISIBLE
            editPassword1.visibility = View.INVISIBLE
            editPassword2.visibility = View.INVISIBLE
            buttonSavePassword.visibility = View.INVISIBLE
        }
        buttonLogOut.setOnClickListener {
            logOut()
        }
        buttonSavePassword.setOnClickListener {
            validPassword()
        }
        buttonSaveName.setOnClickListener {
            validName()
        }

        val builder = AlertDialog.Builder(this)

        buttonDeleteAccount.setOnClickListener {
            builder.setTitle("Uwaga!")
                .setMessage("Na pewno chcesz usunąć swoje konto?")
                .setCancelable(true)
                .setPositiveButton("Tak"){dialogInterface,it->
                    deleteUser()
                }
                .setNegativeButton("Anuluj"){dialogInterface,it->
                    dialogInterface.cancel()
                }
                .show()
        }
    }

    private fun validPassword() {
        if(editPassword1.text.toString().isEmpty()||editPassword1.text.toString().length<6||editPassword1.text.toString().length>16){
            editPassword1.error="Hasło musi składac się z 6-16 znaków"
            editPassword1.requestFocus()
        }
        else if(editPassword2.text.toString().isEmpty()){
            editPassword2.error="Podaj właściwe hasło"
            editPassword2.requestFocus()
        }
        else if(editPassword1.text.toString()!=editPassword2.text.toString()){
            editPassword2.error="Podaj takie samo hasło"
            editPassword2.requestFocus()
        }else{
            changePassword()
        }
    }

    private fun validName(){
        if(editName.text.toString().isEmpty()||editName.text.toString().length<3||editName.text.toString().length>16){
            editName.error="Nazwa użytkownika musi składac się z 3-16 znaków"
            editName.requestFocus()
        }else{
            changeName()
        }
    }

    private fun changePassword(){
        val user = Firebase.auth.currentUser
        val newPassword = editPassword1.text.toString()

        user!!.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Hasło zostało zmienione!", Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(baseContext, "Nie działa!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun changeName(){
        val userDoc = db.collection("users").document(mAuth.currentUser!!.uid)
        val username = editName.text.toString()
        userDoc
            .update("userName", username)
            .addOnSuccessListener {
                Toast.makeText(baseContext, "Nazwa została zmieniona!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }

    private fun logOut(){
        mAuth.signOut()
        val currentUser: FirebaseUser? = mAuth.getCurrentUser()
        updateUI(currentUser)
    }

    private fun deleteUser(){
        val user = Firebase.auth.currentUser!!

        db.collection("users").document(mAuth.currentUser!!.uid)
            .delete()
            .addOnSuccessListener {
                user.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User account deleted.")
                            updateUI(null)
                        }
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this@SettingsActivity,
                    "Nie udało się usunąć użytkownika", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUI(currentUser : FirebaseUser?) {
        if(currentUser==null){
            startActivity(Intent(this, LoginActivity::class.java))
            Toast.makeText(baseContext, "Nie jesteś zalogowany", Toast.LENGTH_SHORT).show()
        }
    }
}