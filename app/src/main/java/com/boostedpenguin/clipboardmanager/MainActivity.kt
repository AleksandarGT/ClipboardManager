package com.boostedpenguin.clipboardmanager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boostedpenguin.clipboardmanager.databinding.ActivityMainBinding
import com.boostedpenguin.clipboardmanager.room.Note
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val model: MainActivityViewModel by viewModels { MainActivityViewModelFactory((applicationContext as NoteApplication).repository) }
    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCurrentTheme()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))


        val recyclerView = binding.recyclerView
        adapter = NoteAdapter()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)


        setListeners()
    }


    private fun setCurrentTheme() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        sharedPref?.let { pref ->
            val darkModeString = getString(R.string.dark_mode)
            val darkModeValues = resources.getStringArray(R.array.dark_mode_values)
            when (pref.getString(darkModeString, darkModeValues[0])) {
                darkModeValues[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                darkModeValues[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                darkModeValues[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    private fun setListeners() {
        model.allNotes.observe(this, Observer { it ->
            it.let {adapter.setNotes(it)}
        })

        // Toggle edit mode and etc.
        adapter.setOnLongItemClickListener(object : NoteAdapter.OnLongItemClickListener {
            override fun onLongClick(position: Int, note: Note) {
                model.isItemSelected.value = model.isItemSelected.value?.not()

                if(model.isItemSelected.value == true) {
                    model.addPosition(position, note)

                    adapter.notifyDataSetChanged()
                }
            }
        })

        adapter.setOnCardClickListener(object : NoteAdapter.OnCardClickListener {
            override fun onCardClick(position: Int, note: Note) {
                if(model.isItemSelected.value == false) {
                    startActivity(Intent(applicationContext, EditNoteActivity::class.java).putExtra("NOTE_SELECTED", note))
                }
                else {
                    handleContent(position, note)
                    adapter.notifyDataSetChanged()
                }
            }
        })

        adapter.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onClick(position: Int, note: Note) {
                handleContent(position, note)
                adapter.notifyDataSetChanged()
            }
        })


        adapter.setOnCheckboxClickListener(object : NoteAdapter.OnCheckboxClickListener {
            override fun onItemClick(position: Int, note: Note) {
                handleContent(position, note)
                adapter.notifyDataSetChanged()
            }
        })

        adapter.setOnButtonCopyClickListener(object : NoteAdapter.OnButtonCopyClickListener {
            override fun onButtonClick(position: Int, note: Note) {
                val clipboard: ClipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("clipboard_content", note.content)
                clipboard.setPrimaryClip(clip)

                Toast.makeText(applicationContext, "Copied", Toast.LENGTH_SHORT).show()
            }
        })

        model.isItemSelected.observe(this, Observer { it ->
            adapter.updateVisibility(it)
            if(!it) {
                model.clearPositions()
            }
            adapter.notifyDataSetChanged();

            // Invalidate tells it to redraw itself -> toggles onprepare
            this.invalidateOptionsMenu()
        })

        model.selectedCheckboxes.observe(this, Observer { it ->
            adapter.selectedPositions = it.toMutableList();
        })

        model.selectedNote.observe(this, Observer {
            invalidateOptionsMenu()
        })
    }

    fun handleContent(position: Int, note: Note) {
        if(model.isItemSelected.value == true) {
            if(model.selectedCheckboxes.value?.contains(position)!!) {
                model.removePosition(position, note)
            }
            else {
                model.addPosition(position, note)
            }
        }
        else {
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onBackPressed() {
        if(model.isItemSelected.value == true) {
            exitSelectMode()
            return
        }
        super.onBackPressed()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {

        if(model.isItemSelected.value == true) {
            menu.clear()
            menuInflater.inflate(R.menu.item_selected_menu , menu)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        else {
            menu.clear()
            menuInflater.inflate(R.menu.main_menu, menu)
            supportActionBar?.setDisplayShowTitleEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        if(model.isItemSelected.value == true) {
            if(model.selectedNote.value?.size!! > 0) {
                menu.findItem(R.id.selected_action_favorite).isEnabled = true
                menu.findItem(R.id.selected_action_share).isEnabled = true
                menu.findItem(R.id.selected_action_delete).isEnabled = true
            }
            else {
                menu.findItem(R.id.selected_action_favorite).isEnabled = false
                menu.findItem(R.id.selected_action_share).isEnabled = false
                menu.findItem(R.id.selected_action_delete).isEnabled = false
            }
        }



        return super.onPrepareOptionsMenu(menu)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
            R.id.main_menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.main_menu_search -> {
                true
            }
            R.id.selected_action_favorite -> {
                model.favoriteNotes()
                true
            }
            R.id.main_menu_add -> {
                startActivity(Intent(this, CreateNoteActivity::class.java))
                true
            }
            R.id.selected_action_delete -> {

                val alertDialog: AlertDialog? = this.let {
                    val builder = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                    builder.apply {
                        setPositiveButton("DELETE"
                        ) { _, _ ->
                            model.deleteNotes()
                            model.clearPositions()
                            model.isItemSelected.value = false;
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
            R.id.selected_action_share -> {
                shareContent()
                true
            }
            android.R.id.home -> {
                exitSelectMode()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun shareContent() {

        var contentString = ""
        model.selectedNote.value?.forEach {
            contentString += "${it.content}\n"
        }

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, contentString)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Notes")
        startActivity(shareIntent)
    }

    private fun exitSelectMode() {
        model.isItemSelected.value = false
        model.clearPositions()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
}