package com.example.turism_register.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.turism_register.MainActivity
import com.example.turism_register.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.zip.Inflater

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
        (activity as AppCompatActivity).bottom_navegation_view.menu.findItem(R.id.fragmentToReturn).isVisible = false

        imgBtn_addlugar.setOnClickListener {
            replaceFragment(addLugarFragment)
        }

        imgBtn_reportes.setOnClickListener {
            replaceFragment(reportesFragment)
        }

        imgBtn_listLugares.setOnClickListener {
            replaceFragment(listLugaresFragment)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {
            val transaction = (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

}
