package com.example.turism_register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login_user.*

class LoginUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_user)
        supportActionBar?.hide()

        tw_registrarse.setOnClickListener(){
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        setup()
    }
    private fun setup(){
        title = "Autentication"

        btn_login.setOnClickListener(){
            if (ip_email.text.isNotEmpty() && ip_contrasenia.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(ip_email.text.toString(),
                    ip_contrasenia.text.toString()).addOnCompleteListener(){
                    if (it.isSuccessful){
                        showHome(it.result?.user?.email?:"", ProviderType.BASIC)
                    }else{
                        showAlert2()
                    }
                }
            }
        }
    }
    private fun showAlert2(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error de autenticado ")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()


    }
    private fun showHome(email:String, provider: ProviderType){
        val homeIntent:Intent=Intent(this,MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}