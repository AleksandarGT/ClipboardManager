package com.boostedpenguin.clipboardmanager.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Database(entities = arrayOf(Note::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao


    private class NoteDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            instance?.let { database ->
                scope.launch {

                    // Pre-add
                    var noteDao = database.noteDao()

                    // Delete all content here.
                    noteDao.deleteAll()

                    noteDao.insert(Note("Example clipboard item"))

                }
            }
        }
    }


    companion object {
        private const val DB_NAME = "db_notes"
        private var instance: NoteDatabase? = null

        fun getInstance(
            context: Context,
            scope: CoroutineScope
        ): NoteDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(NoteDatabaseCallback(scope))
                    .build()
                Companion.instance = instance
                // return instance
                instance
            }
        }

        fun destroyInstance() {
            instance = null
        }
    }
}