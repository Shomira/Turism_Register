package com.example.turism_register.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.turism_register.MainActivity
import com.example.turism_register.R
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private val addLugarFragment = AddLugarFragment()
    private val listLugaresFragment = ListLugaresFragment()
    private val reportesFragment = ReportesFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgBtn_addlugar.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, addLugarFragment)
                commit()
            }
        }

        imgBtn_reportes.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, reportesFragment)
                commit()
            }
        }

        imgBtn_listLugares.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, listLugaresFragment)
                commit()
            }
        }
    }

}
