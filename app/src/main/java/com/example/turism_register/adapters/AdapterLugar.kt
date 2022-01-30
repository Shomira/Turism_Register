package com.example.turism_register.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.turism_register.R
import com.example.turism_register.datas_class.Lugar
import kotlinx.android.synthetic.main.list_item_lugar.view.*
import java.lang.IllegalArgumentException

class AdapterLugar(
    private val lugarList: ArrayList<Lugar>,
    private val activated: View,
    private val itemClickListener:OnLugarClickListener
    ) : RecyclerView.Adapter<AdapterLugar.MyViewHolder>() {

    interface OnLugarClickListener {
        fun onItemClickView(id: String?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterLugar.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_lugar, parent, false)

        return MyViewHolder(itemView)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onBindViewHolder(holder: AdapterLugar.MyViewHolder, position: Int) {
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
            itemView.setOnClickListener {
                itemClickListener.onItemClickView(item.id)
            }
            Glide.with(activated).load(item.imagen).into(itemView.imgv_imageLugar)
            itemView.tv_nombreLugar.text = item.nombre
            itemView.tv_ubicacionLugar.text = item.provincia + ", " + item.canton + ", " + item.parroquia
        }
    }
}