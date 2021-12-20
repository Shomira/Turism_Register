package com.example.turism_register

import android.app.FragmentManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.turism_register.fragments.AboutusFragment
import com.example.turism_register.fragments.HomeFragment
import com.example.turism_register.fragments.ProfileFragment
import com.example.turism_register.fragments.RepositoryFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_nav_header.*
enum class ProviderType{
    BASIC
}
class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val profileFragment = ProfileFragment()
    private val aboutusFragment = AboutusFragment()
    private val repositoryFragment = RepositoryFragment()
    private var email = ""
    private var provider = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Agregar email al header del navigation drawer
        val header:View = navDrawerView.getHeaderView(0)
        var txtEmail:TextView = header.findViewById(R.id.tv_emailNavDrawer)
        val objetoIntent=intent
        email = objetoIntent.getStringExtra("email")!!
        provider = objetoIntent.getStringExtra("provider")!!
        txtEmail.setText(email)
        //

        replaceFragment(homeFragment)

        btnNavView()
        navigationDrawer()
    }

    private fun navigationDrawer() {
        navDrawerView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navDrawer_profile -> replaceFragment(profileFragment)
                R.id.navDrawer_aboutUs -> replaceFragment(aboutusFragment)
                R.id.navDrawer_repository -> replaceFragment(repositoryFragment)
                R.id.sign_out -> {
                    val verAuth: Intent = Intent(this, LoginUserActivity::class.java)
                    FirebaseAuth.getInstance().signOut()
                    startActivity(verAuth)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)

            true
        }
    }

    private fun btnNavView() {
        bottom_navegation_view.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.fragmentNavigationDrawer -> drawerLayout.openDrawer(navDrawerView)
                R.id.fragmentHome -> replaceFragment(homeFragment)
                R.id.fragmentToReturn -> onBackPressed()
            }

            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        var bundle:Bundle = Bundle()
        bundle.putString("email", email)
        bundle.putString("provider", provider)
        fragment.arguments = bundle

        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}