package com.example.turism_register.datas_class

data class Lugar(
    var id:String ?= null,
    var imagen:String ?= null,
    var nombre: String ?= null,
    var provincia:String ?= null,
    var canton:String ?= null,
    var parroquia:String ?= null,
    var descripcion:String ?= null,
    var campos_llenos:String ?= null,
    var campos_total:String ?= null

)
