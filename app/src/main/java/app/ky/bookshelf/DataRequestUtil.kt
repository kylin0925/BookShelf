package app.ky.bookshelf

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class DataRequestUtil(context: Context){
    var context = context
    var handler: Handler = Handler(Looper.getMainLooper())
    val TAG = "DataRequestUtil"
    fun requestBookData(isbn:String){
        var url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn;
        val queue = Volley.newRequestQueue(this.context)

        val req = StringRequest(Request.Method.GET, url,
                Response.Listener<String> {
                    response ->

                    Log.e(TAG,response)
                    handler.obtainMessage(1,response).apply { sendToTarget() }
                    //parseData(response)

                }, Response.ErrorListener {
                    Log.e(TAG,"Error ")
                }
            )
        queue.add(req)
    }
    fun setQueryHandler(handler: Handler){
        this.handler = handler
    }
    fun parseData(response:String): Book?{
        lateinit var jsonArray:JSONArray
        var item:JSONObject
        var volumInfo:JSONObject
        var title :String=""
        var authors:JSONArray ?= null
        var author = ""
        var publisher:String = ""
        var publishedDate = ""
        var pageCount = 0
        var jsonObj = JSONObject(response)
        var totalItems = jsonObj.getInt("totalItems")

        if(totalItems == 0)
            return null


        try {
            jsonArray = jsonObj.getJSONArray("items")

            item = jsonArray.getJSONObject(0)

            volumInfo = item.getJSONObject("volumeInfo")


            title = volumInfo.optString("title")

            authors = volumInfo.optJSONArray("authors")

            publisher = volumInfo.optString("publisher")

            publishedDate = volumInfo.optString("publishedDate")


            pageCount = volumInfo.optInt("pageCount")
            if (authors != null) {
                Log.e(TAG, "Book authors :" + authors)
                for (i in 0..authors.length() - 1) {
                    if (i == 0)
                        author += authors.getString(i)
                    else
                        author += ", " + authors.getString(i)
                }
            }
        }catch (ex:Exception){
            Log.e(TAG,"Error " + ex.toString())
        }


        Log.e(TAG,"Book title :" + title)

        Log.e(TAG,"Book publisher :" + publisher)
        Log.e(TAG,"Book publishedDate :" + publishedDate)
        Log.e(TAG,"Book pageCount :" + pageCount)

        var book = Book()
        book.title = title
        book.authors = author
        book.publisher = publisher
        book.publishedDate = publishedDate
        book.pageCount = pageCount
        return book
    }
}