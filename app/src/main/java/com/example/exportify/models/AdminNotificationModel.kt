package com.example.exportify.models

data class AdminNotificationModel(
    var topic : String ?= null,
    var type : String ?= null,
    var description : String ?= null,
    var id : String ?= null,
    var uid : String ?= null,
    var pdfUrl : String ?= null
)
