package app.ky.bookshelf

import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView

class CameraSurface :SurfaceView {
    lateinit var mHolder:SurfaceHolder
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet): super(context,attrs)
    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int): super(context,attrs,defStyleAttr)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
            :super(context,attrs,defStyleAttr,defStyleRes)


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var paint:Paint = Paint()
        paint.color = Color.GREEN
        paint.style = Paint.Style.STROKE
        canvas?.drawRect(Rect(10,10,300,200), paint)
        canvas?.drawColor(Color.TRANSPARENT,PorterDuff.Mode.CLEAR)

        invalidate()
    }

}

