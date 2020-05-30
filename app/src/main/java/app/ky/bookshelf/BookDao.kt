package app.ky.bookshelf

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
@Dao
interface BookDao{
    @Query("select * from Bookdata")
    fun getAll(): LiveData<List<Book>>

    @Insert
    fun insert(book:Book)
}