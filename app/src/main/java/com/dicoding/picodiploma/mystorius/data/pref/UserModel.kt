package com.dicoding.picodiploma.mystorius.data.pref

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)