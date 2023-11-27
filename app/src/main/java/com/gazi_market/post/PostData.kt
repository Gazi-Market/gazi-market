package com.gazi_market.post

import java.sql.Timestamp
import java.util.Date

data class PostData(
    val documentId: String,
    val content: String,
    val createdAt: Date,
    val image: String,
    val soldOut: Boolean,
    val uid: String,
    val price: Int,
    val title: String
) {
    constructor() : this("","", Timestamp(Date().time), "", false, "", 0, "")
}