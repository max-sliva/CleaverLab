package com.example

data class User(
    val id: Long,
    val fio: String,
    val email: String,
    val login: String,
    val pass: String,
    val status: String,
    val online: Boolean,
    val devices: ArrayList<Object>
)