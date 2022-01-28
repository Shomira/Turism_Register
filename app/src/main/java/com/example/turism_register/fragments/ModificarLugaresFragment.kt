package com.example.turism_register.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.turism_register.R
import com.example.turism_register.adapters.AdapterModificarLugar
import com.example.turism_register.datas_class.Lugar
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_list_lugares.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class ModificarLugaresFragment : Fragment(), AdapterModificarLugar.OnLugarModificarClickListener {

    private var email = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var lugarModificarArrayList: ArrayList<Lugar>
    private lateinit var adapterModificarLugar: AdapterModificarLugar
    private lateinit var db: FirebaseFirestore

    private lateinit var itemSearch: SearchView
    private lateinit var tempLugarModificarArrayList: ArrayList<Lugar>

    private lateinit var contextF: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contextF = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modificar_lugares, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).bottom_navegation_view.menu.findItem(R.id.fragmentToReturn).isVisible = true

        val objetoIntent = (activity as AppCompatActivity).intent
        email = objetoIntent.getStringExtra("email")!!

        db = FirebaseFirestore.getInstance()

        recyclerView = view.findViewById(R.id.recyclerview_modificar_lugar)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        lugarModificarArrayList = arrayListOf()
        tempLugarModificarArrayList = arrayListOf()

        adapterModificarLugar = AdapterModificarLugar(tempLugarModificarArrayList, view, this)

        recyclerView.adapter = adapterModificarLugar

        EventChangeListener()

        itemSearch = view.findViewById(R.id.search_modificar_lugar)
        itemSearch.setOnSearchClickListener {
            tv_categoria.text = "Buscar"
            linear_layout_categoria_search.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            dropdown_menu_list_lugar.visibility = View.GONE
        }
        itemSearch.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                tv_categoria.text = "Categoría"
                linear_layout_categoria_search.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 0.6f)
                dropdown_menu_list_lugar.visibility = View.VISIBLE
                return false
            }
        })
        itemSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempLugarModificarArrayList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    lugarModificarArrayList.forEach {
                        if (it.nombre!!.toLowerCase(Locale.getDefault()).contains(searchText)) {
                            tempLugarModificarArrayList.add(it)
                        }
                    }

                    recyclerView.adapter!!.notifyDataSetChanged()
                } else {
                    tempLugarModificarArrayList.clear()
                    tempLugarModificarArrayList.addAll(lugarModificarArrayList)
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                return false
            }
        })
    }

    private fun EventChangeListener() {
        lugarModificarArrayList.clear()
        tempLugarModificarArrayList.clear()
        db.collection("lugares")
            .whereEqualTo("usuario", email)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    cargaDatosDeFirebase(value, error)
                }
            })
    }

    fun cargaDatosDeFirebase(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) {
            Log.e("Firestore Error", error.message.toString())
            return
        }

        for (dc: DocumentChange in value?.documentChanges!!) {
            if (dc.type == DocumentChange.Type.ADDED) {
                lugarModificarArrayList.add(dc.document.toObject(Lugar::class.java))
            }
        }
        tempLugarModificarArrayList.addAll(lugarModificarArrayList)
        adapterModificarLugar.notifyDataSetChanged()
    }

    override fun onItemClickModificar(id: String?) {
        var addLugarMainFragment = AddLugarMainFragment()
        var bundle:Bundle = Bundle()
        bundle.putString("email", email)
        bundle.putString("isEditar", "true")
        bundle.putString("id", id)
        addLugarMainFragment.arguments = bundle

        if (addLugarMainFragment != null) {
            val transaction = (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, addLugarMainFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }

    override fun onItemCLickEliminar(id: String?, nombre: String?) {
        val build: AlertDialog.Builder = AlertDialog.Builder(contextF, R.style.AlertDialogTheme)
        val v:View = LayoutInflater.from(contextF).inflate(
            R.layout.dialog_delete_lugar, (activity as AppCompatActivity).findViewById(R.id.container_dialog_delete_lugar))
        build.setView(v)
        v.findViewById<TextView>(R.id.tv_message_dialog_delete_lugar).setText("¿Estas seguro de querer eliminar $nombre?")
        v.findViewById<ImageView>(R.id.icon_dialog_delete_lugar).setImageResource(R.drawable.ic_baseline_warning_24)

        val alertDialog:AlertDialog = build.create()

        v.findViewById<Button>(R.id.dialog_button_si_delete_lugar).setOnClickListener {
            db.collection("lugares").document(id.toString())
                .delete()

            EventChangeListener()

            Toast.makeText(contextF, "Lugar Eliminado", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }
        v.findViewById<Button>(R.id.dialog_button_no_delete_lugar).setOnClickListener {
            alertDialog.dismiss()
        }

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}