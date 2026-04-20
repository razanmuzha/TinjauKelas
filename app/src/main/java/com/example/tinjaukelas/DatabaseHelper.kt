package com.example.tinjaukelas

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "Kelas.db"
        const val DATABASE_VERSION = 5

        // rooms
        const val TABLE_ROOM = "rooms"
        const val COL_STATUS = "Status"

        // users
        const val TABLE_USER = "users"
        const val COL_USER_EMAIL = "email"
        const val COL_USER_PASSWORD = "password"
        const val COL_USER_ROLE = "role"

        // kelas
        const val TABLE_KELAS = "kelas"
        const val COL_KELAS_NAMA = "nama_kelas"
        const val COL_KELAS_URL = "spreadsheet_url"

        // guru_kelas
        const val TABLE_GURU_KELAS = "guru_kelas"
        const val COL_GURU_ID = "guru_id"
        const val COL_KELAS_ID = "kelas_id"

        // shared
        const val COL_ID = "id"
        const val COL_KELAS = "Kelas"
        const val COL_USER_ID = "user_id"

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
        db.execSQL("""
            CREATE TABLE $TABLE_ROOM (
                $COL_ID      INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_KELAS   TEXT NOT NULL,
                $COL_STATUS  INTEGER NOT NULL DEFAULT 0,
                $COL_USER_ID INTEGER NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_USER (
                $COL_ID            INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_EMAIL    TEXT NOT NULL UNIQUE,
                $COL_USER_PASSWORD TEXT NOT NULL,
                $COL_USER_ROLE     TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_KELAS (
                $COL_ID        INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_KELAS_NAMA TEXT NOT NULL,
                $COL_KELAS_URL  TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_GURU_KELAS (
                $COL_GURU_ID  INTEGER NOT NULL,
                $COL_KELAS_ID INTEGER NOT NULL,
                PRIMARY KEY ($COL_GURU_ID, $COL_KELAS_ID),
                FOREIGN KEY ($COL_GURU_ID)  REFERENCES $TABLE_USER($COL_ID),
                FOREIGN KEY ($COL_KELAS_ID) REFERENCES $TABLE_KELAS($COL_ID)
            )
        """.trimIndent())

        // Seed users
        db.execSQL("INSERT INTO $TABLE_USER ($COL_USER_EMAIL, $COL_USER_PASSWORD, $COL_USER_ROLE) VALUES ('admin@gmail.com', 'admin123', 'admin')")
        db.execSQL("INSERT INTO $TABLE_USER ($COL_USER_EMAIL, $COL_USER_PASSWORD, $COL_USER_ROLE) VALUES ('siswa@gmail.com', 'siswa123', 'siswa')")
        db.execSQL("INSERT INTO $TABLE_USER ($COL_USER_EMAIL, $COL_USER_PASSWORD, $COL_USER_ROLE) VALUES ('guru@gmail.com', 'guru123', 'guru')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GURU_KELAS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_KELAS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ROOM")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }
}