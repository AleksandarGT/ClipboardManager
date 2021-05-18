package com.boostedpenguin.clipboardmanager

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.boostedpenguin.clipboardmanager.room.Note
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteHolder>() {
    private var notes: List<Note> = ArrayList()
    private var listener: OnCheckboxClickListener? = null
    private var longClickListener: OnLongItemClickListener? = null
    private var itemListener: OnItemClickListener? = null
    private var buttonCopyListener: OnButtonCopyClickListener? = null
    private var cardClickListener: OnCardClickListener? = null

    private var isVisible = false;
    var selectedPositions = mutableListOf<Int>()

    fun updateVisibility(value: Boolean) {
        this.isVisible = value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false)

        return NoteHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val currentNote = notes[position]

        var displayContent = if(currentNote.content.length > 20) {
            "${currentNote.content.substring(0, 19)}..."
        } else {
            currentNote.content
        }


        var readable = android.text.format.DateFormat.format("dd.MM.yyyy", currentNote.date)

        holder.textViewDescription.text = readable

        holder.textViewTitle.text = displayContent
        //holder.textViewId.text = currentNote.id.toString()

        if(notes[position].favorite) {
            holder.favoriteIcon.visibility = View.VISIBLE
        }
        else {
            holder.favoriteIcon.visibility = View.INVISIBLE
        }

        if(selectedPositions.contains(position)) {
            holder.view.setBackgroundColor(Color.GRAY)
        }
        else {
            holder.view.setBackgroundColor(Color.TRANSPARENT)
        }

        holder.checkbox.isChecked = selectedPositions.contains(position)

        if (isVisible) {
            holder.checkbox.visibility = View.VISIBLE
        }
        else {
            holder.checkbox.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun setNotes(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }


    inner class NoteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val textViewTitle: TextView = itemView.findViewById(R.id.textView)
        internal val textViewDescription: TextView = itemView.findViewById(R.id.txtDesc)
        internal val textViewId: TextView = itemView.findViewById(R.id.txtId)
        private val buttonCopy: ImageButton = itemView.findViewById(R.id.buttonCopy)
        private val cardView: CardView = itemView.findViewById(R.id.cardView)
        val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        val view: ConstraintLayout = itemView.findViewById(R.id.main)

        internal val favoriteIcon: ImageView = itemView.findViewById(R.id.favorite_status)

        init {
            cardView.setOnClickListener {
                val position = adapterPosition

                if (cardClickListener != null && position != RecyclerView.NO_POSITION) {
                    cardClickListener!!.onCardClick(position, notes[adapterPosition])
                }
            }

            cardView.setOnLongClickListener {
                val position = adapterPosition

                if (longClickListener != null && position != RecyclerView.NO_POSITION) {
                    longClickListener!!.onLongClick(position, notes[adapterPosition])
                }

                true;
            }

            buttonCopy.setOnClickListener {
                val position = adapterPosition

                if (buttonCopyListener != null && position != RecyclerView.NO_POSITION) {
                    buttonCopyListener!!.onButtonClick(position, notes[adapterPosition])
                }
            }

            // Checkbox click
            checkbox.setOnClickListener {
                val position = adapterPosition

                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener!!.onItemClick(position, notes[adapterPosition])
                }
            }

            // Enter / leave edit state
            itemView.setOnLongClickListener {
                val position = adapterPosition

                if (longClickListener != null && position != RecyclerView.NO_POSITION) {
                    longClickListener!!.onLongClick(position, notes[adapterPosition])
                }

                true;
            }

            // Item click
            itemView.setOnClickListener {
                val position = adapterPosition

                if (itemListener != null && position != RecyclerView.NO_POSITION) {
                    itemListener!!.onClick(position, notes[adapterPosition])
                }
            }
        }
    }

    interface OnCardClickListener {
        fun onCardClick(position: Int, note: Note)
    }

    fun setOnCardClickListener(listener: OnCardClickListener?) {
        this.cardClickListener = listener
    }

    interface OnButtonCopyClickListener {
        fun onButtonClick(position: Int, note: Note)
    }

    fun setOnButtonCopyClickListener(listener: OnButtonCopyClickListener?) {
        this.buttonCopyListener = listener
    }

    interface OnCheckboxClickListener {
        fun onItemClick(position: Int, note: Note)
    }

    fun setOnCheckboxClickListener(listener: OnCheckboxClickListener?) {
        this.listener = listener
    }

    interface OnLongItemClickListener {
        fun onLongClick(position: Int, note: Note)
    }

    fun setOnLongItemClickListener(listener: OnLongItemClickListener?) {
        this.longClickListener = listener
    }

    interface OnItemClickListener {
        fun onClick(position: Int, note: Note)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.itemListener = listener
    }
}