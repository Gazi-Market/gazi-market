package com.gazi_market.model

import java.io.Serializable
data class ChatRoom(
    val users: Map<String, Boolean>? = HashMap(),
    var messages: Map<String,Message>? = HashMap(),
    var postId : String = ""
) : Serializable {
}