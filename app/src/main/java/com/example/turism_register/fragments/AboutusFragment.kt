package com.example.turism_register.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.turism_register.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_aboutus.*

/**
 * A simple [Fragment] subclass.
 */
class AboutusFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aboutus, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).bottom_navegation_view.menu.findItem(R.id.fragmentToReturn).isVisible = true

        tv_parrafo1.text =
            "El siguiente sistema móvil, es una iniciativa que se crea con el objetivo de " +
                    "almacenar información de los diferentes lugares turísticos en las " +
                    "zonas rurales del Ecuador."

        tv_parrafo2.text =
            "A través de la construcción de este aplicativo, los usuarios podran a parte de " +
                    "almacenar la información, ver todos los lugares turísticos existente en el" +
                    "Ecuador, así como buscar un lugar en especifico que les llame la atención" +
                    ", con el fin de ver su información y tomar una buena decisión de que" +
                    "lugar les gustaria visitar."
    }
}