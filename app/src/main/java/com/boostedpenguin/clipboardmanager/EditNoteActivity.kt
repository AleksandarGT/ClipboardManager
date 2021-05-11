package com.boostedpenguin.clipboardmanager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.boostedpenguin.clipboardmanager.databinding.ActivityCreateNoteBinding
import com.boostedpenguin.clipboardmanager.room.Note

class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNoteBinding
    private val model: EditNoteActivityViewModel by viewModels { EditNoteActivityViewModelFactory((applicationContext as NoteApplication).repository) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model.currentNote = intent.getSerializableExtra("NOTE_SELECTED") as Note

        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val m = model.currentNote?.content

        binding.noteContent.setText(m)
        setSupportActionBar(findViewById(R.id.createToolbar))
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.item_view_menu, menu)
        val item = menu.findItem(R.id.action_save)
        item.actionView.setOnClickListener {
            val content = findViewById<EditText>(R.id.note_content).text.toString()
            if(!content.isNullOrEmpty()) {
                model.update(content)
                startActivity(Intent(this, MainActivity::class.java))
            }
            else {
                Toast.makeText(this, "Can't save an empty clipboard!", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_copy -> {
                copy()
                true
            }
            R.id.action_delete -> {
                startActivity(Intent(this, MainActivity::class.java))
                model.deleteCurrent()
                true
            }
            R.id.action_favorite -> {
                model.updateFavorite()
                true
            }
            R.id.action_share -> true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun copy() {
        val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("clipboard_content", model.currentNote.content)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(applicationContext, "Copied", Toast.LENGTH_SHORT).show()
    }
}