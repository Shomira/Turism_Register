package com.example.turism_register.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.example.turism_register.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {


    //private val listLugaresFragment = ListLugaresFragment()
    //private val modificarLugaresFragment = ModificarLugaresFragment()
    private var email = ""
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

        email = this.arguments?.getString("email").toString()

        imgBtn_addlugar.setOnClickListener {
            var editar = "false"
            val addLugarMainFragment = AddLugarMainFragment()
            var bundle:Bundle = Bundle()
            bundle.putString("email", email)
            bundle.putString("isEditar", editar)
            addLugarMainFragment.arguments = bundle
            replaceFragment(addLugarMainFragment)
        }

        imgBtn_modificarLugares.setOnClickListener {
            val modificarLugaresFragment = ModificarLugaresFragment()
            var bundle:Bundle = Bundle()
            bundle.putString("email", email)
            modificarLugaresFragment.arguments = bundle
            replaceFragment(modificarLugaresFragment)
        }

        imgBtn_listLugares.setOnClickListener {
            val listLugaresFragment = ListLugaresFragment()
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
