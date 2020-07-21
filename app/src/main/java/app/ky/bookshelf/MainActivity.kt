package app.ky.bookshelf

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var btnBarcodeScan: Button ?= null
    var edtBarcode: EditText ?= null
    var recyclerView: RecyclerView ?= null
    lateinit var viewModel:DBViewModel

    val TAG = "MainActivity"
    lateinit var adapter :BookListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnBarcodeScan = findViewById(R.id.btnBarcodeScan)
        btnBarcodeScan?.setOnClickListener {
            var intent = Intent(applicationContext,BarcodeScanActivity::class.java)
            startActivityForResult(intent,1)
        }

        edtBarcode = findViewById(R.id.edtBarcode)
        recyclerView = findViewById(R.id.recyclerView)
        adapter = BookListAdapter(this,genTestBookList())

        viewModel = ViewModelProviders.of(this).get(DBViewModel::class.java)
        viewModel.getAllBook()?.observe(this, object :Observer<List<Book>>{
            override fun onChanged(t: List<Book>?) {
                Log.e(TAG,"viewModel onChanged " + t)
                if(t!=null)
                    adapter.setBookList(t)
            }
        })
        adapter.setClickListener(recyclerViewClickListener)

        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(this)

        var book = viewModel.getBook("9789864766758")?.observe(this, object :Observer<Book>{
            override fun onChanged(t: Book?) {
                Log.e(TAG,"viewModel onChanged " + t)
                if(t!=null)
                    Log.e(TAG,"title " + t)
            }
        })


        queryAsyncTask().execute("9789864766758")
    }
    inner class queryAsyncTask() : AsyncTask<String, Book, Book>() {
        override fun doInBackground(vararg params: String?): Book? {

            if(params !=null && params!![0] != null){
                Log.e(TAG, "QUERY new book " + params[0])
                var repo = BookDbRepo(application)
                var res = repo.getBook2(params[0]!!)
                Log.e(TAG, "QUERY " + res)
                return res
            }
            return null
        }

        override fun onPostExecute(result: Book?) {
            super.onPostExecute(result)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data !=null) {

            if(resultCode == 1){
                var barcode = data.getStringExtra("BARCODE")
                Log.e("DecodeResult", barcode)
                edtBarcode?.setText(barcode)
            }else{
                var book = data.getExtras()?.getSerializable("BOOKDATA") as Book
                Log.e("DecodeResult", "book titile " + book.title)
                viewModel.insert(book)
            }
        }
    }

    fun genTestBookList(): List<Book> {
        var book:Book = Book()
        book.title = "Hello world"
        book.authors = "aaa"
        book.isbn = "12345678"
        var list:List<Book> = ArrayList<Book>()
        //list.addLast(book)
        return list
    }
    fun addBook(view: View){
        var intent = Intent(applicationContext,AddBookActivity::class.java)
        startActivityForResult(intent,2)
    }

    var recyclerViewClickListener:RecyclerViewClickListener = object : RecyclerViewClickListener{
        override fun recyclerViewerListClicked(v: View, position: Int) {
            Log.e(TAG, "click " + position + " " +adapter.booklist[position].title)

            var intent = Intent(applicationContext, BookDetailActivity::class.java)
            intent.putExtra("BOOKDATA",adapter.booklist[position])
            startActivity(intent)
        }

    }
}
