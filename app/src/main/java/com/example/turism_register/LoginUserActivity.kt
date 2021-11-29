package com.example.turism_register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login_user.*

class LoginUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_user)
        supportActionBar?.hide()

        tw_registrarse.setOnClickListener(){
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btn_login.setOnClickListener() {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}