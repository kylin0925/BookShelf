package app.ky.bookshelf

import android.view.View

interface RecyclerViewClickListener {
    fun recyclerViewerListClicked(v: View, position: Int)
}