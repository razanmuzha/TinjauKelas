package com.example.tinjaukelas

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class KelasRepository(context: Context) {

    private val db = DatabaseHelper.getInstance(context).writableDatabase

    private fun cursorToKelas(cursor: Cursor) = Kelas(
        id           = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
        namaKelas    = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_KELAS_NAMA)),
        spreadsheetUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_KELAS_URL))
    )

    fun insertKelas(kelas: Kelas): Long {
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_KELAS_NAMA, kelas.namaKelas)
            put(DatabaseHelper.COL_KELAS_URL, kelas.spreadsheetUrl)
        }
        return db.insert(DatabaseHelper.TABLE_KELAS, null, values)
    }

    fun getAllKelas(): List<Kelas> {
        val list = mutableListOf<Kelas>()
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_KELAS}", null)
        if (cursor.moveToFirst()) {
            do { list.add(cursorToKelas(cursor)) } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getKelasByGuru(guruId: Int): List<Kelas> {
        val list = mutableListOf<Kelas>()
        val cursor = db.rawQuery("""
            SELECT k.* FROM ${DatabaseHelper.TABLE_KELAS} k
            INNER JOIN ${DatabaseHelper.TABLE_GURU_KELAS} gk
                ON k.${DatabaseHelper.COL_ID} = gk.${DatabaseHelper.COL_KELAS_ID}
            WHERE gk.${DatabaseHelper.COL_GURU_ID} = ?
        """.trimIndent(), arrayOf(guruId.toString()))
        if (cursor.moveToFirst()) {
            do { list.add(cursorToKelas(cursor)) } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun assignGuruToKelas(guruId: Int, kelasId: Int) {
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_GURU_ID, guruId)
            put(DatabaseHelper.COL_KELAS_ID, kelasId)
        }
        db.insertWithOnConflict(
            DatabaseHelper.TABLE_GURU_KELAS,
            null,
            values,
            android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
        )
    }

    fun deleteKelas(kelasId: Int) {
        db.delete(DatabaseHelper.TABLE_GURU_KELAS, "${DatabaseHelper.COL_KELAS_ID} = ?", arrayOf(kelasId.toString()))
        db.delete(DatabaseHelper.TABLE_KELAS, "${DatabaseHelper.COL_ID} = ?", arrayOf(kelasId.toString()))
    }
}