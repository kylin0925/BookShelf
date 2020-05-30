package app.ky.bookshelf

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [Book::class], version = 1)
abstract class appDatabase: RoomDatabase(){
    abstract fun newBook(): BookDao
}