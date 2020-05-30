package app.ky.bookshelf

import android.R.attr.left
import android.R.attr.top
import android.graphics.ImageFormat
import android.graphics.Rect
import android.media.Image.Plane
import android.graphics.YuvImage
import android.media.Image
import android.os.Build
import android.support.annotation.RequiresApi
import java.io.ByteArrayOutputStream


class ImageUitl {
    fun NV21toJPEG(nv21: ByteArray, width: Int, height: Int, quality: Int): ByteArray {
        val out = ByteArrayOutputStream()
        val yuv = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        yuv.compressToJpeg(Rect(10,10,310,210), quality, out)
        return out.toByteArray()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun YUV420toNV21(image: Image): ByteArray {
        val crop = image.getCropRect()
        val format = image.getFormat()
        val width = crop.width()
        val height = crop.height()
        val planes = image.getPlanes()
        val data = ByteArray(width * height * ImageFormat.getBitsPerPixel(format) / 8)
        val rowData = ByteArray(planes[0].getRowStride())

        var channelOffset = 0
        var outputStride = 1
        for (i in planes.indices) {
            when (i) {
                0 -> {
                    channelOffset = 0
                    outputStride = 1
                }
                1 -> {
                    channelOffset = width * height + 1
                    outputStride = 2
                }
                2 -> {
                    channelOffset = width * height
                    outputStride = 2
                }
            }

            val buffer = planes[i].getBuffer()
            val rowStride = planes[i].getRowStride()
            val pixelStride = planes[i].getPixelStride()

            val shift = if (i == 0) 0 else 1
            val w = width shr shift
            val h = height shr shift
            buffer.position(rowStride * (crop.top shr shift) + pixelStride * (crop.left shr shift))
            for (row in 0 until h) {
                val length: Int
                if (pixelStride == 1 && outputStride == 1) {
                    length = w
                    buffer.get(data, channelOffset, length)
                    channelOffset += length
                } else {
                    length = (w - 1) * pixelStride + 1
                    buffer.get(rowData, 0, length)
                    for (col in 0 until w) {
                        data[channelOffset] = rowData[col * pixelStride]
                        channelOffset += outputStride
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length)
                }
            }
        }
        return data
    }
}