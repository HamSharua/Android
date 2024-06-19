package com.example.challengeme.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ChallengeDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${ChallengeContract.PostEntry.TABLE_NAME} (" +
                    "${ChallengeContract.PostEntry.COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${ChallengeContract.PostEntry.COLUMN_NAME_USER} TEXT," +
                    "${ChallengeContract.PostEntry.COLUMN_NAME_COMMENT} TEXT," +
                    "${ChallengeContract.PostEntry.COLUMN_NAME_DATE} TEXT)"

        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${ChallengeContract.PostEntry.TABLE_NAME}")
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Challenge.db"
    }
}
