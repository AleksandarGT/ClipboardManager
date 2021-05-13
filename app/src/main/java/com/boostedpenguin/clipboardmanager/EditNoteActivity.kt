package com.boostedpenguin.clipboardmanager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.boostedpenguin.clipboardmanager.databinding.ActivityCreateNoteBinding
import com.boostedpenguin.clipboardmanager.room.Note
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNoteBinding
    private val model: EditNoteActivityViewModel by viewModels { EditNoteActivityViewModelFactory((applicationContext as NoteApplication).repository) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model.currentNote.value = intent.getSerializableExtra("NOTE_SELECTED") as Note

        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val m = model.currentNote.value?.content

        binding.noteContent.setText(m)
        setSupportActionBar(findViewById(R.id.createToolbar))
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        model.currentNote.observe (this, Observer {
            invalidateOptionsMenu()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.item_view_menu, menu)
        menu.findItem(R.id.action_save).actionView.setOnClickListener {
            val content = findViewById<EditText>(R.id.note_content).text.toString()
            if(!content.isNullOrEmpty()) {
                model.update(content)
                startActivity(Intent(this, MainActivity::class.java))
            }
            else {
                Toast.makeText(this, "Can't save an empty clipboard!", Toast.LENGTH_SHORT).show()
            }
        }

        if(model.currentNote.value!!.favorite) {
            menu.findItem(R.id.action_favorite).icon.colorFilter = BlendModeColorFilter(ContextCompat.getColor(applicationContext, R.color.buttonSave), BlendMode.SRC_IN)
        }
        else {
            menu.findItem(R.id.action_favorite).icon.clearColorFilter()
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

                val alertDialog: AlertDialog? = this.let {
                    val builder = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                    builder.apply {
                        setPositiveButton("DELETE"
                        ) { _, _ ->
                            model.deleteCurrent()
                            startActivity(Intent(context, MainActivity::class.java))
                        }
                        setNegativeButton("Cancel"
                        ) { dialog, _ ->
                            // User cancelled the dialog
                            dialog.cancel()
                        }
                        setTitle("Delete selected items")
                    }
                    builder.create()
                }

                alertDialog?.show()

                true
            }
            R.id.action_favorite -> {
                model.updateFavorite()
                invalidateOptionsMenu()
                true
            }
            R.id.action_share -> true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun copy() {
        val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("clipboard_content", model.currentNote.value?.content)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(applicationContext, "Copied", Toast.LENGTH_SHORT).show()
    }
}