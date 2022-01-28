package com.example.turism_register.fragments


import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout.LayoutParams.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.turism_register.R
import com.github.chrisbanes.photoview.PhotoView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_lugar_main.*
import kotlinx.android.synthetic.main.fragment_add_lugar_main.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class AddLugarMainFragment : Fragment(), AdapterView.OnItemClickListener{
    private var email = ""
    private lateinit var database: FirebaseFirestore
    private var contadorPantallas = 0
    private var id : String = ""
    private var is_editar = ""

    private lateinit var contextF: Context
    private lateinit var dialog : AlertDialog
    // URL de la imagen del atractivo
    var ImageUri: Uri? = null
    val folder: StorageReference = FirebaseStorage.getInstance().getReference()
    var urlImagen =  "https://bit.ly/3IJC7q5"

    // Barra de progreso
    private var arrayContar  = arrayOf<String>()
    private var contarEncuesta : Int = 0



    // listas de los chekbox seleccionados
    var formaPago_p2 = mutableListOf<String>()
    var transporte_p3 = mutableListOf<String>()
    var accesibilidad_discapacidad = mutableListOf<String>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contextF = context
    }

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
        btn_subir_imagen_atr.setOnClickListener {
            selectImage()
        }

        // ===============FIN subir imagen
        (activity as AppCompatActivity).bottom_navegation_view.menu.findItem(R.id.fragmentToReturn).isVisible = false


        email = this.arguments?.getString("email").toString()

        is_editar = this.arguments?.getString("isEditar").toString()
        usuario_add_lugar.setText(email)

        // Funciones a cada pantalla
        addLugarParte1()
        addLugarParte2()
        addLugarParte3()
        //Contro de edicion y creacion
        if (is_editar == "false"){
            id = ('A'..'Z').random() + (555..99999).random().toString() + ('A'..'Z').random() +
                    (55555..9999999).random().toString()+ ('A'..'Z').random()

        }else if(is_editar == "true"){
            btn_guardarLugarMain.setText("ACTUALIZAR")
            id = this.arguments?.getString("id").toString()
            isEditarLlenandoCampos()
        }
        //FIn Contro de edicion y creacion

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
                val paramsScroll4 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
                scrollparte4.layoutParams= paramsScroll4

            }
            partesFormulario()
            if(contadorPantallas < 2){
                btn_siguiente_add_lugar.isVisible = true
            }
        }
        btn_siguiente_add_lugar.setOnClickListener{
            if(ip_nombre_atr.text.toString().isEmpty() || ip_descripcion_atr.text.toString().isEmpty()||
                ip_provincia.text.toString().isEmpty()|| ip_canton.text.toString().isEmpty() || ip_parroquias.text.toString().isEmpty() ){
                Toast.makeText(context, "Los campos de nombre, descripción, provincia, canton, parroquia, son obligatorios", Toast.LENGTH_LONG).show()
                contadorPantallas = 0
            }else{
                contadorPantallas++
            }

            partesFormulario()
            if (contadorPantallas > 0){
                btn_anterior_add_lugar.isVisible = true
            }
            if(contadorPantallas ==3){
                btn_siguiente_add_lugar.isVisible = true
            }
            if(contadorPantallas ==4){
                btn_siguiente_add_lugar.isVisible = false
            }
        }
        // Formas de Pago

        btn_guardarLugarMain.setOnClickListener {
            if(ip_nombre_atr.text.toString().isEmpty() || ip_descripcion_atr.text.toString().isEmpty()||
                ip_provincia.text.toString().isEmpty()|| ip_canton.text.toString().isEmpty() || ip_parroquias.text.toString().isEmpty() ){
                Toast.makeText(context, "Los campos de nombre, descripción, provincia, canton, parroquia, son obligatorios", Toast.LENGTH_LONG).show()

            }else{
                /// Dialog para mostrar al guardar o editar una ecuesta
                 dialog  = AlertDialog.Builder(contextF).create()
                val dialogView:View = LayoutInflater.from(contextF).inflate(R.layout.editanto_agregando_dialog_lugar, null)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val popTitleDialog: TextView = dialogView.findViewById(R.id.tv_title_guardando_editando_lugar_dialog)
                val card_view_dialog: CardView = dialogView.findViewById(R.id.card_view_dialog_modificar_agregar_lugar)
                if(is_editar == "false"){
                    popTitleDialog.setText("AGREGANDO")
                    card_view_dialog.setCardBackgroundColor(ContextCompat.getColor(contextF, R.color.green))
                }else if(is_editar == "true"){
                    popTitleDialog.setText("ACTUALIZANDO")
                    card_view_dialog.setCardBackgroundColor(ContextCompat.getColor(contextF, R.color.yellow))

                }
                dialog.setView(dialogView)
                dialog.setCancelable(false)
                dialog.show()
                dialog.window?.setLayout(750, 500)

                arrayContar = arrayOf(
                    ip_nombre_atr.text.toString(),ip_descripcion_atr.text.toString(),
                    ip_categoria.text.toString(),ip_tipo.text.toString(),
                    ip_subtipo.text.toString(),ip_provincia.text.toString(),
                    ip_canton.text.toString(),ip_parroquias.text.toString(),
                    ip_calle_principal.text.toString(),
                )
                realizandoConteoEncuesta()

            }
        }

    }

    fun realizandoConteoEncuesta(){
       contarEncuesta = arrayContar.count{it.isNotEmpty()}
        cargarFotoFirebase()

    }


    fun cargarFotoFirebase(){
        if (ImageUri != null ){
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            val fileName = formatter.format(now)
            val file_name: StorageReference = folder.child("imagenes/$fileName")
            file_name.putFile(ImageUri!!).addOnSuccessListener {
                    taskSnapshot ->
                file_name.downloadUrl.addOnSuccessListener {
                        uri ->
                    urlImagen = java.lang.String.valueOf(uri)
                    cargandoDatosFirebase()
                }
            }
        }else{
            cargandoDatosFirebase()
        }
    }
    fun cargandoDatosFirebase() {
        selectFormaPago()
        servicioTransporte()
        //Toast.makeText(context, urlImagen, Toast.LENGTH_LONG).show()
        database = FirebaseFirestore.getInstance()

        database.collection("lugares").document(id).set( hashMapOf(
            "id" to id,
            "campos_llenos" to contarEncuesta.toString(),
            "campos_total" to (arrayContar.size).toString(),
            "usuario" to email,
            "imagen" to urlImagen,
            "nombre" to ip_nombre_atr.text.toString(),
            "descripcion" to ip_descripcion_atr.text.toString(),
            "categoria" to ip_categoria.text.toString(),
            "tipo" to ip_tipo.text.toString(),
            "subtipo" to ip_subtipo.text.toString(),
            "provincia" to ip_provincia.text.toString(),
            "canton" to ip_canton.text.toString(),
            "parroquia" to ip_parroquias.text.toString(),
            "calle_principal" to ip_calle_principal.text.toString(),
            "numero_calle_principal" to ip_calle_principal_numero.text.toString(),
            "transversal" to ip_calle_transversal.text.toString(),
            "barrio_sector" to ip_barrio_sector.text.toString(),
            "poblado_cercano" to ip_sitio_poblado.text.toString(),
            "latitud" to ip_latitud.text.toString(),
            "Longitud" to ip_longitug.text.toString(),
            "Altura" to ip_altura_msnm.text.toString(),
            "Tipo Administrador" to ip_tipo_admin.text.toString(),
            "Nombre Institución" to ip_nombre_inst.text.toString(),
            "Nombre Administrador" to ip_nombre_admin.text.toString(),
            "Cargo que Ocupa" to ip_cargo_ocupa.text.toString(),
            "Teléfono Celular" to ip_telefono.text.toString(),
            "Correo Electrónico" to ip_correo.text.toString(),
            "Observaciones" to ip_observaciones_admin.text.toString(),
            // Segunda pantalla
            "Clima" to clima.text.toString(),
            "Temperatura" to temperatura.text.toString(),
            "Precipitación" to precipitacion.text.toString(),
            "Línea Producto" to ip_linea_producto_p2.text.toString(),
            "Escenario localización" to ip_escenario_localiza_p2.text.toString(),
            "Tipo de Ingreso" to ip_tipo_ingreso_p2.text.toString(),
            "Horario de Ingreso" to horario_ingreso_p2.text.toString(),
            "Horario de Salida" to horario_salida_p2.text.toString(),
            "Atencion" to ip_atencion_p2.text.toString(),
            "Forma de Pago" to formaPago_p2,
            "Precio Desde" to precio_desde.text.toString(),
            "Precio hasta" to precio_hasta.text.toString(),
            "Meses recomendables de visita" to meses_recomendables_visita.text.toString(),
            "Observaciones " to atencion_observaciones.text.toString(),
            // Tercera Pantalla
            "Nombre ciudad/poblado cercano" to nombre_ciudad_cercana.text.toString(),
            "Distancia desde la ciudad/poblado cercano" to distancia_desde_hasta.text.toString(),
            "Tiempo de desplazamiento en auto" to tiempo_desplazamiento.text.toString(),
            "Codenada Latitud(grados decimales)" to ip_latitud.text.toString(),
            "Codenada Longitud(grados decimales)" to ip_longitug.text.toString(),
            "Observaciones Accesibilidad" to accesibilidad_observaciones.text.toString(),
                // - Vias Accesso tererestre
            "Via Acceso terrestre" to hashMapOf(
                "Coordenada Inicio" to ip_coordenada_inicio.text.toString(),
                "Coordenada Fin" to ip_coordenada_fin.text.toString(),
                "Distancia(Km)" to ip_distanciakm.text.toString(),
                "Tipo Material" to ip_tipo_material.text.toString(),
                "Estado" to ip_estado_acceso.text.toString(),
                "Observaciones" to ip_observaciones_acceso.text.toString()
            ),
            //============================
            //============================
                // - Vias Accesso tererestre
            "Via Acceso Acuatico" to hashMapOf(
                "Tipo de vía " to ip_tipo_via_p3.text.toString(),
                "Tipo de acceso" to ip_tipo_acuatico_p3.text.toString(),
                "Muelle de Partida" to ip_muelle_partida.text.toString(),
                "Estado Muelle partida" to ip_estado_muelle_partida.text.toString(),
                "Muelle de llegada" to ip_muelle_llegada.text.toString(),
                "Estado Muelle de Llegada" to ip_estado_muelle_llegada.text.toString()
            ),
            "Vía Acceso Áereo" to ip_tipo_aereo_p3.text.toString(),
            "Servicio de Transporte" to transporte_p3,
            "Accesibilidad Discapacidad" to hashMapOf(
                "Accesible" to accesibilidad_discapacidad,
                "Observaciones" to ip_observaciones_accesibilidad.text.toString()
            )


        ))

        Toast.makeText(context, "LUGAR AGREGADO", Toast.LENGTH_SHORT).show()
        dialog.dismiss()
        // funcionalidades de actividad
        (activity as AppCompatActivity).onBackPressed()
    }
    fun isEditarLlenandoCampos(){
        database.collection("lugares").document(id).get().addOnSuccessListener {
            ip_nombre_atr.setText(it.get("nombre") as String)
            ip_descripcion_atr.setText(it.get("descripcion") as String)
            ip_categoria.setText(it.get("categoria") as String)
            ip_tipo.setText(it.get("tipo") as String)
            ip_subtipo.setText(it.get("subtipo") as String)
            ip_provincia.setText(it.get("provincia") as String)
            ip_canton.setText(it.get("canton") as String)
            ip_parroquias.setText(it.get("parroquia") as String)
            ip_calle_principal.setText(it.get("calle_principal") as String)
            Glide.with(contextF).load(it.get("imagen")).into(firebaseImage)
            urlImagen = it.get("imagen").toString()

        }
    }

    // ========= Imagen

    fun selectImage(){
        val intent = Intent()
        intent.type ="*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == RESULT_OK){
            ImageUri = data?.data!!
            firebaseImage.setImageURI(ImageUri)

        }

    }
    // Array Checkbox
    fun selectFormaPago(){
        if(form_pago_dinero_elect.isChecked){
            formaPago_p2.add((form_pago_dinero_elect.hint).toString())
        }
        if(form_pago_tarjeta_debito.isChecked){
            formaPago_p2.add((form_pago_tarjeta_debito.hint).toString())
        }
        if(form_pago_tarjeta_credito.isChecked){
            formaPago_p2.add((form_pago_tarjeta_credito.hint).toString())
        }
        if(form_pago_deposito.isChecked){
            formaPago_p2.add((form_pago_deposito.hint).toString())
        }
        if(form_pago_efectivo.isChecked){
            formaPago_p2.add((form_pago_efectivo.hint).toString())
        }
        if(form_pago_cheque.isChecked){
            formaPago_p2.add((form_pago_cheque.hint).toString())
        }
    }
    fun servicioTransporte(){
        if(trasnsporte_bus.isChecked){
            transporte_p3.add((trasnsporte_bus.hint).toString())
        }
        if(trasnsporte_buseta.isChecked){
            transporte_p3.add((trasnsporte_buseta.hint).toString())
        }
        if(transporte_lancha.isChecked){
            transporte_p3.add((transporte_lancha.hint).toString())
        }
        if(trasnsporte_bote.isChecked){
            transporte_p3.add((trasnsporte_bote.hint).toString())
        }
        if(transporte_barco.isChecked){
            transporte_p3.add((transporte_barco.hint).toString())
        }
        if(trasnsporte_mototaxi.isChecked){
            transporte_p3.add((trasnsporte_mototaxi.hint).toString())
        }
    }

    fun accesibilidadDiscapacidad(){
        if(acc_discap_visual.isChecked){
            accesibilidad_discapacidad.add((acc_discap_visual.hint).toString())
        }
        if(acc_discap_fisica.isChecked){
            accesibilidad_discapacidad.add((acc_discap_fisica.hint).toString())
        }
        if(acc_discap_auditiva.isChecked){
            accesibilidad_discapacidad.add((acc_discap_auditiva.hint).toString())
        }
        if(acc_discap_intelectual.isChecked){
            accesibilidad_discapacidad.add((acc_discap_intelectual.hint).toString())
        }
        if(accesibilidad_general.isChecked){
            accesibilidad_discapacidad.add((accesibilidad_general.hint).toString())
        }
        if(acc_discap_no_accesible.isChecked){
            accesibilidad_discapacidad.add((acc_discap_no_accesible.hint).toString())
        }
    }
    // Slect categorias, provincias cantones y parroquias

    fun addLugarParte1() {
        // DropDown de provincias
        val categorias = resources.getStringArray(R.array.categorias)

        val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, categorias) }
        with(ip_categoria) {
            setAdapter(adapter)
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
    fun addLugarParte2(){
        //PARTE 2========
        // Select para mostrar o no los input de la parte 2
        cb_caract_clima_p2.setOnClickListener{
            linearH11p2.toggleVisibility()
        }
        linea_producto_p2.setOnClickListener{
            linearH22p2.toggleVisibility()
        }
        escenario_localiza_p2.setOnClickListener{
            linearH23p2.toggleVisibility()
        }
        ingreso_atractivo_p2.setOnClickListener{
            linearH24p2.toggleVisibility()
        }

        // select linea producto parte 2
        var adapter = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.linea_producto_p2)
            )
        }
        with(ip_linea_producto_p2) {
            setAdapter(adapter)
            onItemClickListener = this@AddLugarMainFragment
        }
        // Escenario donde se localiza
        var adapter2 = activity?.let { ArrayAdapter<String>(
            it,
            R.layout.item_select,
            resources.getStringArray(R.array.escenario_localiza_p2)
        )
        }
        with(ip_escenario_localiza_p2) {
            setAdapter(adapter2)
            onItemClickListener = this@AddLugarMainFragment
        }
        // Ingreso Atractivo
        var adapter3 = activity?.let { ArrayAdapter<String>(
            it,
            R.layout.item_select,
            resources.getStringArray(R.array.tipo_ingreso_p2)
        )
        }
        with(ip_tipo_ingreso_p2) {
            setAdapter(adapter3)
            onItemClickListener = this@AddLugarMainFragment
        }
        // Atención Ingreso
        var adapter4 = activity?.let { ArrayAdapter<String>(
            it,
            R.layout.item_select,
            resources.getStringArray(R.array.atencion_p2)
        )
        }
        with(ip_atencion_p2) {
            setAdapter(adapter4)
            onItemClickListener = this@AddLugarMainFragment
        }
        // Tipo de Ingreso
        var adapter5 = activity?.let { ArrayAdapter<String>(
            it,
            R.layout.item_select,
            resources.getStringArray(R.array.tipo_ingreso_p2)
        )
        }
        with(ip_tipo_via_p3) {
            setAdapter(adapter5)
            onItemClickListener = this@AddLugarMainFragment
        }
        // Tipo de Ingreso
        var adapter6 = activity?.let { ArrayAdapter<String>(
            it,
            R.layout.item_select,
            resources.getStringArray(R.array.tipo_acuatico_p3)
        )
        }
        with(ip_tipo_acuatico_p3) {
            setAdapter(adapter6)
            onItemClickListener = this@AddLugarMainFragment

        }
        // Tipo de Ingreso
        var adapter7 = activity?.let { ArrayAdapter<String>(
            it,
            R.layout.item_select,
            resources.getStringArray(R.array.tipo_aereo_p3)
        )
        }
        with(ip_tipo_aereo_p3) {
            setAdapter(adapter7)
            onItemClickListener = this@AddLugarMainFragment

        }
        // Frecuencia de Transporte
        var adapter8 = activity?.let { ArrayAdapter<String>(
            it,
            R.layout.item_select,
            resources.getStringArray(R.array.frecuencia_transporte_p3)
        )
        }
        with(ip_frecuencia_transporte_p3) {
            setAdapter(adapter8)
            onItemClickListener = this@AddLugarMainFragment

        }
        // Estado Señalización
        var adapter9 = activity?.let { ArrayAdapter<String>(
            it,
            R.layout.item_select,
            resources.getStringArray(R.array.estado_senializacion)
        )
        }
        with(ip_senializacion_estado_p3) {
            setAdapter(adapter9)
            onItemClickListener = this@AddLugarMainFragment

        }
        // Alojamiento
        var adapter10 = activity?.let { ArrayAdapter<String>(
            it,
            R.layout.item_select,
            resources.getStringArray(R.array.alojamiento_planta_atr_poblado)
        )
        }
        with(ip_planta_atr_p4) {
            setAdapter(adapter10)
            onItemClickListener = this@AddLugarMainFragment

        }
        // Alojamiento poblado
        with(ip_planta_poblado_p4) {
            setAdapter(adapter10)
            onItemClickListener = this@AddLugarMainFragment

        }
        // Alojamiento
        var adapter11 = activity?.let { ArrayAdapter<String>(
            it,
            R.layout.item_select,
            resources.getStringArray(R.array.alimentos_bebidas)
        )
        }
        with(ip_alimentos_bebidas_p4) {
            setAdapter(adapter11)
            onItemClickListener = this@AddLugarMainFragment

        }
        var adapter12 = activity?.let { ArrayAdapter<String>(
            it,
            R.layout.item_select,
            resources.getStringArray(R.array.agencia_viajes)
        )
        }
        with(ip_agencia_viajes_p4) {
            setAdapter(adapter12)
            onItemClickListener = this@AddLugarMainFragment

        }
        // parte 4

        // Atención Facilidades en el entorno
        var adapter13 = activity?.let { ArrayAdapter<String>(
            it,
            R.layout.item_select,
            resources.getStringArray(R.array.facilidades_categoria)
        )
        }
        with(ip_categoria_facilidad_entorno) {
            setAdapter(adapter13)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter14 = activity?.let { ArrayAdapter<String>(
            it,
            R.layout.item_select,
            resources.getStringArray(R.array.tipo_apoyo)
        )
        }
        with(ip_tipo_facilidad_entorno) {
            setAdapter(adapter14)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter15 = activity?.let { ArrayAdapter<String>(
            it,
            R.layout.item_select,
            resources.getStringArray(R.array.facilidades_estado)
        )
        }
        with(ip_estado_facilidad_entorno) {
            setAdapter(adapter15)
            onItemClickListener = this@AddLugarMainFragment
        }
        // Parte 5
        var adapter16 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.conservacion)
            )
        }
        with(ip_conservacion_atr) {
            setAdapter(adapter16)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter17 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.fatores_naturales)
            )
        }
        with(ip_factores_naturales_atr) {
            setAdapter(adapter17)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter18 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.fatores_antropicos)
            )
        }
        with(ip_factores_antropi_atr) {
            setAdapter(adapter18)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter19 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.fatores_antropicos)
            )
        }
        with(ip_factores_antropi_entorno) {
            setAdapter(adapter19)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter20 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.fatores_naturales)
            )
        }
        with(ip_factores_naturales_entorno) {
            setAdapter(adapter20)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter21 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.conservacion)
            )
        }
        with(ip_conservacion_entorno) {
            setAdapter(adapter21)
            onItemClickListener = this@AddLugarMainFragment
        }

        // Parte 6
        var adapter22 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.senialetica_ambiente)
            )
        }
        with(ip_higiene_senialetica_ambientes) {
            setAdapter(adapter22)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter23 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.tipo_ambiente_areas_urbanas)
            )
        }
        with(ip_higiene_senialetica_tipo) {
            setAdapter(adapter23)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter24 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.materialidad)
            )
        }
        with(ip_higiene_materialidad) {
            setAdapter(adapter24)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter25 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.facilidades_estado)
            )
        }
        with(ip_higiene_estado) {
            setAdapter(adapter25)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter26 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.centro_salud)
            )
        }
        with(ip_higiene_salud_atr) {
            setAdapter(adapter26)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter27 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.centro_salud)
            )
        }
        with(ip_higiene_salud_pbl) {
            setAdapter(adapter27)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter28 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.higiene_seguridad)
            )
        }
        with(ip_seguridad) {
            setAdapter(adapter28)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter29 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.comunic_telefonia)
            )
        }
        with(ip_comunic_telefonia_atr) {
            setAdapter(adapter29)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter30 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.comunic_internet)
            )
        }
        with(ip_comunic_internet_atr) {
            setAdapter(adapter30)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter31 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.comunic_telefonia)
            )
        }
        with(ip_comunic_telefonia_pbl) {
            setAdapter(adapter31)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter32 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.comunic_internet)
            )
        }
        with(ip_comunic_internet_pbl) {
            setAdapter(adapter32)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter33 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.comunic_radio_portatil)
            )
        }
        with(ip_comunic_radio_atr) {
            setAdapter(adapter33)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter34 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.comunic_radio_portatil)
            )
        }
        with(ip_comunic_radio_pbl) {
            setAdapter(adapter34)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter35 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.comunic_radio_portatil)
            )
        }
        with(ip_multiamenazas) {
            setAdapter(adapter35)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter36 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.medio_promocional)
            )
        }
        with(ip_medio_promocional) {
            setAdapter(adapter36)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter37 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.select_si_no)
            )
        }
        with(select_si_no_p11) {
            setAdapter(adapter37)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter38 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.select_tipo)
            )
        }
        with(select_tipo_p11) {
            setAdapter(adapter38)
            onItemClickListener = this@AddLugarMainFragment
        }

        var adapter40 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.select_si_no)
            )
        }
        with(select_si_no_2p11) {
            setAdapter(adapter40)
            onItemClickListener = this@AddLugarMainFragment
        }
        var adapter41 = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.item_select,
                resources.getStringArray(R.array.frecuencia_visitas)
            )
        }
        with(ip_frecuencia_visitas) {
            setAdapter(adapter41)
            onItemClickListener = this@AddLugarMainFragment
        }

        // array forma de pago

        when {
            form_pago_efectivo.isChecked -> {

                formaPago_p2.add(form_pago_efectivo.hint.toString())

            }
            form_pago_deposito.isChecked -> {
                formaPago_p2.add(form_pago_deposito.hint.toString())

            }
            form_pago_tarjeta_credito.isChecked -> {
                formaPago_p2.add(form_pago_tarjeta_credito.hint.toString())
            }
            form_pago_tarjeta_debito.isChecked -> {
                formaPago_p2.add(form_pago_tarjeta_debito.hint.toString())

            }
            form_pago_cheque.isChecked -> {
                formaPago_p2.add(form_pago_cheque.hint.toString())
            }
        }

    }

    fun addLugarParte3(){
        cb_accesibilidad_atr_S.setOnClickListener{
            linearH11p3.toggleVisibility()
        }
        cb_vias_acceso.setOnClickListener{
            linearH2p3.toggleVisibility()
        }
        cb_terrestre.setOnClickListener{
            linearViaTerrestre_p3.toggleVisibility()
        }
        cb_acuatico.setOnClickListener{
            linearViaAcuatica_p3.toggleVisibility()
        }
        cb_aereo.setOnClickListener{
            linearViaArear_p3.toggleVisibility()
        }
        cb_servicio_transporte.setOnClickListener{
            linear_servicio_transporte_p3.toggleVisibility()
        }
        cb_detalle_transporte.setOnClickListener {
            linear_detalle_transporte_p3.toggleVisibility()
        }
        cb_condicion_accesibilidad.setOnClickListener {
            linear_accecibilidad_p3.toggleVisibility()
        }
        cb_senializacion.setOnClickListener {
            linear_senializacion_p3.toggleVisibility()
        }
        // ============= Parte 4
        cb_planta_turistica.setOnClickListener {
            linearV1p4.toggleVisibility()
        }
        cb_planta_turistica_atr.setOnClickListener {
            linearH11p4.toggleVisibility()
        }
        cb_planta_turistica_poblado.setOnClickListener {
            linearH12p4.toggleVisibility()
        }
        cb_alimentos_bebidas.setOnClickListener {
            linearH13p4.toggleVisibility()
        }
        cb_agencias_viajes.setOnClickListener {
            linearH14p4.toggleVisibility()
        }
        cb_facilidad_entorno_atr.setOnClickListener {
            linearV2p4.toggleVisibility()
        }
        cb_facilidad_complementarios.setOnClickListener {
            linear_complement_atr.toggleVisibility()
        }
        cb_facilidad_complementarios_atr.setOnClickListener {
            linear_complment_atr2.toggleVisibility()
        }
        cb_facilidad_complementarios_pobl.setOnClickListener {
            linear_complment_pobl2.toggleVisibility()
        }
        // Parte 5 ==============
        cb_complement_atr.setOnClickListener {
            linearV1p5.toggleVisibility()
        }
        cb_conservacion_alteracion_atr.setOnClickListener {
            linearV11p5.toggleVisibility()
        }
        cb_complement_entorno.setOnClickListener {
            linearV2p5.toggleVisibility()
        }
        cb_conservacion_alteracion_entorno.setOnClickListener {
            linearV21p5.toggleVisibility()
        }
        cb_conservacion_declaratoria.setOnClickListener {
            linearV3p5.toggleVisibility()
        }
        cb_higiene_seguridad.setOnClickListener {
            linearV1p6.toggleVisibility()
        }
        cb_higiene_senialetica.setOnClickListener {
            linearV2p6.toggleVisibility()
        }
        cb_higiene_salud.setOnClickListener {
            linearV3p6.toggleVisibility()
        }
        cb_seguridad.setOnClickListener {
            linearV4p6.toggleVisibility()
        }
        cb_comunicacion.setOnClickListener {
            linearV5p6.toggleVisibility()
        }
        cb_multiamenazas.setOnClickListener {
            cb_multiamenazas.toggleVisibility()
        }
        // Apartado 8  de politicas y regulaciones
        cb_politicas_regulaciones_si.setOnClickListener {
            if(cb_politicas_regulaciones_si.isChecked){
                cb_politicas_regulaciones_no.isVisible = false
                ip_politicas_anio_elaboracion.isVisible = true
            }
            if(cb_politicas_regulaciones_si.isChecked == false) {
                cb_politicas_regulaciones_no.isVisible = true
                ip_politicas_anio_elaboracion.isVisible = false
            }
        }
        cb_ordenanzas_si.setOnClickListener {
            cb_ordenanzas_no.isVisible = !cb_ordenanzas_si.isChecked
        }
        cb_normativas_si.setOnClickListener {
            cb_normativas_no.isVisible = !cb_normativas_si.isChecked
        }
        cb_planificacion_turistica_si.setOnClickListener {
            cb_planificacion_turistica_no.isVisible = !cb_planificacion_turistica_si.isChecked

        }
        // Apartado 9
        cb_actividades_atract_agua.setOnClickListener {
            linearV1p8.toggleVisibility()
        }
        cb_actividades_atract_aire.setOnClickListener {
            linearV2p8.toggleVisibility()
        }
        cb_actividades_atract_superf.setOnClickListener {
            linearV3p8.toggleVisibility()
        }
        cb_atr_cult_tang_intag.setOnClickListener {
            linearV4p8.toggleVisibility()
        }
        cb_temporalidad_visitas_alta.setOnClickListener {
            linearV11p9.toggleVisibility()
        }
        cb_temp_visitas_bajas.setOnClickListener {
            linearV2p9.toggleVisibility()
        }
        cb_turista_nacional.setOnClickListener {
            linearV4p9.toggleVisibility()
        }
        cb_turista_extranjero.setOnClickListener {
            linearV5p9.toggleVisibility()
        }
        cb_frecuencia_visitas.setOnClickListener {
            linearV1p9.toggleVisibility()
        }

        cb_demanda_dias_visita.setOnClickListener {
            ip_demandas_dias_visita.toggleVisibility()
        }
        cb_demanda_frecuencia_visita.setOnClickListener {
            demanda_frecuencia_visita.toggleVisibility()
        }
        cb_nivel_instruccion.setOnClickListener {
            linearV2p10.toggleVisibility()
        }
        cb_personas_capacitadas.setOnClickListener {
            linearV3p10.toggleVisibility()
        }
        cb_personas_idiomas.setOnClickListener {
            linearV4p10.toggleVisibility()
        }
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
            val paramsScroll4 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte4.layoutParams= paramsScroll4
            btn_numpantallasp1.setText("2 DE 6")
        }else if(contadorPantallas ==2){
            val paramsScroll1 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte1.layoutParams= paramsScroll1
            val paramsScroll2 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte2.layoutParams= paramsScroll2
            val paramsScroll3 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,0f )
            scrollparte3.layoutParams= paramsScroll3
            val paramsScroll4 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte4.layoutParams= paramsScroll4
            btn_numpantallasp1.setText("3 DE 6")
        }
        else if(contadorPantallas ==3){
            val paramsScroll1 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte1.layoutParams= paramsScroll1
            val paramsScroll2 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte2.layoutParams= paramsScroll2
            val paramsScroll3 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte3.layoutParams= paramsScroll3
            val paramsScroll4 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,0f )
            scrollparte4.layoutParams= paramsScroll4
            btn_numpantallasp1.setText("4 DE 6")
        }
        else if(contadorPantallas ==5){
            val paramsScroll1 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte1.layoutParams= paramsScroll1
            val paramsScroll2 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte2.layoutParams= paramsScroll2
            val paramsScroll3 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte3.layoutParams= paramsScroll3
            val paramsScroll4 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte4.layoutParams= paramsScroll4
            val paramsScroll5 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,0f )
            scrollparte5.layoutParams= paramsScroll5
            btn_numpantallasp1.setText("5 DE 6")
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
            // Parte 5 formulario
        }else if(item == "De apoyo a la gestión turística"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.tipo_apoyo)) }
            with(ip_tipo_facilidad_entorno) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "De observación y vigilancia"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.tipo_observacion)) }
            with(ip_tipo_facilidad_entorno) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "De recorrido y descanso"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.tipo_observacion)) }
            with(ip_tipo_facilidad_entorno) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "De servicio"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.tipo_servicio)) }
            with(ip_tipo_facilidad_entorno) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "De servicio"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.tipo_servicio)) }
            with(ip_tipo_facilidad_entorno) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
            // Parte 6 ===============
        }else if(item == "En áreas urbanas"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.tipo_ambiente_areas_urbanas)) }
            with(ip_higiene_senialetica_tipo) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "En áreas naturales"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.tipo_ambiente_areas_naturales)) }
            with(ip_higiene_senialetica_tipo) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Letreros informativos"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.letreros)) }
            with(ip_higiene_senialetica_tipo) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }else if(item == "Señalética interna de seguridad"){
            val adapter = activity?.let { ArrayAdapter<String>(it, R.layout.item_select, resources.getStringArray(R.array.senialetica_interna)) }
            with(ip_higiene_senialetica_tipo) {
                setAdapter(adapter)
                onItemClickListener = this@AddLugarMainFragment
            }
        }

    }

}

