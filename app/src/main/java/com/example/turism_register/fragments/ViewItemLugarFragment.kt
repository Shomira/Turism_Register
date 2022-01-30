package com.example.turism_register.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.turism_register.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_view_item_lugar.*
import kotlinx.android.synthetic.main.list_item_lugar.view.*

class ViewItemLugarFragment : Fragment() {

    private lateinit var database: FirebaseFirestore
    private var id : String = ""
    private lateinit var contextF: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contextF = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_item_lugar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).bottom_navegation_view.menu.findItem(R.id.fragmentToReturn).isVisible = true
        id = this.arguments?.getString("id").toString()
        database = FirebaseFirestore.getInstance()
        database.collection("lugares").document(id).get().addOnSuccessListener {
            Glide.with(contextF).load(it.get("imagen")).into(photo_view_item_lugar)
            tv_name_view_item_lugar.setText(it.get("nombre") as String)
            tv_description_view_item_lugar.setText(it.get("descripcion") as String)
            tv_categoria_view_item_lugar.setText(it.get("categoria") as String)

            tv_ubicacion_view_item_lugar.text =
                it.get("provincia") as String + ", " + it.get("canton") as String + ", " + it.get("parroquia") as String

            tv_clima_view_item_lugar.setText(it.get("clima") as String)
            tv_nombre_admin_view_item_lugar.setText(it.get("nombre_administrador") as String)
            tv_email_admin_view_item_lugar.setText(it.get("correo_electronico") as String)
            tv_celular_admin_view_item_lugar.setText(it.get("telefono_celular") as String)

            tv_coord_inicio_acceso_terrestre_view_item_lugar.setText(it.get("acceso_terrestre_coordenada_inicio") as String)
            tv_coord_fin_acceso_terrestre_view_item_lugar.setText(it.get("acceso_terrestre_coordenada_fin") as String)
            tv_distancia_km_acceso_terrestre_view_item_lugar.setText(it.get("acceso_terrestre_distancia_km") as String)
            tv_estado_acceso_terrestre_view_item_lugar.setText(it.get("acceso_terrestre_estado") as String)
            tv_observaciones_acceso_terrestre_view_item_lugar.setText(it.get("acceso_terrestre_observaciones") as String)
            tv_tipo_material_acceso_terrestre_view_item_lugar.setText(it.get("acceso_terrestre_tipo_material") as String)

            tv_puerto_partida_acceso_acuatico_view_item_lugar.setText(it.get("muelle_partida") as String)
            tv_estado_puerto_partida_acceso_acuatico_view_item_lugar.setText(it.get("estado_muelle_partida") as String)
            tv_puerto_llegada_acceso_acuatico_view_item_lugar.setText(it.get("muelle_llegada") as String)
            tv_estado_puerto_llegada_acceso_acuatico_view_item_lugar.setText(it.get("estado_muelle_llegada") as String)
            tv_tipo_acceso_acuatico_view_item_lugar.setText(it.get("acuatico_tipo_acceso") as String)
            tv_tipo_via_acceso_acuatico_view_item_lugar.setText(it.get("acuatico_tipo_via") as String)

            tv_tipo_acceso_aereo_view_item_lugar.setText(it.get("tipo_via_acceso_aereo") as String)
        }
    }
}