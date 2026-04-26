package com.example.tinjaukelas

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class RoomRepository(context: Context) {

    private val db = DatabaseHelper.getInstance(context).writableDatabase

    private fun cursorToRoom(cursor: Cursor) = Room(
        id        = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
        Kelas  = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_KELAS)),
        Status = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STATUS)) == 1,
        userId    = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID)),
        Kapasitas = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_KAPASITAS))
    )


    fun insertRoom(room: Room): Long {
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_KELAS,room.Kelas)
            put(DatabaseHelper.COL_STATUS, if (room.Status) 1 else 0)
            put(DatabaseHelper.COL_USER_ID,room.userId)
            put(DatabaseHelper.COL_KAPASITAS,room.Kapasitas)
        }
        return db.insert(DatabaseHelper.TABLE_ROOM, null, values)
    }


    fun getAllRooms(): List<Room> {
        val rooms = mutableListOf<Room>()
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_ROOM}", null)
        if (cursor.moveToFirst()) {
            do { rooms.add(cursorToRoom(cursor)) } while (cursor.moveToNext())
        }

        cursor.close()
        return rooms
    }



    fun getActiveRoomByUser(userId: Int): Room? {
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_ROOM} WHERE ${DatabaseHelper.COL_USER_ID} = ? AND ${DatabaseHelper.COL_STATUS} = 1",
            arrayOf(userId.toString())
        )
        val room = if (cursor.moveToFirst()) cursorToRoom(cursor) else null
        cursor.close()
        return room
    }


    fun updateRoomStatusAndUser(roomId: Int, newStatus: Boolean, userId: Int): Int {
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_STATUS, if (newStatus) 1 else 0)
            put(DatabaseHelper.COL_USER_ID, if (newStatus) userId else 0) // 0 = tidak ada yang pakai
        }
        return db.update(
            DatabaseHelper.TABLE_ROOM,
            values,
            "${DatabaseHelper.COL_ID} = ?",
            arrayOf(roomId.toString())
        )
    }

        fun deleteRoom(roomId: Int): Int {
            return db.delete(
                DatabaseHelper.TABLE_ROOM,
                "${DatabaseHelper.COL_ID} = ?",
                arrayOf(roomId.toString())
            )
        }
    }