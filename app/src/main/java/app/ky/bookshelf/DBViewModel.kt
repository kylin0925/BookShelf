package app.ky.bookshelf

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

class DBViewModel(application: Application) : AndroidViewModel(application){
    private var repo = BookDbRepo(application)
    private var allbook: LiveData<List<Book>>?
    init {
        allbook = repo.getAllBook()
    }
    fun getAllBook():LiveData<List<Book>>?{
        return this.allbook
    }

    fun getBook(isbn:String):LiveData<Book>?{
        return repo.getBook(isbn)
    }
    fun insert(book:Book){
        repo.insert(book)
    }
}