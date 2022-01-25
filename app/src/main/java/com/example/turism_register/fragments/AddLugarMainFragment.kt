package com.example.turism_register.fragments


import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.turism_register.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_lugar_main.*
import kotlinx.android.synthetic.main.fragment_add_lugar_main.view.*
import java.text.SimpleDateFormat
import java.util.*


class AddLugarMainFragment : Fragment(), AdapterView.OnItemClickListener{
    private var email = ""
    private lateinit var database: FirebaseFirestore
    private var contadorPantallas = 0

    lateinit var ImageUri: Uri
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_lugar_main, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Subir imagen del atractivo
        btn_subir_imagen_atr.setOnClickListener{
            selectImage()
        }

        // ===============FIN subir imagen
        (activity as AppCompatActivity).bottom_navegation_view.menu.findItem(R.id.fragmentToReturn).isVisible = false

        val objetoIntent = (activity as AppCompatActivity).intent
        email = objetoIntent.getStringExtra("email")!!
        usuario_add_lugar.setText(email)
        addLugarParte1()
        btn_anterior_add_lugar.isVisible = false
        btn_anterior_add_lugar.setOnClickListener{
            contadorPantallas--
            if (contadorPantallas == 0){
                btn_anterior_add_lugar.isVisible = false
                val paramsScroll1 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,0f )
                scrollparte1.layoutParams= paramsScroll1
                val paramsScroll2 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
                scrollparte2.layoutParams= paramsScroll2
                val paramsScroll3 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
                scrollparte3.layoutParams= paramsScroll3
            }
            partesFormulario()
            if(contadorPantallas < 2){
                btn_siguiente_add_lugar.isVisible = true
            }
        }
        btn_siguiente_add_lugar.setOnClickListener{
            contadorPantallas++
            partesFormulario()
            if (contadorPantallas > 0){
                btn_anterior_add_lugar.isVisible = true
            }
            if(contadorPantallas ==2){
                btn_siguiente_add_lugar.isVisible = false

            }
        }

        btn_guardarLugarMain.setOnClickListener {
            database = FirebaseFirestore.getInstance()
            database.collection("Lugares Agregados").document().set( hashMapOf(
                "Usuario" to email,
                "Nombre Atractivo" to ip_nombre_atr.text.toString(),
                "Descripción del Atractivo" to ip_descripcion_atr.text.toString(),
                "Categoria" to ip_categoria.text.toString(),
                "Tipo" to ip_tipo.text.toString(),
                "Subtipo" to ip_subtipo.text.toString(),
                "Provincia" to ip_provincia.text.toString(),
                "Cantones" to ip_canton.text.toString(),
                "Parroquias" to ip_parroquias.text.toString(),
                "Calle Principal" to ip_calle_principal.text.toString(),
                "Número Calle Principal" to ip_calle_principal_numero.text.toString(),
                "Transversal" to ip_calle_transversal.text.toString(),
                "Barrio, Sector" to ip_barrio_sector.text.toString(),
                "Poblado Cercano" to ip_sitio_poblado.text.toString(),
                "Latitud" to ip_latitud.text.toString(),
                "Longitud" to ip_longitug.text.toString(),
                "Altura" to ip_altura_msnm.text.toString(),
                "Tipo Administrador" to ip_tipo_admin.text.toString(),
                "Nombre Institución" to ip_nombre_inst.text.toString(),
                "Nombre Administrador" to ip_nombre_admin.text.toString(),
                "Cargo que Ocupa" to ip_cargo_ocupa.text.toString(),
                "Teléfono Celular" to ip_telefono.text.toString(),
                "Correo Electrónico" to ip_correo.text.toString(),
                "Observaciones" to ip_observaciones_admin.text.toString()

            ))

            Toast.makeText(context, "LUGAR AGREGADO", Toast.LENGTH_SHORT).show()
            // funcionalidades de actividad
            (activity as AppCompatActivity).onBackPressed()

        }

    }
    // ========= Imagen


    // Imagen
    fun selectImage(){
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == RESULT_OK){
            ImageUri = data?.data!!
            firebaseImage.setImageURI(ImageUri)
            if(data == null || data.data == null){
                return
            }else{
                val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
                val now = Date()
                val fileName = formatter.format(now)
                val storageReference = FirebaseStorage.getInstance().getReference("pruebas/$fileName")

                storageReference.putFile(ImageUri).addOnSuccessListener {

                    Toast.makeText(context, "carga exitosa ....", Toast.LENGTH_LONG).show()
                }.addOnFailureListener{

                    Toast.makeText(context, "hola "+ ImageUri.toString(), Toast.LENGTH_LONG).show()
                }
            }



        }

    }







    fun addLugarParte1() {
        // DropDown de provincias
        val categorias = resources.getStringArray(R.array.categorias)

        val adapter22 = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, categorias) }
        with(ip_categoria) {
            setAdapter(adapter22)
            onItemClickListener = this@AddLugarMainFragment
        }
        // fin drowpdown categorias

        // DropDown de Tipo
        val tipos = resources.getStringArray(R.array.tp_atractivoNaturales)

        var adapter1 = activity?.let {
            ArrayAdapter<String>(it, R.layout.item_select, tipos)
        }
        with(ip_tipo) {
            setAdapter(adapter1)
            onItemClickListener = this@AddLugarMainFragment
        }
        // fin drowpdown subtipo
        // DropDown de provincias
        var adapter3 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.provincias)
            )
        }
        with(ip_provincia) {
            setAdapter(adapter3)
            onItemClickListener = this@AddLugarMainFragment
        }
        // fin drowpdown provincias
        // DropDown de Tipo
        // val sub_tipos= resources.getStringArray(R.array.sbtp_Montanias)
        var adapter2 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.sbtp_Montanias)
            )
        }
        with(ip_subtipo) {
            setAdapter(adapter2)
            onItemClickListener = this@AddLugarMainFragment
        }
        // fin drowpdown subtipo

        // Select para mostrar o no los input de la parte 2

        cb_caract_clima_p2.setOnClickListener{
            linearH11p2.toggleVisibility()
        }
        //  Imagen para Expandir y Contraer el contenido de las secciones a llenar
        imgCollapse1p1.setOnClickListener {
            linear1p1.toggleVisibility()
            imgExpand1p1.visibility = View.VISIBLE
            imgCollapse1p1.visibility = View.GONE
        }
        imgCollapse2p1.setOnClickListener {
            linear2p1.toggleVisibility()
            imgExpand2p1.visibility = View.VISIBLE
            imgCollapse2p1.visibility = View.GONE
        }

        imgExpand1p1.setOnClickListener {
            linear1p1.toggleVisibility()
            imgExpand1p1.visibility = View.GONE
            imgCollapse1p1.visibility = View.VISIBLE
        }
        imgExpand2p1.setOnClickListener {
            linear2p1.toggleVisibility()
            imgExpand2p1.visibility = View.GONE
            imgCollapse2p1.visibility = View.VISIBLE
        }
        database = FirebaseFirestore.getInstance()

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //Recuperamos el item seleccionado por su posición
        selectProvinciasCantones(parent, view, position, id)

    }
    fun View.toggleVisibility() {
        if (visibility == View.VISIBLE) {
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
        }

    }
    fun partesFormulario(){
        if ( contadorPantallas == 1){
            val paramsScroll1 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte1.layoutParams= paramsScroll1
            val paramsScroll2 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,0f )
            scrollparte2.layoutParams= paramsScroll2
            val paramsScroll3 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte3.layoutParams= paramsScroll3
            btn_numpantallasp1.setText("2 DE 6")
        }else if(contadorPantallas ==2){
            val paramsScroll1 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte1.layoutParams= paramsScroll1
            val paramsScroll2 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte2.layoutParams= paramsScroll2
            val paramsScroll3 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,0f )
            scrollparte3.layoutParams= paramsScroll3
            btn_numpantallasp1.setText("3 DE 6")
        }
    }


    fun selectProvinciasCantones(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
        val item = parent?.getItemAtPosition(position).toString()
        //Mostramos en un toast del elemento seleccionado

        //Toast.makeText(context, "It"+item+ " Positi "+ position, Toast.LENGTH_LONG).show()
        if (item == "Atractivos Naturales"){
            val atractivosNaturales = resources.getStringArray(R.array.tp_atractivoNaturales)

            val adapter = activity?.let {ArrayAdapter<String>(it,R.layout.item_select, atractivosNaturales)}
            with(ip_tipo){ setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Manifestaciones Culturales"){
            val manifestacionesCulturales = resources.getStringArray(R.array.tp_manifestaciones)

            val adapter = activity?.let {ArrayAdapter<String>(it,R.layout.item_select, manifestacionesCulturales)}
            with(ip_tipo){ setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
            /* Subtipos Manifestaciones Culturales*/
        }else if (item == "Arquitectura"){
            val arquitectura = resources.getStringArray(R.array.sbtp_arquitectura)

            val adapter = activity?.let {ArrayAdapter<String>(it,R.layout.item_select, arquitectura)}
            with(ip_subtipo){ setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Acervo cultural y popular"){
            val arr = resources.getStringArray(R.array.sbtp_acervo)

            val adapter = activity?.let {ArrayAdapter<String>(it,R.layout.item_select, arr)}
            with(ip_subtipo){ setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Acontecimientos Programados"){
            val arr = resources.getStringArray(R.array.sbtp_acontecimientospro)

            val adapter = activity?.let {ArrayAdapter<String>(it,R.layout.item_select, arr)}
            with(ip_subtipo){ setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Realizaciones Técnicas Científicas"){
            val arr = resources.getStringArray(R.array.sbtp_realizacionestecnicas)
            val adapter = activity?.let {ArrayAdapter<String>(it,R.layout.item_select, arr)}
            with(ip_subtipo){ setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
            /* FINNN Subtipos Manifestaciones Culturales*/

            /* Subtipos Atractivos Naturales*/
        }else if (item == "Desiertos"){
            val arr = resources.getStringArray(R.array.sbtp_desiertos)
            val adapter = activity?.let {ArrayAdapter<String>(it,R.layout.item_select, arr)}
            with(ip_subtipo){ setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Ambientes Lacustres"){
            val adapter = activity?.let {ArrayAdapter<String>(it,R.layout.item_select, resources.getStringArray(R.array.sbtp_ambienteslacustres))}
            with(ip_subtipo){ setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Ríos"){
            val adapter = activity?.let {ArrayAdapter<String>(it,R.layout.item_select, resources.getStringArray(R.array.sbtp_rios))}
            with(ip_subtipo){ setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Bosques"){
            val adapter = activity?.let {ArrayAdapter<String>(it,R.layout.item_select, resources.getStringArray(R.array.sbtp_bosques))}
            with(ip_subtipo){ setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Aguas Subrterráneas"){
            val adapter = activity?.let {ArrayAdapter<String>(it,R.layout.item_select, resources.getStringArray(R.array.sbtp_aguassubterraneas))}
            with(ip_subtipo){ setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Fenómenos Espeleológicos"){
            val adapter = activity?.let {ArrayAdapter<String>(it,R.layout.item_select, resources.getStringArray(R.array.sbtp_fenomenos_es))}
            with(ip_subtipo){ setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Fenómenos Geológicos"){
            val adapter = activity?.let {ArrayAdapter<String>(it,R.layout.item_select, resources.getStringArray(R.array.sbtp_fenomenos_ge))}
            with(ip_subtipo){ setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Costas o Litorales"){
            val adapter = activity?.let {ArrayAdapter<String>(it,R.layout.item_select, resources.getStringArray(R.array.sbtp_costaslitorales))}
            with(ip_subtipo){ setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Ambientes Marinos"){
            val adapter = activity?.let {ArrayAdapter<String>(it,R.layout.item_select, resources.getStringArray(R.array.sbtp_ambientesmarinos))}
            with(ip_subtipo){ setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment

            }
        }else if (item == "Tierras Insulares"){
            val adapter = activity?.let {ArrayAdapter<String>(it,R.layout.item_select, resources.getStringArray(R.array.sbtp_tierrasinsulares))}
            with(ip_subtipo){ setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Montañas"){
            val arr = resources.getStringArray(R.array. sbtp_Montanias)

            val adapter = activity?.let {ArrayAdapter<String>(it,R.layout.item_select, arr)}
            with(ip_subtipo){ setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }
        // FINN subtipos sbtp Atractivos Naturales

        //============================================PROVINCIAS Y CANTONES ============================================
        else if (item == "Azuay") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select,resources.getStringArray(R.array.cantones_azuay) )}
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Bolivar") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_bolivar)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Cañar") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select,resources.getStringArray(R.array.cantones_caniar)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Carchi") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_carchi)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Cotopaxi") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select,resources.getStringArray(R.array.cantones_cotopaxi)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Chimborazo") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_chimborazo)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "El Oro") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_oro)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Esmeraldas") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_esmeraldas)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Guayas") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_guayas)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Imbabura") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_imbabura)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Loja") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_loja)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Los Ríos") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_rios)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Manabi") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_manabi)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Morona Santiago") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_morona)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Napo") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_napo)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Pastaza") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_pastaza)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Pichincha") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_pichincha)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Tungurahua") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_tungurahua)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Zamora Chinchipe") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_zamora)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Galapagos") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_galapagos)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Sucumbios") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_sucumbios)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Orellana") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_orellana)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Santo Domingo de los Tsachilas") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_santodomingo)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if (item == "Santa Elena") {
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.cantones_santaelena)) }
            with(ip_canton) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Cuenca"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_cuenca)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Girón"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_giron)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Gualaceo"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_gualaceo)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Nabón"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_nabon)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Paute"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_paute)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Pucara"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_pucara)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "San Fernando"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_sanfernando)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Santa Isabel"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_santaisabel)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Sigsig"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_sigsig)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Oña"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_ona)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Chordeleg"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_chordeleg)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "El Pan"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_elpan)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Sevilla de Oro"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_sevilladeoro)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Guachapala"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_guachapala)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Camilo Ponce Enriquez"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_camiloponce)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
            // cantones Bolivar
        }else if(item == "Guaranda"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_guaranda)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Chillanes"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_chillanes)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Chimbo"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_chimbo)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Echendía"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_echendia)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "San Miguel"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_sanmiguel)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Caluma"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_caluma)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Las Naves"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_lasnaves)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Azogues"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_azogues)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Biblián"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_biblian)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Cañar"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_caniar)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "La Troncal"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_latroncal)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "El Tambo"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_tambo)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Déleg"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_deleg)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Suscal"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_suscal)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Tulcán"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_tulcan)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Bolívar"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_bolivar)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }

        }else if(item == "Espejo"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_espejo)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Mira"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_mira)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Montúfar"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_montufar)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "San Pedro de Huaca"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_sanpedro)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Latacunga"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_latacunga)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "La Maná"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_lamana)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Pangua"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_pangua)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Pujili"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_pujili)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Salcedo"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_salcedo)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Saquisilí"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_saquisili)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Sigchos"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_sigchos)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Riobamba"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_riobamba)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Alausi"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_alausi)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Colta"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_colta)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Chambo"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_chambo)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Chunchi"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_chunchi)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Guamote"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_guamote)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Guano"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_guano)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Pallatanga"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_pallatanga)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Penipe"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_penipe)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Cumandá"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_cumanda)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }// EL ORO
        else if(item == "Machala"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_machala)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Arenillas"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_arenillas)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Atahualpa"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_atahualpa)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Balsas"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_balsas)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Chilla"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_chilla)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "El Guabo"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_elguabo)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Huaquillas"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_huaquillas)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Marcabelí"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_marcabeli)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Pasaje"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_pasaje)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Piñas"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_pinias)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Portovelo"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_portovelo)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Santa Rosa"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_santarosa)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Zaruma"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_zaruma)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Las Lajas"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_laslajas)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
            // ESMERALDAS
        }else if(item == "Esmeraldas"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_esmeraldas)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Eloy Alfaro"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_eloyalfaro)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Muisne"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_muisne)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Quinindé"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.parroquias_quininde)) }
            with(ip_parroquias) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }

    }

}

