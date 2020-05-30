package app.ky.bookshelf

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Room
import android.os.AsyncTask
import android.util.Log

class BookDbRepo(var application: Application){
    var db:appDatabase
    var dao:BookDao
    val TAG = "BookDbRepo"
    init{
        db = Room
            .databaseBuilder(application,appDatabase::class.java,"book_db")
            .build()
        dao = db.newBook()
    }

    fun getAllBook(): LiveData<List<Book>> {
        return dao.getAll()
    }

    fun insert(book:Book){
        inserAsyncTask(dao).execute(book)
    }

    inner class inserAsyncTask(dao:BookDao) : AsyncTask<Book,Void,Void>() {
        override fun doInBackground(vararg params: Book): Void? {
            if(params !=null && params!![0] != null){
                Log.e(TAG, "add new book " + params[0])
                dao.insert(params[0])
            }
            return null
        }
    }
}