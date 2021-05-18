package com.boostedpenguin.clipboardmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.boostedpenguin.clipboardmanager.databinding.ActivityCreateNoteBinding
import com.boostedpenguin.clipboardmanager.room.Note
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CreateNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNoteBinding
    private val model: CreateNoteActivityViewModel by viewModels { CreateNoteActivityViewModelFactory((applicationContext as NoteApplication).repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_create_note)

        setSupportActionBar(findViewById(R.id.createToolbar))
        supportActionBar?.title = "Add clipboard"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.create_menu, menu)
        val item = menu.findItem(R.id.action_save)
        item.actionView.setOnClickListener {
            saveItem()
        }

        return true
    }

    private fun saveItem() {
        val content = findViewById<EditText>(R.id.note_content).text.toString()
        if(!content.isNullOrEmpty()) {
            model.insert(Note(content))
            startActivity(Intent(this, MainActivity::class.java))
        }
        else {
            Toast.makeText(this, "Can't save an empty clipboard!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        onBack()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBack()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun onBack() {
        val alertDialog: AlertDialog? = this.let {
            val builder = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            builder.apply {
                setPositiveButton("Save"
                ) { _, _ ->

                    saveItem()
                }
                setNegativeButton("Discard"
                ) { _, _ ->

                    finish()
                }
                setNeutralButton("Cancel") {
                    dialog, _ ->

                    // User cancelled the dialog
                    dialog.cancel()
                }
                setTitle("Save your changes or discard them?")
            }
            builder.create()
        }

        if(findViewById<EditText>(R.id.note_content).text.toString().isNotEmpty()) {
            alertDialog?.show()
        }
        else {
            finish()
        }
    }
}