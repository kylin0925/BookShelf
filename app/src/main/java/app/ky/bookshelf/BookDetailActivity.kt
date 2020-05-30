package app.ky.bookshelf

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_book_detail.*


class BookDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)


        var bundle =intent.extras
        if (bundle!=null){
            var book = bundle.getSerializable("BOOKDATA") as Book
            tvISBN.text = book.isbn
            tvBook.text = book.title
            tvAuthor.text = book.authors
            tvPublishedDate.text = book.publishedDate
            tvPageCount.text = book.pageCount.toString()
            tvPublisher.text = book.publisher
        }
    }
}
