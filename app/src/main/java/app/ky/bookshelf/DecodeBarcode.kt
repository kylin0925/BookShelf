package app.ky.bookshelf

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.ReaderException
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import java.lang.Exception

class DecodeBarcode {
    lateinit var thumb:Drawable
    fun decode(bitmap:Bitmap){
        var multiFormatReader:MultiFormatReader = MultiFormatReader()
        var result:Result?= null
        try {


        }catch (e:Exception){
            //e.printStackTrace()
        }finally {
            multiFormatReader.reset()
        }
    }
    fun decode(data:ByteArray, width:Int, height:Int):Result?{
        var multiFormatReader:MultiFormatReader = MultiFormatReader()
        var source:PlanarYUVLuminanceSource = PlanarYUVLuminanceSource(data, width, height,640/2-150,
            320/2-100,300,200, false)

        var result:Result?= null
        if(source !=null){
            var bitmap = BinaryBitmap(HybridBinarizer(source))
            try {
                result = multiFormatReader.decodeWithState(bitmap)
            }catch (e:Exception){
                //e.printStackTrace()
            }finally {
                multiFormatReader.reset()
            }
        }
        //if(source!=null) {
            var bytes = source.renderThumbnail()
            var w = source.thumbnailWidth
            var h = source.thumbnailHeight
            var bitmap = Bitmap.createBitmap(bytes, 0,w, w, h, Bitmap.Config.ARGB_8888)
            var mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            thumb = BitmapDrawable(mutableBitmap)
        //}
        return result
    }


}