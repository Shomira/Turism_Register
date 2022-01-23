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
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.turism_register.R
import com.example.turism_register.adapters.AdapterLugar
import com.example.turism_register.datas_class.Lugar
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.fragment_list_lugares.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class ListLugaresFragment : Fragment(), AdapterLugar.OnLugarClickListener,
    AdapterView.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var lugarArrayList: ArrayList<Lugar>
    private lateinit var adapterLugar: AdapterLugar
    private lateinit var db: FirebaseFirestore
    private var contClick = 0

    private lateinit var itemSearch: SearchView
    private lateinit var tempLugarArrayList: ArrayList<Lugar>

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
        return inflater.inflate(R.layout.fragment_list_lugares, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).bottom_navegation_view.menu.findItem(R.id.fragmentToReturn).isVisible = true
        db = FirebaseFirestore.getInstance()
        dropDownCategorias()

        recyclerView = view.findViewById(R.id.recyclerViewLugar)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        lugarArrayList = arrayListOf()
        tempLugarArrayList = arrayListOf()

        adapterLugar = AdapterLugar(tempLugarArrayList, view, this)

        recyclerView.adapter = adapterLugar

        EventChangeListener()

        itemSearch = view.findViewById(R.id.et_search)
        itemSearch.setOnSearchClickListener {
            tv_categoria.text = "Buscar"
            linear_layout_categoria_search.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, 1f)
            dropdown_menu_list_lugar.visibility = View.GONE
        }
        itemSearch.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                tv_categoria.text = "Categoría"
                linear_layout_categoria_search.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, 0.6f)
                dropdown_menu_list_lugar.visibility = View.VISIBLE
                return false
            }
        })
        itemSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempLugarArrayList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    lugarArrayList.forEach {
                        if (it.nombre!!.toLowerCase(Locale.getDefault()).contains(searchText)) {
                            tempLugarArrayList.add(it)
                        }
                    }

                    recyclerView.adapter!!.notifyDataSetChanged()
                } else {
                    tempLugarArrayList.clear()
                    tempLugarArrayList.addAll(lugarArrayList)
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                return false
            }
        })
    }

    override fun onStart() {
        itemSearch.onActionViewCollapsed()
        super.onStart()
    }

    private fun EventChangeListener() {
        lugarArrayList.clear()
        tempLugarArrayList.clear()
        db.collection("lugares")
            .orderBy("nombre", Query.Direction.ASCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    cargaDatosDeFirebase(value, error)
                }
            })
    }

    override fun onItemClick(
        nombre: String?,
        descripcion: String?,
        provincia: String?,
        canton: String?,
        parroquia: String?,
        imagen: String?,
        actividad: View
    ) {
        //Toast.makeText((activity as AppCompatActivity), "$nombre", Toast.LENGTH_SHORT).show()
        val dialog: AlertDialog = AlertDialog.Builder(actividad.rootView.context).create()
        val dialogView:View = LayoutInflater.from(actividad.rootView.context).inflate(R.layout.popup_list_lugar, null)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val popNombreLugar: TextView = dialogView.findViewById(R.id.pop_tv_titleLugar)
        val popDescripLugar: TextView = dialogView.findViewById(R.id.pop_tv_descLugar)
        val popUbicacionLugar: TextView = dialogView.findViewById(R.id.pop_tv_ubicLugar)
        val popImgLugar: PhotoView = dialogView.findViewById(R.id.pop_imgv_lugar)

        popNombreLugar.setText("$nombre")
        popDescripLugar.setText("$descripcion")
        popUbicacionLugar.setText("$provincia, $canton, $parroquia")
        Glide.with(actividad).load(imagen).into(popImgLugar)

        dialog.setView(dialogView)
        dialog.setCancelable(true)
        dialog.show()
    }

    fun dropDownCategorias() {
        val categorias_drop = resources.getStringArray(R.array.categorias_list_lugar_drop)
        val adapter = ArrayAdapter(
            contextF,
            R.layout.list_item_categorias_drop_layout,
            categorias_drop
        )

        with(autocom_tv_categoria_list) {
            setAdapter(adapter)
            onItemClickListener = this@ListLugaresFragment
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position).toString()
        contClick++

        if (item == "Manifestaciones Culturales") {
            Toast.makeText(contextF, item, Toast.LENGTH_SHORT).show()
            EventChangeListenerCategorias(item)
        } else if (item == "Atractivos Naturales") {
            Toast.makeText(contextF, item, Toast.LENGTH_SHORT).show()
            EventChangeListenerCategorias(item)
        } else if (item == "Todas" && contClick != 1) {
            EventChangeListener()
        }
    }

    private fun EventChangeListenerCategorias(categoria: String) {
        lugarArrayList.clear()
        tempLugarArrayList.clear()
        db.collection("lugares")
            .whereEqualTo("categoria", categoria)
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
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
                lugarArrayList.add(dc.document.toObject(Lugar::class.java))
            }
        }

        tempLugarArrayList.addAll(lugarArrayList)

        adapterLugar.notifyDataSetChanged()
    }
}