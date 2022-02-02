package com.example.turism_register


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

import android.util.Patterns
import com.example.turism_register.fragments.HomeFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login_user.ip_contrasenia
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Pattern


class RegisterActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        val bundle: Bundle? = intent.extras
        val provider:ProviderType = ProviderType.BASIC
        btn_close_register.setOnClickListener {
            val loginIntent: Intent = Intent(this, LoginUserActivity::class.java)
            startActivity(loginIntent)
        }
        btn_registrar.setOnClickListener {

            val mEmail = ip_correo_r.text.toString()
            val mPassword = ip_contrasenia.text.toString()
            val mRepeatPassword = ip_contrasenia2_r.text.toString()

            val passwordRegex = Pattern.compile("^" +
                    "(?=.*[-@#$%^&+=])" +     // Al menos 1 carácter especial
                    ".{6,}" +                // Al menos 4 caracteres
                    "$")

            if(mEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                Toast.makeText(this, "Ingrese un email valido.",
                    Toast.LENGTH_SHORT).show()
            } else if (mPassword.isEmpty() || !passwordRegex.matcher(mPassword).matches()){
                Toast.makeText(this, "La contraseña es debil.",
                    Toast.LENGTH_SHORT).show()
            } else if (mPassword != mRepeatPassword){
                Toast.makeText(this, "Confirma la contraseña.",
                    Toast.LENGTH_SHORT).show()
            } else {
                db.collection("users").document(mEmail).set(
                    hashMapOf(
                        "provider" to provider,
                        "address" to ip_correo_r.text.toString(),
                        "apellidos" to ip_apellido_r.text.toString(),
                        "nombres" to ip_nombre_r.text.toString()
                    )
                )
                createAccount(mEmail, mPassword, provider)
            }

        }


    }


    private fun createAccount(email: String, password: String, provider: ProviderType) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                val homeIntent: Intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("email", email)
                    putExtra("provider", provider.name)
                }
                startActivity(homeIntent)
            }
    }
}
