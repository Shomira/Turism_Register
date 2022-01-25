package com.example.turism_register.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.turism_register.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * A simple [Fragment] subclass.
 */
class ReportesFragment : Fragment() {

    private lateinit var contextF: Context
    private var email = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reportes, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contextF = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).bottom_navegation_view.menu.findItem(R.id.fragmentToReturn).isVisible = true

        val objetoIntent =  (activity as AppCompatActivity).intent
        email = objetoIntent.getStringExtra("email")!!
    }
}