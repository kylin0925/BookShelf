package app.ky.bookshelf

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
@Dao
interface BookDao{
    @Query("select * from Bookdata")
    fun getAll(): LiveData<List<Book>>

    @Query("select * from Bookdata where isbn = :isbn")
    fun getBook(isbn:String): LiveData<Book>

    @Query("select * from Bookdata where isbn = :isbn")
    fun getBook2(isbn:String): Book

    @Insert
    fun insert(book:Book)
}