package com.example.ergontodolist

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row.view.*

class MainActivity : AppCompatActivity() {

    private var listsNotes = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Cargar de la base de datos
        loadQuery("%")
    }

    override fun onResume() {
        super.onResume()
        loadQuery("%")

    }

    private fun loadQuery(title: String) {
        val database = Database(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)
        val cursor = database.Query(projections, "Title likes?", selectionArgs, "Title")
        listsNotes.clear()
        if(cursor.moveToFirst()){
            do{
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))

                listsNotes.add(Note(ID, Title, Description))
            }while(cursor.moveToNext())
        }

        //Adapter
        val ergonNotesAdapter = ErgonNotesAdapter(this, listsNotes)
        //setear el adapter
        noteLV.adapter = ergonNotesAdapter

        //Obtener el numero total de tareas del ListView
        val total = noteLV.count
        //actionbar
        val mActionBar = supportActionBar
        if(mActionBar != null){
            mActionBar.subtitle = "You have${total}note(s) in your list"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        //searchView
        val sv = menu!!.findItem(R.id.app_bar_search).actionView as SearchView
        //Se borra el import android.widget.SearchView ya que no sera el que se utilice e importamos
        //import androidx.appcompat.widget.SearchView

        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            //Generamos los metodos onQueryTextSubmit y onQueryTextChange
            override fun onQueryTextSubmit(query: String?): Boolean {
                loadQuery("%$query%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                loadQuery("%$newText%")
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.addNote->{
                startActivity(Intent(this, AddNoteActivity::class.java))
            }
            R.id.action_settings->{
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Suppress("DEPRECATION")
    inner class ErgonNotesAdapter(context: Context, var listNotesAdapter: ArrayList<Note>) :
        BaseAdapter() {

        var context:Context?= context


        @SuppressLint("ViewHolder", "InflateParams")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            //inflate layout wor.xml
            val myView = layoutInflater.inflate(R.layout.row, null)
            val myNote = listNotesAdapter[position]
            myView.titleNT.text = myNote.nodeName
            myView.descNT.text = myNote.nodeDesc
            //click del boton delete
            myView.deleteBtn.setOnClickListener{
                val database = Database(this.context!!)
                val selectionArgs = arrayOf(myNote.nodeName.toString())
                database.delete("ID=?", selectionArgs)
                loadQuery("%")
            }
            //click del boton de editar o actualizar
            myView.editBtn.setOnClickListener{
                goToUpdateFun(myNote)
            }
            //click del boton copiar
            myView.copyBtn.setOnClickListener{
                //obtener el titulo
                val title = myView.titleNT.text.toString()
                //obtener descripcion
                val desc = myView.descNT.text.toString()
                //concatenar titulo con la descripcion
                val s = title +"\n" + desc
                val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cb.text = s //Agregarlo al clipboard
                Toast.makeText(this@MainActivity, "Copied....", Toast.LENGTH_SHORT).show()
            }
            //Click del boton de compartir
            myView.shareBtn.setOnClickListener {
                //obtener el titulo
                val title = myView.titleNT.text.toString()
                //obtener descripcion
                val desc = myView.descNT.text.toString()
                //concatenar titulo con la descripcion
                val s = title +"\n" + desc
                // share intent
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, s)
                startActivity(Intent.createChooser(shareIntent, s))
            }
            return myView
        }

        override fun getItem(position: Int): Any {
            return listNotesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size
        }

    }

    private fun goToUpdateFun(myNote: Note) {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("ID", myNote.nodeId) //poner el ID
        intent.putExtra("name", myNote.nodeName) //poner nombre
        intent.putExtra("desc", myNote.nodeDesc) //poner descripcion
        startActivity(intent) //Iniciar el activity
    }
}