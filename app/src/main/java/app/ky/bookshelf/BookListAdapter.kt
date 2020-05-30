package app.ky.bookshelf

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

class BookListAdapter(context:Context, bookList: List<Book>):RecyclerView.Adapter<BookListAdapter.ViewHolder>(){
    lateinit var layoutInflater:LayoutInflater
    lateinit var recyclerViewClickListener:RecyclerViewClickListener

    var booklist = bookList
    init {
        layoutInflater = LayoutInflater.from(context)
    }
    fun setClickListener(click: RecyclerViewClickListener){
        recyclerViewClickListener = click
    }
    public fun setBookList(allbook:List<Book>){
        this.booklist = allbook
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        var layout = layoutInflater.inflate(R.layout.booklist, parent, false)
        return ViewHolder(layout, this)
    }

    override fun getItemCount(): Int {
        return booklist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var book = booklist[position]
        holder?.tvTitle?.text = book.title
        holder?.tvAuthor?.text = book.authors
        holder?.tvIsbn?.text = book.isbn
    }

    inner class ViewHolder(itemView: View, adapter:BookListAdapter) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener{
        override fun onClick(p0: View?) {
            val TAG = "ViewHolder";
            Log.e(TAG,"click " + layoutPosition)
            if (p0 != null) {
                recyclerViewClickListener.recyclerViewerListClicked(p0,layoutPosition)
            }
        }

        var tvTitle:TextView ?= null
        var tvAuthor:TextView ?= null
        var tvIsbn:TextView ?= null
        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvAuthor = itemView.findViewById(R.id.tvAuthor)
            tvIsbn = itemView.findViewById(R.id.tvIsbn)
            itemView.setOnClickListener(this)
        }
    }
}