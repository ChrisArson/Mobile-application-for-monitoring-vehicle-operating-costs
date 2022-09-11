package com.example.polkar.userauth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.register_activity.*
import android.widget.Toast

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.polkar.menu.MainPanelActivity
import com.example.polkar.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        mAuth = FirebaseAuth.getInstance();
        supportActionBar?.hide()

        val tv_reg = findViewById(R.id.tv_login) as TextView
        tv_reg.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        RegisterButton.setOnClickListener {
            valid()
        }

    }

    private fun valid() {
        if(RegName.text.toString().isEmpty()||RegName.text.toString().length<3||RegName.text.toString().length>16){
            RegName.error="Nazwa użytkownika musi składac się z 3-16 znaków"
            RegName.requestFocus()
        }
        else if(RegMail.text.toString().isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(RegMail.text.toString()).matches()){
            RegMail.error="Podaj właściwy mail"
            RegMail.requestFocus()
        }
        else if(RegPasswrd1.text.toString().isEmpty()||RegPasswrd1.text.toString().length<6||RegPasswrd1.text.toString().length>16){
            RegPasswrd1.error="Hasło musi składac się z 6-16 znaków"
            RegPasswrd1.requestFocus()
        }
        else if(RegPasswrd2.text.toString().isEmpty()){
            RegPasswrd2.error="Podaj właściwe hasło"
            RegPasswrd2.requestFocus()
        }
        else if(RegPasswrd1.text.toString()!=RegPasswrd2.text.toString()){
            RegPasswrd2.error="Podaj takie samo hasło"
            RegPasswrd2.requestFocus()
        }else{
            createUser()
        }
    }

    private fun createUser(){
        mAuth.createUserWithEmailAndPassword(RegMail.text.toString(), RegPasswrd1.text.toString())
            .addOnCompleteListener(
                this
            ) { task ->
                val user = hashMapOf(
                    "UID" to mAuth.currentUser!!.uid,
                    "userCars" to emptyList<String>(),
                    "userEmail" to RegMail.text.toString(),
                    "userName" to RegName.text.toString()
                )
                if (task.isSuccessful) {
                    db.collection("users").document(mAuth.currentUser!!.uid)
                        .set(user)
                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                    Log.d(TAG, "createUserWithEmail:success")
                    val currUser = mAuth.currentUser
                    updateUI(currUser)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Autoryzacja nie powiodła się.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUI(currentUser : FirebaseUser?) {
        if(currentUser!=null){
            startActivity(Intent(this, MainPanelActivity::class.java))
            Toast.makeText(baseContext, "Konto zostało utworzone", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(baseContext, "Wystąpił błąd, spróbuj ponownie.", Toast.LENGTH_SHORT).show()
        }
    }

}