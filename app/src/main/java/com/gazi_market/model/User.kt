package com.gazi_market.model

import java.io.Serializable

data class User(
    val name:String?="",
    val uid:String?="",
    val email:String?="",
    val birth : String = ""):Serializable {
}
