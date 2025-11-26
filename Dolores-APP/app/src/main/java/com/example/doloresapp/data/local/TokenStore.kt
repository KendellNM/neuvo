package com.example.doloresapp.data.local

import android.content.Context
import android.content.SharedPreferences

object TokenStore {

    private const val PREFS = "auth_prefs"

    private const val KEY_TOKEN = "jwt"

    private var prefs: SharedPreferences? = null

    //Inicializar
    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        }
    }


    //Guardamos
    fun saveToken(token: String) {
        prefs?.edit()?.putString(KEY_TOKEN, token)?.apply()
    }

    //Obtenemos
    fun getToken(): String? = prefs?.getString(KEY_TOKEN, null)

    //Adios token
    fun clear() {
        prefs?.edit()?.remove(KEY_TOKEN)?.apply()
    }
}
