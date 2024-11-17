package com.cleartrack

data class UpdateItem(
    var documentId: String,
    var location: String,
    var logistics: String,
    var pincode: String,
    var status : String,
    var text4: String,
    var time: Long,
    var phone : String,
    var email : String
)

