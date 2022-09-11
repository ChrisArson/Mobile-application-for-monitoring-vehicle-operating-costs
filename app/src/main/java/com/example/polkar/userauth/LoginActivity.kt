package com.example.polkar.userauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.polkar.menu.MainPanelActivity
import com.example.polkar.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener{
            valid()
        }

        val tv_reg = findViewById(R.id.tv_register) as TextView
        tv_reg.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun valid() {
        if (textLogin.text.toString().isEmpty()) {
            textLogin.error = "Podaj właściwy mail"
        } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(textLogin.text.toString())
                .matches() == false
        ) {
            textLogin.error = "Podaj właściwy mail"
        } else if (textPassword.text.toString()
                .isEmpty() || textPassword.text.toString().length < 6 || textPassword.text.toString().length > 16
        ) {
            textPassword.error = "Hasło musi składac się z 6-16 znaków"
        } else {
            login()
        }
    }

    private fun login(){
        mAuth.signInWithEmailAndPassword(textLogin.text.toString(), textPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(baseContext, "Podałeś zły login lub hasło.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = mAuth.getCurrentUser()
        updateUI(currentUser)
    }

    private fun updateUI(currentUser : FirebaseUser?) {
        if(currentUser!=null){
            startActivity(Intent(this, MainPanelActivity::class.java))
        }
    }
}