package com.gazi_market

import java.sql.Timestamp

data class PostData(
    val nickname: String,
    val title: String,
    val content: String,
    val price: Int,
    val isSoldOut: Boolean,
    val createdAt: Timestamp
)