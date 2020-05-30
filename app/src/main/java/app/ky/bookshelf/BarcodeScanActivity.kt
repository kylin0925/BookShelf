package app.ky.bookshelf

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.TextureView.SurfaceTextureListener
import android.hardware.camera2.*
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.Image
import android.media.ImageReader
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.checkSelfPermission
import android.util.Size
import java.lang.Exception
import android.os.HandlerThread
import android.os.Handler
import android.view.*
import android.widget.Toast
import java.nio.ByteBuffer


class BarcodeScanActivity : AppCompatActivity() {
    lateinit var textureView: TextureView
    lateinit var surfaceView: View

    lateinit var mCameraDevice: CameraDevice
    lateinit var imageDiemension: Size
    lateinit var imageReader:ImageReader
    lateinit var captureRequestBuilder: CaptureRequest.Builder
    lateinit var cameraCaptureSessions: CameraCaptureSession
    lateinit var mBackgroundHandler: Handler
    lateinit var mBackgroundThread: HandlerThread
    lateinit var mHolder: SurfaceHolder
    val TAG = "BarcodeScanActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scan)
        textureView = findViewById(R.id.textureView)
        textureView.setSurfaceTextureListener(textureListener)
        surfaceView = findViewById(R.id.surfaceView)

//        surfaceView.setZOrderMediaOverlay(true)
//        mHolder = surfaceView.holder
//        mHolder.setFormat(PixelFormat.TRANSPARENT)
//        mHolder.addCallback(callbackT)
    }
    var callbackT = object :SurfaceHolder.Callback{

        override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

        }

        override fun surfaceDestroyed(p0: SurfaceHolder?) {

        }

        override fun surfaceCreated(p0: SurfaceHolder?) {
            var canvas:Canvas? = p0?.lockCanvas()
            canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            p0?.unlockCanvasAndPost(canvas)
        }

    }
    var textureListener: TextureView.SurfaceTextureListener = object : TextureView.SurfaceTextureListener {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, i: Int, i1: Int) {
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, i: Int, i1: Int) {

        }

        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {

        }
    }
    var decodeResultIntent = Intent()
    var onImageAvailableListener:ImageReader.OnImageAvailableListener = @RequiresApi(Build.VERSION_CODES.KITKAT)
        object : ImageReader.OnImageAvailableListener {
            override fun onImageAvailable(p0: ImageReader?) {
                var img: Image ?= p0?.acquireNextImage()
                if(img!=null) {
                    var bytebuffer = img.planes[0].buffer
                    var bytes = ByteArray(bytebuffer.remaining())
                    bytebuffer.get(bytes)

                    var decode = DecodeBarcode()
                    var decodeResult = decode.decode(bytes,640,480)
                    if(decodeResult!=null) {
                        Log.e(TAG, "barcode " + decodeResult + " " + img.width + " " + img.height)
                        decodeResultIntent.putExtra("BARCODE",decodeResult.toString())
                        setResult(1,decodeResultIntent)
                        finish()
                    }
                    //var nv21 = ImageUitl().YUV420toNV21(img)
                    //var jpeg = ImageUitl().NV21toJPEG(nv21,640,480,90)
                    //var bitmap = BitmapFactory.decodeByteArray(jpeg,0,jpeg.size)
                    //var thumb = BitmapDrawable(resources,bitmap)
                    runOnUiThread {
                        surfaceView.background = decode.thumb
                    }

                }
                img?.close()


            }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun openCamera(){
        var manager:CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try{
            var cameraId = manager.cameraIdList[0]
            Log.e("openCamera","cameraId " + cameraId)
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(
                CameraCharacteristics
                    .SCALER_STREAM_CONFIGURATION_MAP
            )!!
            imageDiemension = map.getOutputSizes(SurfaceTexture::class.java)[0]
            imageReader = ImageReader.newInstance(640,
                480, ImageFormat.YUV_420_888,3);
            imageReader.setOnImageAvailableListener(onImageAvailableListener,mBackgroundHandler)
            if(checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    1)
                return
            }
            val matrix = Matrix()
            var rect = RectF(0f,0f,480f,640f)
            var viewrect = RectF(0f,0f, textureView.width.toFloat(),textureView.height.toFloat())
            var centerX =viewrect.centerX()
            var centerY =viewrect.centerY()
            rect.offset(centerX - rect.centerX() , centerY - rect.centerY())

            matrix.setRectToRect(viewrect,rect,Matrix.ScaleToFit.FILL)
            var scaley = 640f/480
            var scalex =   480/640f
            var scale = Math.max(textureView.width.toFloat()/640,textureView.height.toFloat()/480)
            matrix.postScale(scale,scale,centerX,centerY)
            matrix.postRotate(270f,viewrect.centerX(),viewrect.centerY())
            textureView.setTransform(matrix)
            manager.openCamera(cameraId,stateCallback,null)
        }catch (e:CameraAccessException){
            e.printStackTrace()
        }
    }
    var stateCallback:CameraDevice.StateCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object:CameraDevice.StateCallback(){
        override fun onOpened(p0: CameraDevice) {
            mCameraDevice = p0
            createPreview()
        }

        override fun onDisconnected(p0: CameraDevice) {
            mCameraDevice.close()
        }

        override fun onError(p0: CameraDevice, p1: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
    @TargetApi(Build.VERSION_CODES.P)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun createPreview(){
        Log.e(TAG,"create preview")
        try {
            var surfaceTexture: SurfaceTexture = textureView.surfaceTexture

            surfaceTexture.setDefaultBufferSize(640,480)

            var surfaces = ArrayList<Surface>()
            var surface = Surface(surfaceTexture)
            var surfaceImage = imageReader.surface
            surfaces.add(surface)
            surfaces.add(surfaceImage)
            captureRequestBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)
            captureRequestBuilder.addTarget(surfaceImage)

            mCameraDevice.createCaptureSession(surfaces,callback, null)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    var callback:CameraCaptureSession.StateCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : CameraCaptureSession.StateCallback(){
        override fun onConfigureFailed(p0: CameraCaptureSession) {


            Toast.makeText(getApplicationContext(), "Configuration change",
                Toast.LENGTH_SHORT).show()
        }

        override fun onConfigured(p0: CameraCaptureSession) {
            //The camera is already closed
            if (null == mCameraDevice) {
                return
            }
            // When the session is ready, we start displaying the preview.
            cameraCaptureSessions = p0
            updatePreview()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun updatePreview() {
      if(null == mCameraDevice) {
            Log.e(TAG, "updatePreview error, return")
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO)
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(),
                    null, mBackgroundHandler);
        } catch (e:CameraAccessException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected fun stopBackgroundThread() {
        mBackgroundThread.quitSafely()
        try {
            mBackgroundThread.join()
            //mBackgroundThread = null
            //mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }
     protected fun startBackgroundThread() {
        mBackgroundThread =  HandlerThread("Camera Background");
        mBackgroundThread.start()
        mBackgroundHandler = Handler(mBackgroundThread.getLooper());
    }
    override fun onResume() {
        super.onResume()
        startBackgroundThread()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onPause() {
        super.onPause()
        cameraCaptureSessions?.close()
        mCameraDevice.close()
        imageReader.close()
        stopBackgroundThread()
    }
}
