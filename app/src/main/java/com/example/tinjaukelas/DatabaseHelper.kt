package com.example.tinjaukelas

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "Kelas.db"
        const val DATABASE_VERSION = 4
        const val TABLE_ROOM = "rooms"
        const val COL_ID = "id"
        const val COL_KELAS = "Kelas"
        const val COL_STATUS = "Status"
        const val COL_USER_ID = "user_id"

        const val TABLE_USER = "users"
        const val COL_USER_EMAIL = "email"
        const val COL_USER_PASSWORD = "password"


        @Volatile
        private var INSTANCE: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseHelper(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_ROOM (
                $COL_ID         INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_KELAS  TEXT NOT NULL,
                $COL_STATUS INTEGER NOT NULL DEFAULT 0,
                $COL_USER_ID    INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)

        db.execSQL("""
            CREATE TABLE $TABLE_USER (
                $COL_ID            INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_EMAIL    TEXT NOT NULL UNIQUE,
                $COL_USER_PASSWORD TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("INSERT INTO $TABLE_USER ($COL_USER_EMAIL, $COL_USER_PASSWORD) VALUES ('admin@gmail.com', 'admin123')")

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ROOM")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }
}