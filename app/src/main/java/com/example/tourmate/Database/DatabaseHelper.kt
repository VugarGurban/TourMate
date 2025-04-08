package com.example.tourmate.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tourmate.Database.AppDatabase.Companion.DATABASE_NAME

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE IF NOT EXISTS \"cities\" (\n" +
                "\t\"id\"\tINTEGER NOT NULL,\n" +
                "\t\"name\"\tTEXT NOT NULL,\n" +
                "\t\"id\"PRIMARY KEY AUTOINCREMENT\n" +
                ");")

        db!!.execSQL("CREATE TABLE IF NOT EXISTS \"places\" (\n" +
                "\t\"id\"\tINTEGER NOT NULL,\n" +
                "\t\"city_id\"\tINTEGER NOT NULL,\n" +
                "\t\"category_id\"\tINTEGER NOT NULL,\n" +
                "\t\"name\"\tTEXT NOT NULL,\n" +
                "\t\"address\"\tTEXT NOT NULL,\n" +
                "\t\"rate\"\tREAL NOT NULL,\n" +
                "\t\"description\"\tTEXT NOT NULL,\n" +
                "\t\"link\"\tTEXT NOT NULL,\n" +
                "\t\"image_path\"\tTEXT NOT NULL,\n" +
                "\t\"is_liked\"\tINTEGER NOT NULL,\n" +
                "\t\"id\"PRIMARY KEY AUTOINCREMENT\n" +
                ");")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS cities")
        db!!.execSQL("DROP TABLE IF EXISTS places")
        onCreate(db)
    }
}