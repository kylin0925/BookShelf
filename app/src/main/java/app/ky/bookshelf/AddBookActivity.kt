package app.ky.bookshelf

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_add_book.*

class AddBookActivity : AppCompatActivity() {
    var query = DataRequestUtil(this)
    val TAG ="AddBookActivity"
    var bookData:Book?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)
    }

    fun Cancel(view:View){
        finish()
    }

    fun Ok(view:View){
        var intent = Intent()
        intent.putExtra("BOOKDATA",bookData)
        setResult(2,intent)
        finish()
    }
    fun ScanBarcode(view:View){
        var intent = Intent(applicationContext,BarcodeScanActivity::class.java)
        startActivityForResult(intent,1)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data !=null) {
            var barcode = data.getStringExtra("BARCODE")
            Log.e("DecodeResult", barcode)
            edtISBN.setText(barcode)

            query.setQueryHandler(handler)
            query.requestBookData(barcode)
        }
    }

    var handler = object: Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            Log.e(TAG,"handler what " + msg.what)
            Log.e(TAG,"handler what " + msg.obj)
            var book = query.parseData(msg.obj.toString())
            if (book != null) {
                tvBook.text = book.title
                tvAuthor.text = book.authors
                tvAuthor.text = book.publisher
                tvPublishedDate.text = book.publishedDate
                tvPageCount.text = book.pageCount.toString()
                book.isbn = edtISBN.text.toString()
                bookData = book
            }else{
                tvBook.setText("Not found")
                book = null
            }
        }
    }
}
