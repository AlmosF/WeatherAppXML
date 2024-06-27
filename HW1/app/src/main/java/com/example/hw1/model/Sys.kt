package com.example.hw1.model

data class Sys(
    val type: Int = 0,
    val id: Long = 0,
    val country: String? = null,
    val sunrise: Long = 0,
    val sunset: Long = 0
)