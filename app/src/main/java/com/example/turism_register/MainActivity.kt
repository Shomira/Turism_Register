package com.example.turism_register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.turism_register.fragments.AboutusFragment
import com.example.turism_register.fragments.HomeFragment
import com.example.turism_register.fragments.ProfileFragment
import com.example.turism_register.fragments.RepositoryFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val profileFragment = ProfileFragment()
    private val aboutusFragment = AboutusFragment()
    private val repositoryFragment = RepositoryFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                //R.id.exitApp ->
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
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }
}