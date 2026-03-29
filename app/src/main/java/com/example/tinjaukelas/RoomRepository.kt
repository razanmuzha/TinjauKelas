package com.example.tinjaukelas

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class RoomRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // Helper: converts a cursor row into a Room object
    private fun cursorToRoom(cursor: Cursor) = Room(
        id        = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
        Kelas  = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_KELAS)),
        Status = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STATUS)) == 1,
        userId    = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID))
    )


    fun insertRoom(room: Room): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_KELAS,room.Kelas)
            put(DatabaseHelper.COL_STATUS, if (room.Status) 1 else 0)
            put(DatabaseHelper.COL_USER_ID,room.userId)
        }
        val id = db.insert(DatabaseHelper.TABLE_ROOM, null, values)
        db.close()
        return id
    }


    fun getAllRooms(): List<Room> {
        val rooms = mutableListOf<Room>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_ROOM}", null)

        if (cursor.moveToFirst()) {
            do { rooms.add(cursorToRoom(cursor)) } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return rooms
    }


    fun getRoomsByStatus(status: String): List<Room> {
        val rooms = mutableListOf<Room>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_ROOM} WHERE ${DatabaseHelper.COL_KELAS} = ?",
            arrayOf(status)
        )

        if (cursor.moveToFirst()) {
            do { rooms.add(cursorToRoom(cursor)) } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return rooms
    }


    fun getRoomsByUser(userId: Int): List<Room> {
        val rooms = mutableListOf<Room>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_ROOM} WHERE ${DatabaseHelper.COL_USER_ID} = ?",
            arrayOf(userId.toString())
        )

        if (cursor.moveToFirst()) {
            do { rooms.add(cursorToRoom(cursor)) } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return rooms
    }

    fun updateRoom(room: Room): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_KELAS,  room.Kelas)
            put(DatabaseHelper.COL_STATUS, if (room.Status) 1 else 0)
            put(DatabaseHelper.COL_USER_ID,    room.userId)
        }
        val rowsAffected = db.update(
            DatabaseHelper.TABLE_ROOM,
            values,
            "${DatabaseHelper.COL_ID} = ?",
            arrayOf(room.id.toString())
        )
        db.close()
        return rowsAffected
    }

    fun updateRoomStatus(roomId: Int, newStatus: Boolean): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_STATUS, if (newStatus) 1 else 0)
        }
        val rowsAffected = db.update(
            DatabaseHelper.TABLE_ROOM,
            values,
            "${DatabaseHelper.COL_ID} = ?",
            arrayOf(roomId.toString())
        )
        db.close()
        return rowsAffected
    }


    fun deleteRoom(roomId: Int): Int {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete(
            DatabaseHelper.TABLE_ROOM,
            "${DatabaseHelper.COL_ID} = ?",
            arrayOf(roomId.toString())
        )
        db.close()
        return rowsDeleted
    }
}
