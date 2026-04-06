package com.example.tinjaukelas

import android.content.Context

class UserRepository(context: Context) {

    private val db = DatabaseHelper.getInstance(context).writableDatabase

    fun login(email: String, password: String): User? {
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_USER} WHERE ${DatabaseHelper.COL_USER_EMAIL} = ? AND ${DatabaseHelper.COL_USER_PASSWORD} = ?",
            arrayOf(email, password)
        )
        val user = if (cursor.moveToFirst()) {
            User(
                id       = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
                email    = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PASSWORD))
            )
        } else null
        cursor.close()
        return user
    }
}