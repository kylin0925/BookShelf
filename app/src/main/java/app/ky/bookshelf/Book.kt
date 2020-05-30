package app.ky.bookshelf

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName="Bookdata")
class Book: Serializable{
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    @ColumnInfo
    var title:String ?= null
    @ColumnInfo
    var isbn:String ?= null
    @ColumnInfo
    var authors:String ?= null
    @ColumnInfo
    var publisher: String ?= null
    @ColumnInfo
    var publishedDate: String?= null
    @ColumnInfo
    var pageCount: Int = 0


    fun gettitle():String?{
        return title
    }
    fun settitle(title:String){
        this.title = title
    }

    fun getisbn():String?{
        return isbn
    }
    fun setisbn(isbn:String){
        this.isbn = isbn
    }


}


