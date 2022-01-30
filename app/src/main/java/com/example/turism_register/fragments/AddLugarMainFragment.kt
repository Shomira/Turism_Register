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
    var urlImagen =  "https://firebasestorage.googleapis.com/v0/b/pruebaturismo-38afa.appspot.com/o/lugarTuritico_default.jpg?alt=media&token=e676058f-6327-4714-a1bd-4583335da6b8"

    // Barra de progreso
    private var arrayContar  = arrayOf<String>()
    private var contarEncuesta : Int = 0



    // listas de los chekbox seleccionados
    var formaPago_p2 = mutableListOf<String>()
    var transporte_p3 = mutableListOf<String>()
    var accesibilidad_discapacidad = mutableListOf<String>()
    var complementos_atr = mutableListOf<String>()
    var complementos_pbl = mutableListOf<String>()
    var servicios_basicos = mutableListOf<String>()

    // variables checked
    var plan_contigencias: String = ""
    //preguntas pantalla  8
    var plan_desarrollo: String = ""
    var doc_planificacion: String = ""
    var aplicac_normativa: String = ""
    var aplic_ordenanzas: String = " "
    var promocion_cantonal_turistica: String = ""
    var promocion_atractivo: String =""

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

        // Funciones a las pantallas
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
            if (contadorPantallas > 2){
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
            if (contadorPantallas > 0){
                btn_anterior_add_lugar.isVisible = true
            }
            if (contadorPantallas > 0){
                btn_siguiente_add_lugar.isVisible = true
            }
            if(contadorPantallas ==10){
                btn_siguiente_add_lugar.isVisible = false
            }
            partesFormulario()
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
                        ip_calle_principal.text.toString(),ip_calle_principal_numero.text.toString(),
                        ip_calle_transversal.text.toString(),ip_barrio_sector.text.toString(),
                        ip_sitio_poblado.text.toString(),ip_latitud.text.toString(),ip_longitug.text.toString(),
                        ip_altura_msnm.text.toString(),ip_tipo_admin.text.toString(),ip_nombre_inst.text.toString(),
                        ip_nombre_admin.text.toString(),ip_cargo_ocupa.text.toString(),ip_telefono.text.toString(),
                        ip_correo.text.toString(),ip_observaciones_admin.text.toString(),clima.text.toString(),
                        temperatura.text.toString(),precipitacion.text.toString(),
                        ip_linea_producto_p2.text.toString(),ip_escenario_localiza_p2.text.toString(),
                        ip_tipo_ingreso_p2.text.toString(),horario_ingreso_p2.text.toString(),
                        horario_salida_p2.text.toString(),ip_atencion_p2.text.toString(),
                        formaPago_p2.toString(),precio_desde.text.toString(),precio_hasta.text.toString(),
                        meses_recomendables_visita.text.toString(),atencion_observaciones.text.toString(),
                        nombre_ciudad_cercana.text.toString(),distancia_desde_hasta.text.toString(),
                        tiempo_desplazamiento.text.toString(),ip_latitud.text.toString(),
                        ip_longitug.text.toString(),accesibilidad_observaciones.text.toString(),
                        ip_coordenada_inicio.text.toString(),ip_coordenada_fin.text.toString(),
                        ip_distanciakm.text.toString(),ip_tipo_material.text.toString(),
                        ip_estado_acceso.text.toString(),ip_observaciones_acceso.text.toString(),
                        ip_tipo_via_p3.text.toString(),ip_tipo_acuatico_p3.text.toString(),
                        ip_muelle_partida.text.toString(),ip_estado_muelle_partida.text.toString(),
                        ip_muelle_llegada.text.toString(),ip_estado_muelle_llegada.text.toString(),
                        ip_tipo_aereo_p3.text.toString(),transporte_p3.toString(),ip_observaciones_transporte.text.toString(),
                        ip_nombre_cooperativa.text.toString(), ip_estacion_terminal.text.toString(),
                        ip_frecuencia_transporte_p3.text.toString(),ip_transporte_detalle_traslado.text.toString(),
                        accesibilidad_discapacidad.toString(),ip_observaciones_accesibilidad.text.toString(),
                        ip_senializacion_estado_p3.text.toString(),ip_observaciones_senializacion.text.toString(),
                        ip_planta_atr_p4.text.toString(), establecimientos_registrados.text.toString(),
                        numero_habitaciones.text.toString(),numero_plazas.text.toString(),
                        planta_turistica_observaciones.text.toString(),ip_planta_poblado_p4.text.toString(),
                        est_registrados_poblado.text.toString(),numero_habitaciones_poblado.text.toString(),
                        numero_plazas_poblado.text.toString(),planta_turistica_observaciones2.text.toString(),
                        ip_alimentos_bebidas_p4.text.toString(),est_registrados_alimentos.text.toString(),
                        numero_mesas_alimentos.text.toString(),numero_plazas_alimentos.text.toString(),
                        alimentos_bebidas_p4.text.toString(),ip_agencia_viajes_p4.text.toString(),
                        establecimientos_registrados_agencia.text.toString(),observaciones_agencias_viajes_p4.text.toString(),
                        ip_categoria_facilidad_entorno.text.toString(),ip_tipo_facilidad_entorno.text.toString(),
                        ip_estado_facilidad_entorno.text.toString(),facilidad_entorno_cantidad.text.toString(),
                        facilidad_entorno_coordenadas.text.toString(),facilidad_entorno_administrador.text.toString(),
                        complementos_atr.toString(),complementos_pbl.toString(),
                        observaciones_complement_p4.text.toString(),observaciones_complement_pobl_p4.text.toString(),
                        ip_conservacion_atr.text.toString(),observaciones_conservacion_atr_p5.text.toString(),
                        ip_factores_naturales_atr.text.toString(),ip_factores_antropi_atr.text.toString(),
                        factores_atr_p5.text.toString(),ip_conservacion_entorno.text.toString(),
                        observaciones_entorno_p5.text.toString(),ip_factores_naturales_entorno.text.toString(),
                        ip_factores_antropi_entorno.text.toString(),factores_entorno_p5.text.toString(),
                        declaratoria_declarante.text.toString(),declaratoria_denominacion.text.toString(),
                        declaratoria_fecha_creacion.text.toString(),declaratoria_alcance.text.toString(),
                        observaciones_declaratoria_p5.text.toString(),servicios_basicos.toString(),
                        observaciones_servicios_p6.text.toString(),observaciones_servicios_p6_2.text.toString(),
                        ip_higiene_senialetica_ambientes.text.toString(),ip_higiene_senialetica_tipo.text.toString(),
                        ip_higiene_materialidad.text.toString(),ip_higiene_estado.text.toString(),
                        ip_higiene_salud_atr.text.toString(),ip_observaciones_salud_atr.text.toString(),
                        ip_higiene_salud_pbl.text.toString(),ip_observaciones_higiene.text.toString(),
                        ip_seguridad.text.toString(),ip_observaciones_seguridad.text.toString(),
                        ip_comunic_telefonia_atr.text.toString(),ip_comunic_internet_atr.text.toString(),
                        ip_comunic_radio_atr.text.toString(),ip_observaciones_comunic_atr.text.toString(),
                        ip_comunic_telefonia_pbl.text.toString(),ip_comunic_internet_pbl.text.toString(),
                        ip_comunic_radio_pbl.text.toString(),ip_observaciones_comunic_pbl.text.toString(),
                        ip_multiamenazas.text.toString(),plan_contigencias,
                        ip_institucion_elaboro_doc.text.toString(),ip_nom_documento.text.toString(),
                        ip_anio_elaboracion_doc.text.toString(),ip_observaciones_multiamenazas.text.toString(),
                        plan_desarrollo,doc_planificacion,aplicac_normativa,aplic_ordenanzas,observaciones_politicas.text.toString(),
                        promocion_cantonal_turistica,promocion_atractivo,ip_observaciones_promocion.text.toString(),
                        ip_medio_promocional.text.toString(),ip_direccion_nombre.text.toString(),ip_periodicidad_promocion.text.toString(),
                        select_si_no_2p11.text.toString(),select_tipo_p11.text.toString(),
                        ip_anios_registro_viistante.text.toString(),select_si_no_p11.text.toString(),
                        ip_frecuencia_reportes.text.toString(),ip_temp_visitas_espe.text.toString(),
                        ip_temp_visitas_num.text.toString(),ip_temp_visitas_b_espe.text.toString(),
                        ip_temp_visitas_b_num.text.toString(),ip_ciudad_origen.text.toString(),
                        ip_llegada_mensual.text.toString(),ip_total_anual.text.toString(),
                        ip_observaciones_llegada_turist.text.toString(),ip_ciudad_origen_extr.text.toString(),
                        ip_llegada_mensual_ex.text.toString(),ip_total_anual_ex.text.toString(),
                        ip_observaciones_llegda_turist.text.toString(),ip_nombre_informate.text.toString(),
                        ip_contactos.text.toString(),ip_demanda_dias_l_v.text.toString(),ip_demanda_dias_f_s.text.toString(),
                        ip_demanda_dias_fer.text.toString(),ip_frecuencia_visitas.text.toString(),
                        ip_observaciones_registro.text.toString(),ip_num_personas.text.toString(),
                        ip_num_personas_nivel_primaria.text.toString(),ip_num_personas_nivel_secundaria.text.toString(),
                        ip_num_personas_nivel_tercern.text.toString(),ip_num_personas_nivel_cuarton.text.toString(),
                        ip_num_personas_nivel_otro.text.toString(),ip_num_personas_primeroa_aux.text.toString(),
                        ip_num_personas_atención_cliente.text.toString(),ip_num_personas_hospitalidad.text.toString(),
                        ip_num_personas_guianza.text.toString(),ip_num_personas_sensibilizacion.text.toString(),
                        ip_num_personas_otro.text.toString(),ip_observaciones_recurso_humano.text.toString(),
                        ip_num_personas_aleman.text.toString(),ip_num_personas_ingles.text.toString(),
                        ip_num_personas_frances.text.toString(),ip_num_personas_italiano.text.toString(),
                        ip_num_personas_chino.text.toString(),ip_num_personas_otro_idioma.text.toString(),
                        ip_observaciones_idiomas.text.toString()


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
        accesibilidadDiscapacidad()
        complementosPbl()
        complementosAtr()
        serviciosBasicos()

        if (cb_plan_contingencias.isChecked){
            plan_contigencias  =  "si"
        }else {
            plan_contigencias  = "no"
        }

        // Si y no pantalla 7 apartado 8
        if(cb_planificacion_turistica_si.isChecked){
            plan_desarrollo = "si"
        }
        if(cb_politicas_regulaciones_no.isChecked){
            plan_desarrollo = "no"
        }
        if(cb_planificacion_turistica_si.isChecked){
            doc_planificacion = "si"
        }
        if(cb_planificacion_turistica_no.isChecked){
            doc_planificacion = "no"
        }
        if(cb_normativas_si.isChecked){
            aplicac_normativa = "si"
        }
        if(cb_normativas_no.isChecked){
            aplicac_normativa = "no"
        }
        if(cb_ordenanzas_si.isChecked){
            aplic_ordenanzas = "si"
        }
        if(cb_ordenanzas_no.isChecked){
            aplic_ordenanzas = "no"
        }
        if (cb_promocion_si.isChecked){
            promocion_cantonal_turistica = "si"
        }
        if(!cb_promocion_no.isChecked){
            promocion_cantonal_turistica = "no"
        }
        if (cb_plan_promocion_si.isChecked){
            promocion_atractivo = "si"
        }
        if(!cb_plan_promocion_no.isChecked){
            promocion_atractivo = "no"
        }


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
            "longitud" to ip_longitug.text.toString(),
            "altura" to ip_altura_msnm.text.toString(),
            "tipo_administrador" to ip_tipo_admin.text.toString(),
            "nombre_institución" to ip_nombre_inst.text.toString(),
            "nombre_administrador" to ip_nombre_admin.text.toString(),
            "cargo_ocupa" to ip_cargo_ocupa.text.toString(),
            "telefono_celular" to ip_telefono.text.toString(),
            "correo_electrónico" to ip_correo.text.toString(),
            "observaciones_admin" to ip_observaciones_admin.text.toString(),
            // Segunda pantalla
            "clima" to clima.text.toString(),
            "temperatura" to temperatura.text.toString(),
            "precipitacion" to precipitacion.text.toString(),

            "linea_producto" to ip_linea_producto_p2.text.toString(), // select
            "escenario_localización" to ip_escenario_localiza_p2.text.toString(), // select
            "tipo_ingreso" to ip_tipo_ingreso_p2.text.toString(), // select
            "horario_ingreso" to horario_ingreso_p2.text.toString(),
            "horario_salida" to horario_salida_p2.text.toString(),
            "atencion" to ip_atencion_p2.text.toString(), // select
            "forma_de_pago" to formaPago_p2,
            "precio_desde" to precio_desde.text.toString(),
            "precio_hasta" to precio_hasta.text.toString(),
            "meses_recomendables_visita" to meses_recomendables_visita.text.toString(),
            "observaciones_ingreso_atractivo " to atencion_observaciones.text.toString(),
            // Tercera Pantalla
            "nombre_poblado_cercano" to nombre_ciudad_cercana.text.toString(),
            "distancia_desde_poblado_cercano" to distancia_desde_hasta.text.toString(),
            "tiempo_desplazamiento_auto" to tiempo_desplazamiento.text.toString(),
            "coordenada_latitud" to ip_latitud.text.toString(),
            "coordenada_longitud" to ip_longitug.text.toString(),
            "observaciones_accesibilidad" to accesibilidad_observaciones.text.toString(),
                // - Vias Accesso tererestre

            "acceso_terrestre_coordenada_inicio" to ip_coordenada_inicio.text.toString(),
            "acceso_terrestre_coordenada_fin" to ip_coordenada_fin.text.toString(),
            "acceso_terrestre_distancia_km" to ip_distanciakm.text.toString(),
            "acceso_terrestre_tipo_material" to ip_tipo_material.text.toString(),
            "acceso_terrestre_estado" to ip_estado_acceso.text.toString(),
            "acceso_terrestre_observaciones" to ip_observaciones_acceso.text.toString(),
            //============================
            //============================
                // - Vias Accesso Acuatico

            "acuatico_tipo_via " to ip_tipo_via_p3.text.toString(),
            "acuatico_tipo_acceso" to ip_tipo_acuatico_p3.text.toString(),
            "muelle_partida" to ip_muelle_partida.text.toString(),
            "estado_muelle_partida" to ip_estado_muelle_partida.text.toString(),
            "muelle_llegada" to ip_muelle_llegada.text.toString(),
            "estado_muelle_llegada" to ip_estado_muelle_llegada.text.toString(),

            "tipo_via_acceso_aereo" to ip_tipo_aereo_p3.text.toString(),
            "servicio_transporte" to transporte_p3,
            "observaciones_transporte" to ip_observaciones_transporte.text.toString(),
            "nombre_cooperativa" to ip_nombre_cooperativa.text.toString(),
            "estacion_terminal" to ip_estacion_terminal.text.toString(),
            "frecuencia_transporte" to ip_frecuencia_transporte_p3.text.toString(),
            "transporte_detalle_traslado" to ip_transporte_detalle_traslado.text.toString(),

            "accesible_discapacidad" to accesibilidad_discapacidad,
            "observaciones_discapacidad" to ip_observaciones_accesibilidad.text.toString(),
            "senializacion_estado" to ip_senializacion_estado_p3.text.toString(), // select
            "observaciones_senializacion" to ip_observaciones_senializacion.text.toString(),
            // pantalla 4
            "alojamiento_atr" to ip_planta_atr_p4.text.toString(), // ---select
            "establecimientos_registrados" to establecimientos_registrados.text.toString(),
            "numero_habitaciones" to numero_habitaciones.text.toString(),
            "numero_plazas" to numero_plazas.text.toString(),
            "planta_turistica_observaciones" to planta_turistica_observaciones.text.toString(),
            "alojamiento_poblado" to ip_planta_poblado_p4.text.toString(), // --select
            "est_registrados_poblado" to est_registrados_poblado.text.toString(),
            "numero_habitaciones_poblado" to numero_habitaciones_poblado.text.toString(),
            "numero_plazas_poblado" to numero_plazas_poblado.text.toString(),
            "planta_turistica_observaciones2" to planta_turistica_observaciones2.text.toString(),
            "alimentos_bebidas" to ip_alimentos_bebidas_p4.text.toString(), // select
            "est_registrados_alimentos" to est_registrados_alimentos.text.toString(),
            "numero_mesas_alimentos" to numero_mesas_alimentos.text.toString(),
            "numero_plazas_alimentos" to numero_plazas_alimentos.text.toString(),
            "alimentos_bebidas_observaciones" to alimentos_bebidas_p4.text.toString(),
            "agencia_viajes" to ip_agencia_viajes_p4.text.toString(), //select
            "establecimientos_registrados_agencia" to establecimientos_registrados_agencia.text.toString(),
            "observaciones_agencias_viajes" to observaciones_agencias_viajes_p4.text.toString(),
            "categoria_facilidad_entorno" to ip_categoria_facilidad_entorno.text.toString(), // select
            "tipo_facilidad_entorno" to ip_tipo_facilidad_entorno.text.toString(), // select
            "estado_facilidad_entorno" to ip_estado_facilidad_entorno.text.toString(),// select
            "facilidad_entorno_cantidad" to facilidad_entorno_cantidad.text.toString(),
            "facilidad_entorno_coordenadas" to facilidad_entorno_coordenadas.text.toString(),
            "facilidad_entorno_administrador" to facilidad_entorno_administrador.text.toString(),
            // problema en conteo
            "cb_facilidad_accesibilidad_entorno" to cb_facilidad_accesibilidad_entorno.hint,
            "complementos_atr" to complementos_atr,
            "complementos_pbl" to complementos_pbl,
            "observaciones_complement_atr" to observaciones_complement_p4.text.toString(),
            "observaciones_complement_pobl" to observaciones_complement_pobl_p4.text.toString(),
            // Pantalla 5
            "conservacion_atr" to ip_conservacion_atr.text.toString(), // select
            "observaciones_conservacion_atr" to observaciones_conservacion_atr_p5.text.toString(),
            "factores_naturales_atr" to ip_factores_naturales_atr.text.toString(), // select
            "factores_antropi_atr" to ip_factores_antropi_atr.text.toString(), // select
            "observaciones_factores_atr" to factores_atr_p5.text.toString(),
            "entorno" to ip_conservacion_entorno.text.toString(), // select
            "observaciones_entorno" to observaciones_entorno_p5.text.toString(),
            "factores_naturales_entorno" to ip_factores_naturales_entorno.text.toString(), // select
            "factores_antropi_entorno" to ip_factores_antropi_entorno.text.toString(), // select
            "observaciones_factores_entorno" to factores_entorno_p5.text.toString(),
            "declaratoria_declarante" to declaratoria_declarante.text.toString(),
            "declaratoria_denominacion" to declaratoria_denominacion.text.toString(),
            "declaratoria_fecha_creacion" to declaratoria_fecha_creacion.text.toString(),
            "declaratoria_alcance" to declaratoria_alcance.text.toString(),
            "observaciones_declaratoria" to observaciones_declaratoria_p5.text.toString(),
            // Pantalla 6
            "servicios_basicos" to servicios_basicos,
            "observaciones_servicios" to observaciones_servicios_p6.text.toString(),
            "observaciones_servicios_p2" to observaciones_servicios_p6_2.text.toString(),
            "higiene_senialetica_ambientes" to ip_higiene_senialetica_ambientes.text.toString(), // select
            "higiene_senialetica_tipo" to ip_higiene_senialetica_tipo.text.toString(), // select
            "higiene_materialidad" to ip_higiene_materialidad.text.toString(), // select
            "higiene_estado" to ip_higiene_estado.text.toString(), // select
            "higiene_salud_atr" to ip_higiene_salud_atr.text.toString(), // select
            "observaciones_salud_atr" to ip_observaciones_salud_atr.text.toString(),
            "higiene_salud_pbl" to ip_higiene_salud_pbl.text.toString(), //select
            "observaciones_higiene" to ip_observaciones_higiene.text.toString(),
            "seguridad" to ip_seguridad.text.toString(),  // select
            "observaciones_seguridad" to ip_observaciones_seguridad.text.toString(),
            "comunic_telefonia_atr" to ip_comunic_telefonia_atr.text.toString(), // select a
            "comunic_internet_atr" to ip_comunic_internet_atr.text.toString(), // select a
            "comunic_radio_atr" to ip_comunic_radio_atr.text.toString(), // select a
            "observaciones_comunic_atr" to ip_observaciones_comunic_atr.text.toString(),
            "comunic_telefonia_pbl" to ip_comunic_telefonia_pbl.text.toString(),// select a
            "comunic_internet_pbl" to ip_comunic_internet_pbl.text.toString(), // select a
            "comunic_radio_pbl" to ip_comunic_radio_pbl.text.toString(), // select a
            "observaciones_comunic_pbl" to ip_observaciones_comunic_pbl.text.toString(),
            "multiamenazas" to ip_multiamenazas.text.toString(), //select
            "tiene_plan_contingencia" to plan_contigencias,

            "institucion_elaboro_doc" to ip_institucion_elaboro_doc.text.toString(),
            "nom_documento" to ip_nom_documento.text.toString(),
            "anio_elaboracion_doc" to ip_anio_elaboracion_doc.text.toString(),
            "observaciones_multiamenazas" to ip_observaciones_multiamenazas.text.toString(),

            // Pantalla 7

            "plan_desarrollo" to plan_desarrollo,
            "documento_planificacion" to doc_planificacion,
            "aplica_normativa" to aplicac_normativa,
            "aplic_ordenanzas" to aplic_ordenanzas,
            "observaciones_politicas" to observaciones_politicas.text.toString(),

            // Pantalla 8 Checkboxs partado 9

            // Pantalla 9 apratado 10

            "promocion_cantonal_turistica" to promocion_cantonal_turistica,
            "promocion_atractivo" to promocion_atractivo,
            "observaciones_promocion" to ip_observaciones_promocion.text.toString(),
            "medio_promocional" to ip_medio_promocional.text.toString(),
            "direccion_nombre" to ip_direccion_nombre.text.toString(),
            "periodicidad_promocion" to ip_periodicidad_promocion.text.toString(),

            // Pantalla 10 apartado 11
            "tiene_registros" to  select_si_no_2p11.text.toString(),
            "tipo_material_reporte" to select_tipo_p11.text.toString(),
            "anios_registro_viistante" to ip_anios_registro_viistante.text.toString(),
            "reportes_estadisticos " to select_si_no_p11.text.toString(),
            "frecuencia_reportes" to ip_frecuencia_reportes.text.toString(),
            "temp_alta_especificacion" to ip_temp_visitas_espe.text.toString(),
            "temp_alta_visitas_num" to ip_temp_visitas_num.text.toString(),
            "temp_baja_especificacion" to ip_temp_visitas_b_espe.text.toString(),
            "temp_baja_visitas_num" to ip_temp_visitas_b_num.text.toString(),
            "turista_nacional_ciudad_origen" to ip_ciudad_origen.text.toString(),
            "turista_nacional_llegada" to ip_llegada_mensual.text.toString(),
            "turista_nacional_total_anual" to ip_total_anual.text.toString(),
            "observaciones_llegada_turist" to ip_observaciones_llegada_turist.text.toString(),
            "ciudad_origen_extr" to ip_ciudad_origen_extr.text.toString(),
            "llegada_mensual_ex" to ip_llegada_mensual_ex.text.toString(),
            "total_anual_ex" to ip_total_anual_ex.text.toString(),
            "observaciones_llegada_turist_ex" to ip_observaciones_llegda_turist.text.toString(),
             "nombre_informate" to ip_nombre_informate.text.toString(),
            "contacto_informante" to ip_contactos.text.toString(),
            "demanda_dias_l_v"  to ip_demanda_dias_l_v.text.toString(),
            "demanda_dias_f_s" to ip_demanda_dias_f_s.text.toString(),
            "demanda_dias_fer" to ip_demanda_dias_fer.text.toString(),
            "demanda_frecuencia_visitas" to ip_frecuencia_visitas.text.toString(), //select
            "observaciones_registro" to ip_observaciones_registro.text.toString(),

            // Pantalla 11 apartado 12
            "num_per_admin" to ip_num_personas.text.toString(),
            "num_per_primaria" to ip_num_personas_nivel_primaria.text.toString(),
            "num_per_secundaria" to ip_num_personas_nivel_secundaria.text.toString(),
            "num_per_tercer_nivel" to ip_num_personas_nivel_tercern.text.toString(),
            "num_per_cuarto_nivel" to ip_num_personas_nivel_cuarton.text.toString(),
            "num_per_otro_viel" to ip_num_personas_nivel_otro.text.toString(),
            "num_personas_primeroa_aux" to ip_num_personas_primeroa_aux.text.toString(),
            "num_personas_atención_cliente" to ip_num_personas_atención_cliente.text.toString(),
            "num_personas_hospitalidad" to ip_num_personas_hospitalidad.text.toString(),
            "num_personas_guianza" to ip_num_personas_guianza.text.toString(),
            "num_personas_sensibilizacion" to ip_num_personas_sensibilizacion.text.toString(),
            "num_personas_otro" to ip_num_personas_otro.text.toString(),
            "observaciones_recurso_humano" to ip_observaciones_recurso_humano.text.toString(),
            "num_personas_aleman" to ip_num_personas_aleman.text.toString(),
            "num_personas_ingles" to ip_num_personas_ingles.text.toString(),
            "num_personas_frances" to ip_num_personas_frances.text.toString(),
            "num_personas_italiano" to ip_num_personas_italiano.text.toString(),
             "num_personas_chino" to ip_num_personas_chino.text.toString(),
            "num_personas_otro_idioma" to ip_num_personas_otro_idioma.text.toString(),
            "observaciones_idiomas" to ip_observaciones_idiomas.text.toString()



        ))

        Toast.makeText(context, "LUGAR AGREGADO", Toast.LENGTH_SHORT).show()
        dialog.dismiss()
        // funcionalidades de actividad
        (activity as AppCompatActivity).onBackPressed()
    }
    // Funcion para recuperar losd datos de un lugar turiatico
    fun isEditarLlenandoCampos(){
        (activity as AppCompatActivity).bottom_navegation_view.menu.findItem(R.id.fragmentToReturn).isVisible = true
        var adapterProv = activity?.let { ArrayAdapter<String>(it, R.layout.item_select,
            resources.getStringArray(R.array.provincias)) }
        val adapterCat = activity?.let { ArrayAdapter<String>(it, R.layout.item_select,
            resources.getStringArray(R.array.categorias)) }
        var adapterLineaProduct = activity?.let { ArrayAdapter<String>(it,R.layout.item_select,
            resources.getStringArray(R.array.linea_producto_p2))}
        var adapterEscenario = activity?.let { ArrayAdapter<String>(it,R.layout.item_select,
            resources.getStringArray(R.array.escenario_localiza_p2))
        }
        var adapterTipoIngreso = activity?.let { ArrayAdapter<String>(it, R.layout.item_select,
            resources.getStringArray(R.array.tipo_ingreso_p2))
        }
        var adapterAtencion = activity?.let { ArrayAdapter<String>(it,R.layout.item_select,
            resources.getStringArray(R.array.atencion_p2))
        }
        var adapterTipoVia = activity?.let { ArrayAdapter<String>(it, R.layout.item_select,
            resources.getStringArray(R.array.tipo_ingreso_p2))
        }
        var adapterTipoAceso = activity?.let { ArrayAdapter<String>(it, R.layout.item_select,
            resources.getStringArray(R.array.tipo_acuatico_p3))
        }
        var adapterTipoAereo = activity?.let { ArrayAdapter<String>(it, R.layout.item_select,
            resources.getStringArray(R.array.tipo_aereo_p3))
        }
        var adapterFrecuenciaTransporte = activity?.let { ArrayAdapter<String>(it,R.layout.item_select,
            resources.getStringArray(R.array.frecuencia_transporte_p3))
        }
        var adapterSenializacion = activity?.let { ArrayAdapter<String>(it, R.layout.item_select,
            resources.getStringArray(R.array.estado_senializacion))
        }

        database.collection("lugares").document(id).get().addOnSuccessListener {
            // pantalla 1
            ip_nombre_atr.setText(it.get("nombre") as String)
            ip_descripcion_atr.setText(it.get("descripcion") as String)
            ip_categoria.setText(it.get("categoria") as String)
            with(ip_categoria) { setAdapter(adapterCat)
                onItemClickListener = this@AddLugarMainFragment
            }
            ip_tipo.setText(it.get("tipo") as String)
            ip_subtipo.setText(it.get("subtipo") as String)
            ip_provincia.setText(it.get("provincia") as String)
            with(ip_provincia) { setAdapter(adapterProv)
                onItemClickListener = this@AddLugarMainFragment
            }
            ip_canton.setText(it.get("canton") as String?)
            ip_parroquias.setText(it.get("parroquia") as String?)
            ip_calle_principal.setText(it.get("calle_principal") as String?)
            ip_calle_principal_numero.setText(it.get("calle_principal_numero") as String?)
            Glide.with(contextF).load(it.get("imagen")).into(firebaseImage)
            urlImagen = it.get("imagen").toString()

            ip_calle_transversal.setText(it.get("transversal") as String?)
            ip_barrio_sector.setText(it.get("barrio_sector") as String?)
            ip_sitio_poblado.setText(it.get("poblado_cercano") as String?)
            ip_latitud.setText(it.get("latitud") as String?)
            ip_longitug.setText(it.get("ip_longitug") as String?)
            ip_altura_msnm.setText(it.get("altura") as String?)
            ip_tipo_admin.setText(it.get("tipo_administrador") as String?)
            ip_nombre_inst.setText(it.get("nombre_institución") as String?)
            ip_nombre_admin.setText(it.get("nombre_administrador") as String?)
            ip_cargo_ocupa.setText(it.get("cargo_ocupa") as String?)
            ip_telefono.setText(it.get("telefono_celular") as String?)
            ip_correo.setText(it.get("correo_electrónico") as String?)
            ip_observaciones_admin.setText(it.get("observaciones_admin") as String?)

            // pantlla 2
            clima.setText(it.get("clima") as String?)
            temperatura.setText(it.get("temperatura") as String?)
            precipitacion.setText(it.get("precipitacion") as String?)
            ip_linea_producto_p2.setText(it.get("linea_producto") as String?)
            with(ip_linea_producto_p2){ setAdapter(adapterLineaProduct)
                onItemClickListener = this@AddLugarMainFragment
            }

            ip_escenario_localiza_p2.setText(it.get("escenario_localización") as String?)
            with(ip_escenario_localiza_p2) {setAdapter(adapterEscenario)
                onItemClickListener = this@AddLugarMainFragment
            }

            ip_tipo_ingreso_p2.setText(it.get("tipo_ingreso") as String?)
            with(ip_tipo_ingreso_p2) {
                setAdapter(adapterTipoIngreso)
                onItemClickListener = this@AddLugarMainFragment
            }
            horario_ingreso_p2.setText(it.get("horario_ingreso") as String?)
            horario_salida_p2.setText(it.get("horario_salida") as String?)
            ip_atencion_p2.setText(it.get("atencion") as String?)
            with(ip_atencion_p2) { setAdapter(adapterAtencion)
                onItemClickListener = this@AddLugarMainFragment
            }
            // Formas de pago
            if(form_pago_tarjeta_debito.hint in it.get("forma_de_pago") as List<*>){
                form_pago_tarjeta_debito.isChecked = true
            }
            if(form_pago_efectivo.hint in it.get("forma_de_pago") as  List<*>){
                form_pago_efectivo.isChecked = true
            }
            if(form_pago_deposito.hint in it.get("forma_de_pago") as  List<*>){
                form_pago_deposito.isChecked = true
            }
            if(form_pago_tarjeta_credito.hint in it.get("forma_de_pago") as  List<*>){
                form_pago_tarjeta_credito.isChecked = true
            }
            if(form_pago_cheque.hint in it.get("forma_de_pago") as  List<*>){
                form_pago_cheque.isChecked = true
            }
            if(form_pago_dinero_elect.hint in it.get("forma_de_pago") as List<*>){
                form_pago_dinero_elect.isChecked = true
            }
            precio_desde.setText(it.get("precio_desde") as String?)
            precio_hasta.setText(it.get("precio_hasta") as String?)
            meses_recomendables_visita.setText(it.get("meses_recomendables_visita") as String?)
            atencion_observaciones.setText(it.get("observaciones_ingreso_atractivo") as String?)
            // Tercera pantalla
            nombre_ciudad_cercana.setText(it.get("nombre_poblado_cercano") as String?)
            distancia_desde_hasta.setText(it.get("distancia_desde_poblado_cercano") as String?)
            tiempo_desplazamiento.setText(it.get("tiempo_desplazamiento_auto") as String?)
            ip_latitud.setText(it.get("coordenada_latitud") as String?)
            ip_longitug.setText(it.get("coordenada_longitud") as String?)
            accesibilidad_observaciones.setText(it.get("observaciones_accesibilidad") as String?)
            // -----Acceso terrestre
            ip_coordenada_inicio.setText(it.get("acceso_terrestre_coordenada_inicio") as String?)
            ip_coordenada_fin.setText(it.get("acceso_terrestre_coordenada_fin") as String?)
            ip_distanciakm.setText(it.get("acceso_terrestre_distancia_km") as String?)
            ip_tipo_material.setText(it.get("acceso_terrestre_tipo_material") as String?)
            ip_estado_acceso.setText(it.get("acceso_terrestre_estado") as String?)
            ip_observaciones_acceso.setText(it.get("acceso_terrestre_observaciones") as String?)
            // ---- Acceso Acuatico

            ip_tipo_via_p3.setText(it.get("acuatico_tipo_via") as String?)
            with(ip_tipo_via_p3) {
                setAdapter(adapterTipoVia)
                onItemClickListener = this@AddLugarMainFragment
            }

            ip_tipo_acuatico_p3.setText(it.get("acuatico_tipo_acceso ") as String?)
            with(ip_tipo_acuatico_p3) { setAdapter(adapterTipoAceso)
                onItemClickListener = this@AddLugarMainFragment
            }
            ip_muelle_partida.setText(it.get("muelle_partida") as String?)
            ip_estado_muelle_partida.setText(it.get("estado_muelle_partida") as String?)
            ip_muelle_llegada.setText(it.get("muelle_llegada") as String?)
            ip_estado_muelle_llegada.setText(it.get("estado_muelle_llegada") as String?)
            ip_tipo_aereo_p3.setText(it.get("tipo_via_acceso_aereo") as String?)
            with(ip_tipo_aereo_p3) { setAdapter(adapterTipoAereo)
                onItemClickListener = this@AddLugarMainFragment
            }
            // Servicio de Transporte



            ip_observaciones_transporte.setText(it.get("observaciones_transporte") as String?)
            ip_nombre_cooperativa.setText(it.get("nombre_cooperativa") as String?)
            ip_estacion_terminal.setText(it.get("estacion_terminal") as String?)
            ip_frecuencia_transporte_p3.setText(it.get("frecuencia_transporte") as String?)
            with(ip_frecuencia_transporte_p3) { setAdapter(adapterFrecuenciaTransporte)
                onItemClickListener = this@AddLugarMainFragment
            }
            ip_transporte_detalle_traslado.setText(it.get("transporte_detalle_traslado") as String?)
            // Accesibilidad del atractivo apra discapacidad

            ip_observaciones_accesibilidad.setText(it.get("observaciones_discapacidad") as String?)

            ip_senializacion_estado_p3.setText(it.get("senializacion_estado") as String?)
            with(ip_senializacion_estado_p3) { setAdapter(adapterSenializacion)
                onItemClickListener = this@AddLugarMainFragment
            }
            ip_observaciones_senializacion.setText(it.get("observaciones_senializacion") as String?)
            // Pantalla 4

            ip_planta_atr_p4.setText(it.get("alojamiento_atr") as String?)
            establecimientos_registrados.setText(it.get("establecimientos_registrados") as String?)
            numero_habitaciones.setText(it.get("numero_habitaciones") as String?)
            numero_plazas.setText(it.get("numero_plazas") as String?)
            planta_turistica_observaciones.setText(it.get("planta_turistica_observaciones") as String?)
            ip_planta_poblado_p4.setText(it.get("alojamiento_poblado") as String?)
            est_registrados_poblado.setText(it.get("est_registrados_poblado") as String?)
            numero_habitaciones_poblado.setText(it.get("numero_habitaciones_poblado") as String?)
            numero_plazas_poblado.setText(it.get("numero_plazas_poblado") as String?)
            planta_turistica_observaciones2.setText(it.get("planta_turistica_observaciones2") as String?)
            ip_alimentos_bebidas_p4.setText(it.get("alimentos_bebidas") as String?)
            est_registrados_alimentos.setText(it.get("est_registrados_alimentos") as String?)
            numero_mesas_alimentos.setText(it.get("numero_mesas_alimentos") as String?)
            numero_plazas_alimentos.setText(it.get("numero_plazas_alimentos") as String?)
            alimentos_bebidas_p4.setText(it.get("alimentos_bebidas_observaciones") as String?)
            ip_agencia_viajes_p4.setText(it.get("agencia_viajes") as String?)
            establecimientos_registrados_agencia.setText(it.get("establecimientos_registrados_agencia") as String?)
            observaciones_agencias_viajes_p4.setText(it.get("observaciones_agencias_viajes") as String?)
            ip_categoria_facilidad_entorno.setText(it.get("categoria_facilidad_entorno") as String?)
            ip_tipo_facilidad_entorno.setText(it.get("tipo_facilidad_entorno") as String?)
            ip_estado_facilidad_entorno.setText(it.get("estado_facilidad_entorno") as String?)
            facilidad_entorno_cantidad.setText(it.get("facilidad_entorno_cantidad") as String?)
            facilidad_entorno_coordenadas.setText(it.get("facilidad_entorno_coordenadas") as String?)
            facilidad_entorno_administrador.setText(it.get("facilidad_entorno_administrador") as String?)
            cb_facilidad_accesibilidad_entorno.setText(it.get("cb_facilidad_accesibilidad_entorno") as String?)

            if(complementarios_atr_alquiler.hint in it.get("complementos_atr") as List<*>){
                complementarios_atr_alquiler.isChecked = true
            }
            if(complementarios_atr_artesanias.hint in it.get("complementos_atr") as  List<*>){
                complementarios_atr_artesanias.isChecked = true
            }
            if(complement_atr_casa_cambio.hint in it.get("complementos_atr") as List<*>){
                complement_atr_casa_cambio.isChecked = true
            }
            if(complement_atr_cajero.hint in it.get("complementos_atr") as  List<*>){
                complement_atr_cajero.isChecked = true
            }
            if(complement_atr_otro.hint in it.get("complementos_atr") as  List<*>){
                complement_atr_otro.isChecked = true
            }
            if(complementarios_pobl_alquiler.hint in it.get("complementos_pbl") as List<*>){
                complementarios_pobl_alquiler.isChecked = true
            }
            if(complementarios_pobl_artesanias.hint in it.get("complementos_pbl") as  List<*>){
                complementarios_pobl_artesanias.isChecked = true
            }
            if(complement_pobl_casa_cambio.hint in it.get("complementos_pbl") as List<*>){
                complement_pobl_casa_cambio.isChecked = true
            }
            if(complement_pobl_cajero.hint in it.get("complementos_pbl") as  List<*>){
                complement_pobl_cajero.isChecked = true
            }
            if(complement_pobl_otro.hint in it.get("complementos_pbl") as  List<*>){
                complement_pobl_otro.isChecked = true
            }
            observaciones_complement_p4.setText(it.get("observaciones_complement_atr") as String?)
            observaciones_complement_pobl_p4.setText(it.get("observaciones_complement_pobl") as String?)


                    // PANTALLA 5
            ip_conservacion_atr.setText(it.get("conservacion_atr") as String?)
            observaciones_conservacion_atr_p5.setText(it.get("observaciones_conservacion_atr") as String?)
            ip_factores_naturales_atr.setText(it.get("factores_naturales_atr") as String?)
            ip_factores_antropi_atr.setText(it.get("factores_antropi_atr") as String?)
            factores_atr_p5.setText(it.get("observaciones_factores_atr") as String?)
            ip_conservacion_entorno.setText(it.get("entorno") as String?)
            observaciones_entorno_p5.setText(it.get("observaciones_entorno") as String?)
            ip_factores_naturales_entorno.setText(it.get("factores_naturales_entorno") as String?)
            ip_factores_antropi_entorno.setText(it.get("factores_antropi_entorno") as String?)
            factores_entorno_p5.setText(it.get("observaciones_factores_entorno") as String?)
            declaratoria_declarante.setText(it.get("declaratoria_declarante") as String?)
            declaratoria_denominacion.setText(it.get("declaratoria_denominacion") as String?)
            declaratoria_fecha_creacion.setText(it.get("declaratoria_fecha_creacion") as String?)
            declaratoria_alcance.setText(it.get("declaratoria_alcance") as String?)
            observaciones_declaratoria_p5.setText(it.get("observaciones_declaratoria") as String?)
                    // Pantall 6
            /*
            if(higiene_energia.hint in it.get("servicios_basicos") as List<*>){
                higiene_energia.isChecked = true
            }
            if(higiene_desechos.hint in it.get("servicios_basicos") as  List<*>){
                higiene_desechos.isChecked = true
            }
*/
            observaciones_servicios_p6.setText(it.get("observaciones_servicios") as String?)
            observaciones_servicios_p6_2.setText(it.get("observaciones_servicios_p2") as String?)
            ip_higiene_senialetica_ambientes.setText(it.get("higiene_senialetica_ambientes") as String?)
            ip_higiene_senialetica_tipo.setText(it.get("higiene_senialetica_tipo") as String?)
            ip_higiene_materialidad.setText(it.get("higiene_materialidad") as String?)
            ip_higiene_estado.setText(it.get("higiene_estado") as String?)
            ip_higiene_salud_atr.setText(it.get("higiene_salud_atr") as String?)
            ip_observaciones_salud_atr.setText(it.get("observaciones_salud_atr") as String?)
            ip_higiene_salud_pbl.setText(it.get("higiene_salud_pbl") as String?)
            ip_observaciones_higiene.setText(it.get("observaciones_higiene") as String?)
            ip_seguridad.setText(it.get("seguridad") as String?)
            ip_observaciones_seguridad.setText(it.get("observaciones_seguridad") as String?)
            ip_comunic_telefonia_atr.setText(it.get("comunic_telefonia_atr") as String?)
            ip_comunic_internet_atr.setText(it.get("comunic_internet_atr") as String?)
            ip_comunic_radio_atr.setText(it.get("comunic_radio_atr") as String?)
            ip_observaciones_comunic_atr.setText(it.get("observaciones_comunic_atr") as String?)
            ip_comunic_telefonia_pbl.setText(it.get("comunic_telefonia_pbl") as String?)
            ip_comunic_internet_pbl.setText(it.get("comunic_internet_pbl") as String?)
            ip_comunic_radio_pbl.setText(it.get("comunic_radio_pbl") as String?)
            ip_observaciones_comunic_pbl.setText(it.get("observaciones_comunic_pbl") as String?)
            ip_multiamenazas.setText(it.get("multiamenazas") as String?)
            if((it.get("plan_contigencias") as String?) == "si"){
                cb_plan_contingencias.isChecked = true
            }else if((it.get("plan_contigencias") as String?) == "no"){
                cb_plan_contingencias.isChecked = false
            }

            ip_institucion_elaboro_doc.setText(it.get("institucion_elaboro_doc") as String?)
            ip_nom_documento.setText(it.get("nom_documento") as String?)
            ip_anio_elaboracion_doc.setText(it.get("anio_elaboracion_doc") as String?)
            ip_observaciones_multiamenazas.setText(it.get("observaciones_multiamenazas") as String?)
            // Pantalla 7
                // COndicionales
            // Pantalla 8 Checbox

            // Pantalla 10 aprartao 11
            select_si_no_2p11.setText(it.get("tiene_registros") as String?)
            select_tipo_p11.setText(it.get("tipo_material_reporte") as String?)
            ip_anios_registro_viistante.setText(it.get("anios_registro_viistante") as String?)
            select_si_no_p11.setText(it.get("reportes_estadisticos") as String?)
            ip_frecuencia_reportes.setText(it.get("frecuencia_reportes") as String?)
            ip_temp_visitas_espe.setText(it.get("temp_alta_especificacion") as String?)
            ip_temp_visitas_num.setText(it.get("temp_alta_visitas_num") as String?)
            ip_temp_visitas_b_espe.setText(it.get("temp_baja_especificacion") as String?)
            ip_temp_visitas_b_num.setText(it.get("temp_baja_visitas_num") as String?)
            ip_ciudad_origen.setText(it.get("turista_nacional_ciudad_origen") as String?)
            ip_llegada_mensual.setText(it.get("turista_nacional_llegada") as String?)
            ip_total_anual.setText(it.get("turista_nacional_total_anual") as String?)
            ip_observaciones_llegada_turist.setText(it.get("observaciones_llegada_turist") as String?)
            ip_ciudad_origen_extr.setText(it.get("ciudad_origen_extr") as String?)
            ip_llegada_mensual_ex.setText(it.get("llegada_mensual_ex") as String?)
            ip_total_anual_ex.setText(it.get("total_anual_ex") as String?)
            ip_observaciones_llegda_turist.setText(it.get("observaciones_llegada_turist_ex") as String?)
            ip_nombre_informate.setText(it.get("nombre_informate") as String?)
            ip_contactos.setText(it.get("contacto_informante") as String?)
            ip_demanda_dias_l_v.setText(it.get("demanda_dias_l_v") as String?)
            ip_demanda_dias_f_s.setText(it.get("demanda_dias_f_s") as String?)
            ip_demanda_dias_fer.setText(it.get("demanda_dias_fer") as String?)
            ip_frecuencia_visitas.setText(it.get("demanda_frecuencia_visitas") as String?)
            ip_observaciones_registro.setText(it.get("observaciones_registro") as String?)
                    
            // pantalla 11 apartado 12

            ip_num_personas.setText(it.get("num_per_admin") as String?)
            ip_num_personas_nivel_primaria.setText(it.get("num_per_primaria") as String?)
            ip_num_personas_nivel_secundaria.setText(it.get("num_per_secundaria") as String?)
            ip_num_personas_nivel_tercern.setText(it.get("num_per_tercer_nivel") as String?)
            ip_num_personas_nivel_cuarton.setText(it.get("num_per_cuarto_nivel") as String?)
            ip_num_personas_nivel_otro.setText(it.get("num_per_otro_viel") as String?)
            ip_num_personas_primeroa_aux.setText(it.get("num_personas_primeroa_aux") as String?)
            ip_num_personas_atención_cliente.setText(it.get("num_personas_atención_cliente") as String?)
            ip_num_personas_hospitalidad.setText(it.get("num_personas_hospitalidad") as String?)
            ip_num_personas_guianza.setText(it.get("num_personas_guianza") as String?)
            ip_num_personas_sensibilizacion.setText(it.get("num_personas_sensibilizacion") as String?)
            ip_num_personas_otro.setText(it.get("num_personas_otro") as String?)
            ip_observaciones_recurso_humano.setText(it.get("observaciones_recurso_humano") as String?)
            ip_num_personas_aleman.setText(it.get("num_personas_aleman") as String?)
            ip_num_personas_ingles.setText(it.get("num_personas_ingles") as String?)
            ip_num_personas_frances.setText(it.get("num_personas_frances") as String?)
            ip_num_personas_italiano.setText(it.get("num_personas_italiano") as String?)
            ip_num_personas_chino.setText(it.get("num_personas_chino") as String?)
            ip_num_personas_otro_idioma.setText(it.get("num_personas_otro_idioma") as String?)
            ip_observaciones_idiomas.setText(it.get("observaciones_idiomas") as String?)

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

    fun serviciosBasicos(){
        if(higiene_energia.isChecked){
            servicios_basicos.add((higiene_energia.hint).toString())
        }
        if(higiene_desechos.isChecked){
            servicios_basicos.add((higiene_desechos.hint).toString())
        }
        if(higiene_saneamiento.isChecked){
            servicios_basicos.add((higiene_saneamiento.hint).toString())
        }
        if(higiene_agua.isChecked){
            servicios_basicos.add((higiene_agua.hint).toString())
        }
    }
    fun complementosPbl(){
        if(complementarios_pobl_alquiler.isChecked){
            complementos_pbl.add((complementarios_pobl_alquiler.hint).toString())
        }
        if(complementarios_pobl_artesanias.isChecked){
            complementos_pbl.add((complementarios_pobl_artesanias.hint).toString())
        }
        if(complement_pobl_casa_cambio.isChecked){
            complementos_pbl.add((complement_pobl_casa_cambio.hint).toString())
        }
        if(complement_pobl_cajero.isChecked){
            complementos_pbl.add((complement_pobl_cajero.hint).toString())
        }
        if(complement_pobl_otro.isChecked){
            complementos_pbl.add((complement_pobl_otro.hint).toString())
        }
    }
    fun complementosAtr(){
        if(complementarios_atr_alquiler.isChecked){
            complementos_atr.add((complementarios_atr_alquiler.hint).toString())
        }
        if(complementarios_atr_artesanias.isChecked){
            complementos_atr.add((complementarios_atr_artesanias.hint).toString())
        }
        if(complement_atr_casa_cambio.isChecked){
            complementos_atr.add((complement_atr_casa_cambio.hint).toString())
        }
        if(complement_atr_cajero.isChecked){
            complementos_atr.add((complement_atr_cajero.hint).toString())
        }
        if(complement_atr_otro.isChecked){
            complementos_atr.add((complement_atr_otro.hint).toString())
        }
    }
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
        if(atencion_sistema_reservas.isChecked){
            formaPago_p2.add((atencion_sistema_reservas.hint).toString())
        }



    }
    fun servicioTransporte(){
        if(transporte_bus.isChecked){
            transporte_p3.add((transporte_bus.hint).toString())
        }
        if(transporte_buseta.isChecked){
            transporte_p3.add((transporte_buseta.hint).toString())
        }
        if(transporte_lancha.isChecked){
            transporte_p3.add((transporte_lancha.hint).toString())
        }
        if(transporte_bote.isChecked){
            transporte_p3.add((transporte_bote.hint).toString())
        }
        if(transporte_barco.isChecked){
            transporte_p3.add((transporte_barco.hint).toString())
        }
        if(transporte_mototaxi.isChecked){
            transporte_p3.add((transporte_mototaxi.hint).toString())
        }
        if(transporte_canoa.isChecked){
            transporte_p3.add((transporte_canoa.hint).toString())
        }
        if(transporte_avioneta.isChecked){
            transporte_p3.add((transporte_avioneta.hint).toString())
        }
        if(transporte_teleferico.isChecked){
            transporte_p3.add((transporte_teleferico.hint).toString())
        }
        if(transporte_helicoptero.isChecked){
            transporte_p3.add((transporte_helicoptero.hint).toString())
        }
        if(transporte_otro.isChecked){
            transporte_p3.add((transporte_otro.hint).toString())
        }
        if(transporte_taxi.isChecked){
            transporte_p3.add((transporte_taxi.hint).toString())
        }
        if(transporte_avion.isChecked){
            transporte_p3.add((transporte_avion.hint).toString())
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
            linearV6p6.toggleVisibility()
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

            btn_numpantallasp1.setText("2 DE 11")
        }else if(contadorPantallas ==2){
            val paramsScroll1 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte1.layoutParams= paramsScroll1
            val paramsScroll2 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte2.layoutParams= paramsScroll2
            val paramsScroll3 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,0f )
            scrollparte3.layoutParams= paramsScroll3


            btn_numpantallasp1.setText("3 DE 11")
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

            btn_numpantallasp1.setText("4 DE 11")
        }
        else if(contadorPantallas ==4){
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

            btn_numpantallasp1.setText("5 DE 11")
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
            val paramsScroll5 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte5.layoutParams= paramsScroll5
            val paramsScroll6 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,0f )
            scrollparte6.layoutParams= paramsScroll6

            btn_numpantallasp1.setText("6 DE 11")
        }
        else if(contadorPantallas ==6){
            val paramsScroll1 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte1.layoutParams= paramsScroll1
            val paramsScroll2 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte2.layoutParams= paramsScroll2
            val paramsScroll3 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte3.layoutParams= paramsScroll3
            val paramsScroll4 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte4.layoutParams= paramsScroll4
            val paramsScroll5 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte5.layoutParams= paramsScroll5
            val paramsScroll6 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte6.layoutParams= paramsScroll6
            val paramsScroll7 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,0f )
            scrollparte7.layoutParams= paramsScroll7

            btn_numpantallasp1.setText("7 DE 11")
        }else if(contadorPantallas ==7){
            val paramsScroll1 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte1.layoutParams= paramsScroll1
            val paramsScroll2 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte2.layoutParams= paramsScroll2
            val paramsScroll3 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte3.layoutParams= paramsScroll3
            val paramsScroll4 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte4.layoutParams= paramsScroll4
            val paramsScroll5 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte5.layoutParams= paramsScroll5
            val paramsScroll6 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte6.layoutParams= paramsScroll6
            val paramsScroll7 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte7.layoutParams= paramsScroll7
            val paramsScroll8 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,0f )
            scrollparte8.layoutParams= paramsScroll8

            btn_numpantallasp1.setText("8 DE 11")
        }else if(contadorPantallas ==8){
            val paramsScroll1 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte1.layoutParams= paramsScroll1
            val paramsScroll2 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte2.layoutParams= paramsScroll2
            val paramsScroll3 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte3.layoutParams= paramsScroll3
            val paramsScroll4 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte4.layoutParams= paramsScroll4
            val paramsScroll5 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte5.layoutParams= paramsScroll5
            val paramsScroll6 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte6.layoutParams= paramsScroll6
            val paramsScroll7 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte7.layoutParams= paramsScroll7
            val paramsScroll8 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte8.layoutParams= paramsScroll8
            val paramsScroll9 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,0f )
            scrollparte9.layoutParams= paramsScroll9
            btn_numpantallasp1.setText("9 DE 11")
        }else if(contadorPantallas ==9){
            val paramsScroll1 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte1.layoutParams= paramsScroll1
            val paramsScroll2 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte2.layoutParams= paramsScroll2
            val paramsScroll3 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte3.layoutParams= paramsScroll3
            val paramsScroll4 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte4.layoutParams= paramsScroll4
            val paramsScroll5 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte5.layoutParams= paramsScroll5
            val paramsScroll6 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte6.layoutParams= paramsScroll6
            val paramsScroll7 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte7.layoutParams= paramsScroll7
            val paramsScroll8 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte8.layoutParams= paramsScroll8
            val paramsScroll9 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte9.layoutParams= paramsScroll9
            val paramsScroll10 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,0f )
            scrollparte10.layoutParams= paramsScroll10
            btn_numpantallasp1.setText("10 DE 11")
        }else if(contadorPantallas == 10){
            val paramsScroll1 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte1.layoutParams= paramsScroll1
            val paramsScroll2 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte2.layoutParams= paramsScroll2
            val paramsScroll3 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte3.layoutParams= paramsScroll3
            val paramsScroll4 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte4.layoutParams= paramsScroll4
            val paramsScroll5 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte5.layoutParams= paramsScroll5
            val paramsScroll6 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte6.layoutParams= paramsScroll6
            val paramsScroll7 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte7.layoutParams= paramsScroll7
            val paramsScroll8 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte8.layoutParams= paramsScroll8
            val paramsScroll9 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte9.layoutParams= paramsScroll9
            val paramsScroll10 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,1f )
            scrollparte10.layoutParams= paramsScroll10
            val paramsScroll11 = LinearLayout.LayoutParams( MATCH_PARENT, MATCH_PARENT,0f )
            scrollparte11.layoutParams= paramsScroll11
            btn_numpantallasp1.setText("11 DE 11")
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

