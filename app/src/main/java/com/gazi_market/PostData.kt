package com.gazi_market

import java.sql.Timestamp
import java.util.Date

data class PostData(
    val documentId: String,
    val content: String,
    val createdAt: Date,
    val image: String,
    val isSoldOut: Boolean,
    val nickname: String,
    val price: Int,
    val title: String
) {
    constructor() : this("","", Timestamp(Date().time), "", false, "", 0, "")
}