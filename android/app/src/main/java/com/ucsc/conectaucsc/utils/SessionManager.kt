package com.ucsc.conectaucsc.utils

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("conectaucsc_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_TOKEN = "auth_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_NAME = "user_name"
    }

    fun saveSession(token: String, userId: Int, userName: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, userName)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    fun isLoggedIn(): Boolean = getToken() != null

    fun clearSession() = prefs.edit().clear().apply()
}