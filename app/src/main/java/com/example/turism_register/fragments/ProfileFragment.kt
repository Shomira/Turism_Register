package com.example.turism_register.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.turism_register.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).bottom_navegation_view.menu.findItem(R.id.fragmentToReturn).isVisible = true

        var email: String? = this.arguments?.getString("email")
        var provider: String? = this.arguments?.getString("provider")

        et_namesUser.isEnabled = false
        et_lastNames.isEnabled = false
        et_direccion.isEnabled = false
        et_phone.isEnabled = false

        btn_actualizar.isVisible = false
        btn_cancelar.isVisible = false

        mostrarDatos(email)

        imgBtn_editUser.setOnClickListener {
            et_namesUser.isEnabled = true
            et_lastNames.isEnabled = true
            et_direccion.isEnabled = true
            et_phone.isEnabled = true

            btn_actualizar.isVisible = true
            btn_cancelar.isVisible = true

            imgBtn_editUser.isVisible = false
        }

        btn_cancelar.setOnClickListener {
            botonesFuncionando()
            mostrarDatos(email)
        }

        btn_actualizar.setOnClickListener {
            if (et_namesUser.length() < 3) {
                Toast.makeText((activity as AppCompatActivity).applicationContext, "Ingrese 3 o más letras en el Nombre", Toast.LENGTH_SHORT).show()
            } else {
                if (et_lastNames.length() < 3) {
                    Toast.makeText((activity as AppCompatActivity).applicationContext, "Ingrese 3 o más letras en el Apellido", Toast.LENGTH_SHORT).show()
                } else {
                    if (et_direccion.length() < 7) {
                        Toast.makeText((activity as AppCompatActivity).applicationContext, "Ingrese 7 o más letras en la Dirección", Toast.LENGTH_SHORT).show()
                    } else {
                        if (et_phone.length() < 10) {
                            Toast.makeText((activity as AppCompatActivity).applicationContext, "Ingrese 10 dígitos en el Teléfono", Toast.LENGTH_SHORT).show()
                        } else {
                            db.collection("users").document(email.toString()).set(
                                hashMapOf(
                                    "provider" to provider,
                                    "address" to email,
                                    "nombres" to et_namesUser.text.toString(),
                                    "apellidos" to et_lastNames.text.toString(),
                                    "direccion" to et_direccion.text.toString(),
                                    "telefono" to et_phone.text.toString()
                                )
                            )
                            botonesFuncionando()
                            mostrarDatos(email)
                            Toast.makeText((activity as AppCompatActivity).applicationContext, "DATOS ACTUALIZADOS", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun botonesFuncionando() {
        imgBtn_editUser.isVisible = true
        et_namesUser.isEnabled = false
        et_lastNames.isEnabled = false
        et_direccion.isEnabled = false
        et_phone.isEnabled = false

        btn_actualizar.isVisible = false
        btn_cancelar.isVisible = false
    }

    private fun mostrarDatos(email: String?) {
        db.collection("users").document(email.toString()).get().addOnSuccessListener {
            et_namesUser.setText(it.get("nombres") as String?)
            et_lastNames.setText(it.get("apellidos") as String?)
            et_direccion.setText(it.get("direccion") as String?)
            et_phone.setText(it.get("telefono") as String?)
        }
    }
}