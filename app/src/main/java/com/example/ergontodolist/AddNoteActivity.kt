package com.example.ergontodolist

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_note.*

class AddNoteActivity : AppCompatActivity() {
    val dbTable = "Notes"
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        try {
            val bundle:Bundle = intent.extras!!
            id = bundle.getInt("ID", 0)
            if(id != 0){
                titleNote.setText(bundle.getString("name"))
                descNote.setText(bundle.getString("desc"))
            }
        }catch (ex:Exception){}
    }

    fun addFun(view: View){
        var database = Database(this)
        var values = ContentValues()
        values.put("Title", titleNote.text.toString())
        values.put("Description", descNote.text.toString())

        if(id == 0){
            val ID = database.insert(values)
            if(ID > 0){
                Toast.makeText(this, "Note is added", Toast.LENGTH_SHORT).show()
                finish()
            }
            else{
                Toast.makeText(this, "There was an error adding the note", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            var selectionArgs = arrayOf(id.toString())
            val ID = database.update(values, "ID=?", selectionArgs)
            if(ID>0){
                Toast.makeText(this, "Note is added", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "There was an error adding the note", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
