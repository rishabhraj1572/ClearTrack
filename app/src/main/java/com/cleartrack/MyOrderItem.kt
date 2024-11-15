package com.cleartrack

data class MyOrderItem(
    val orderId: String,
    val status: String,
    val time : Long,
    val qr_url : String
)

