package com.example.turism_register.adapters

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.turism_register.R
import com.example.turism_register.datas_class.Lugar
import kotlinx.android.synthetic.main.list_item_modificar_lugar_view.view.*
import java.lang.IllegalArgumentException

class AdapterModificarLugar(
    private val lugarList: ArrayList<Lugar>,
    private val activated: View,
    private val itemClickListener: AdapterModificarLugar.OnLugarModificarClickListener
) : RecyclerView.Adapter<AdapterModificarLugar.MyViewHolder>() {

    interface OnLugarModificarClickListener {
        fun onItemClickModificar(id: String?)

        fun onItemCLickEliminar(id: String?, nombre: String?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterModificarLugar.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_modificar_lugar_view, parent, false)

        return MyViewHolder(itemView)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onBindViewHolder(holder: AdapterModificarLugar.MyViewHolder, position: Int) {
        when (holder) {
            is MyViewHolder -> holder.bind(lugarList[position], position)
            else -> throw IllegalArgumentException("Se olvido de pasar el viewhold en el bind")
        }
    }

    override fun getItemCount(): Int {
        return lugarList.size
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(item: Lugar, position: Int) {
            Glide.with(activated).load(item.imagen).into(itemView.photo_modificar_lugar_item)
            itemView.tv_item_modificar_lugar.text = item.nombre
            itemView.btn_item_edit_lugar.setOnClickListener {
                itemClickListener.onItemClickModificar(item.id)
            }
            itemView.btn_item_delete_lugar.setOnClickListener {
                itemClickListener.onItemCLickEliminar(item.id, item.nombre)
            }
            itemView.item_total_completado_modificar.text = item.campos_llenos + "/" + item.campos_total
            itemView.item_porcentaje_total_llemando_modificar.text =
                "%.1f".format(((item.campos_llenos?.toFloat()!! * 100) / item.campos_total?.toFloat()!!)) + "%"
            itemView.item_progres_bar.max = item.campos_total?.toInt()!!
            ObjectAnimator.ofInt(itemView.item_progres_bar, "progress",
                item.campos_llenos?.toInt()!!
            ).setDuration(2000).start()
        }
    }
}