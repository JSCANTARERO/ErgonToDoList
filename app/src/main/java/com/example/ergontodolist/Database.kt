package com.example.ergontodolist

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast

class Database {
    //Nombre de la base de datos
    var dbName = "ErgonNotes"
    //Nombre de la tabla
    var dbTable = "Notes"
    //Columnas
    var colID = "ID"
    var colTitle = "Title"
    var colDesc = "Description"
    //Version de la base de datos
    var dbVersion = 1

    //Ahora, si la base de datos no existe le diremos de la cree de la siguiente manera:
    //CREATE TABLE IF NOT EXISTS ErgonNotes (ID INTEGER PRIMARY KEY, Title TEXT, Description TEXT);
    val sqlCreateTable =
        "CREATE TABLE IF NOT EXISTS $dbTable ($colID INTEGER PRIMARY KEY,$colTitle TEXT, $colDesc TEXT);"

    var sqlDB: SQLiteDatabase?=null

    constructor(context: Context){
        var db = DatabaseHelperNotes(context)
        sqlDB = db.writableDatabase
    }

    inner class DatabaseHelperNotes:SQLiteOpenHelper{
        var context: Context?=null
        constructor(context: Context): super(context,dbName, null, dbVersion){
            this.context = context
        }
        //Implementamos los metodos onCreate y onUpdate
        /*El onCreate es llamado por primera vez cuando la creacion de tablas es requerida.
        * En este caso se necesita un override en este metodo donde se escribe el script para la creacion de la tabla
        * la cua es ejecutada por SQLiteDatabase.
        * El metodo execSQL no sera llamado en adelante despues de ejecutarse e la primera implementacion.*/
        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(sqlCreateTable)
            Toast.makeText(this.context, "database created", Toast.LENGTH_SHORT).show()
        }
        /*El metodo onUpgrade es llamado cuando la version de la base de datos es actualizada.
        * Esto quiere decir que, la primera vez que la base de datos se ejecuta, la version es la 1, y en la segunda
        * si hay cambios en la estructura de la base de datos, como agregar una colmna extra en la tabla, la version de la
        * base de datos sera 2.*/
        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("Drop table if exists" + dbTable)
        }

    }

    fun insert(values:ContentValues):Long{
        val ID = sqlDB!!.insert(dbTable,"", values)
        return ID
    }
    fun Query(projection:Array<String>, selection:String, selectionArgs:Array<String>, sorOrder:String): Cursor{
        val qb = SQLiteQueryBuilder();
        qb.tables = dbTable
        val cursor = qb.query(sqlDB, projection, selection, selectionArgs, null, null, sorOrder)
        return cursor
    }

    fun delete(selection: String, selectionArgs: Array<String>):Int{
        val count = sqlDB!!.delete(dbTable, selection, selectionArgs)
        return count
    }

    fun update(values: ContentValues, selection: String, selectionArgs: Array<String>):Int{
        val count = sqlDB!!.update(dbTable, values, selection, selectionArgs)
        return count
    }
}