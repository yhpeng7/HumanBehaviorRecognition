package com.xupt.utils

import android.content.Context
import android.content.SharedPreferences
import com.xupt.app.BaseApplication
import com.xupt.constant.Constant
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class Preference{
    companion object {
        private val prefs: SharedPreferences by lazy {
            BaseApplication.context.getSharedPreferences(Constant.SHARE_PREFERENCES_FILENAME, Context.MODE_PRIVATE)
        }

        fun clearPreference() {
            prefs.edit().clear().apply()
        }

        fun clearPreference(key: String) {
            prefs.edit().remove(key).apply()
        }

        fun contains(key: String): Boolean {
            return prefs.contains(key)
        }

        fun getAll(): Map<String, *> {
            return prefs.all
        }

        @Suppress("UNCHECKED_CAST")
        fun <T>getSharedPreferences(name: String, default: T): T = with(prefs) {
            val res: Any = when (default) {
                is Long -> getLong(name, default)
                is String -> getString(name, default)!!
                is Int -> getInt(name, default)
                is Boolean -> getBoolean(name, default)
                is Float -> getFloat(name, default)
                else -> deSerialization(getString(name, serialize(default)))
            }
            return res as T
        }

        fun <T>putSharedPreferences(name: String, value: T) = with(prefs.edit()) {
            when (value) {
                is Long -> putLong(name, value)
                is String -> putString(name, value)
                is Int -> putInt(name, value)
                is Boolean -> putBoolean(name, value)
                is Float -> putFloat(name, value)
                else -> putString(name, serialize(value))
            }.apply()
        }

        private fun <T>serialize(value: T): String? {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
            objectOutputStream.writeObject(value)
            var serStr = byteArrayOutputStream.toString("ISO-8859-1")
            serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
            objectOutputStream.close()
            byteArrayOutputStream.close()
            return serStr
        }

        private fun <T>deSerialization(string: String?): T {
            val redStr = java.net.URLDecoder.decode(string, "UTF-8")
            val byteArrayInputStream = ByteArrayInputStream(redStr.toByteArray(charset("ISO-8859-1")))
            val objectInputStream = ObjectInputStream(
                    byteArrayInputStream)
            val obj = objectInputStream.readObject() as T
            objectInputStream.close()
            byteArrayInputStream.close()
            return obj
        }
    }
}