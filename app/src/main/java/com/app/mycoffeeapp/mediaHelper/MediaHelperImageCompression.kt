package com.app.mycoffeeapp.mediaHelper

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.app.mycoffeeapp.BuildConfig
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Media FileDirectoryUtils : Helps developer to take picture from gallery, camera, video from gallery and
 * load them in image view If it is video thumbnail is shown on it.
 *
 * @author Dipen Jansari
 */
class MediaHelperImageCompression(private val mFragment: Fragment?, type: Media) {
    private var currentCodeToSend = 100
    private val mediaHolderList: HashMap<Int, MediaCallback>
    private var mMediaType: Media? = null
    private val mActivity: Activity? = null


    init {
        mediaHolderList = HashMap()
    }


    fun takePictureFromCamera(callback: MediaCallback) {
        mMediaType = Media.IMAGE
        var currentFile: File? = null
        try {
            currentFile = createImageFile()
            if (currentFile != null && currentFile.exists()) {
                currentFile.delete()
            } else {
                currentFile?.parentFile?.mkdirs()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        val imageSelectIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (mActivity != null) {
            if (imageSelectIntent.resolveActivity(mActivity.packageManager) != null) {
                val photoURI =
                    FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", currentFile!!)
                imageSelectIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                // Grant URI permission START
                // Enableing the permission at runtime
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageSelectIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                } else {
                    val resInfoList = mActivity.packageManager
                        .queryIntentActivities(
                            imageSelectIntent, PackageManager
                                .MATCH_DEFAULT_ONLY
                        )
                    for (resolveInfo in resInfoList) {
                        val packageName = resolveInfo.activityInfo.packageName
                        mActivity.grantUriPermission(
                            packageName, photoURI, Intent
                                .FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                    }
                }
                currentCodeToSend = currentCodeToSend + 1
                callback.media = mMediaType
                callback.file = currentFile
                mediaHolderList[currentCodeToSend] = callback
                mActivity.startActivityForResult(
                    Intent.createChooser(imageSelectIntent, "Select " + "Picture"),
                    currentCodeToSend
                )
            }
        } else if (mFragment != null && mFragment.context != null && mFragment.context!!
                .packageManager != null
        ) {
            if (imageSelectIntent.resolveActivity(mFragment.context!!.packageManager) != null) {
                val photoURI = FileProvider.getUriForFile(
                    mFragment.context!!, BuildConfig
                        .APPLICATION_ID + ".provider", currentFile!!
                )
                imageSelectIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                // Grant URI permission START
                // Enableing the permission at runtime
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageSelectIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                } else {
                    val resInfoList = mFragment.context!!.packageManager
                        .queryIntentActivities(
                            imageSelectIntent, PackageManager
                                .MATCH_DEFAULT_ONLY
                        )
                    for (resolveInfo in resInfoList) {
                        val packageName = resolveInfo.activityInfo.packageName
                        mFragment.context!!.grantUriPermission(
                            packageName, photoURI, Intent
                                .FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                    }
                }
                currentCodeToSend = currentCodeToSend + 1
                callback.media = mMediaType
                callback.file = currentFile
                mediaHolderList[currentCodeToSend] = callback
                mFragment.startActivityForResult(
                    Intent.createChooser(imageSelectIntent, "Select " + "Picture"),
                    currentCodeToSend
                )
            }
        }
    }

    fun takePictureFromCamera(callback: MediaCallback, isCompress: Boolean) {
        takePictureFromCamera(callback)
    }

    fun takePictureFromGallery(callback: MediaCallback, isCompress: Boolean) {
        mMediaType = Media.IMAGE
        val imageSelectIntent = Intent()
        imageSelectIntent.action = Intent.ACTION_PICK
        imageSelectIntent.type = "image/*"
        currentCodeToSend = currentCodeToSend + 1
        callback.media = mMediaType
        mediaHolderList[currentCodeToSend] = callback
        mActivity?.startActivityForResult(
            Intent.createChooser(imageSelectIntent, "Select " + "Picture"),
            currentCodeToSend
        ) ?: mFragment?.startActivityForResult(
            Intent.createChooser(imageSelectIntent, "Select " + "Picture"),
            currentCodeToSend
        )
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (mediaHolderList.containsKey(requestCode)) {
                val mediaHolder = mediaHolderList[requestCode]
                if (mediaHolder != null) {
                    if (mediaHolder.file == null) {
                        // means file not have so get from intent
                        try {
                            mediaHolder.file = createImageFile()
                            if (mediaHolder.file!!.exists()) {
                                mediaHolder.file!!.delete()
                            } else {
                                mediaHolder.file!!.parentFile.mkdirs()
                            }
                            var inputStream: InputStream? = null
                            if (mActivity != null) {
                                inputStream = mActivity.contentResolver.openInputStream(
                                    data
                                        .data!!
                                )
                            } else if (mFragment != null) {
                                inputStream = mFragment.activity!!.contentResolver
                                    .openInputStream(data.data!!)
                            }
                            if (inputStream != null) {
                                val fileOutputStream = FileOutputStream(mediaHolder.file!!)
                                copyStream(inputStream, fileOutputStream)
                                fileOutputStream.close()
                                inputStream.close()
                            }
                        } catch (e: Exception) {
                            mediaHolder.onResult(false, null!!, mediaHolder.mediaType!!)
                        }

                    }
                    mediaHolder.file = compressCameraImage(mediaHolder.file)
                    mediaHolder.onResult(true, mediaHolder.file!!, mediaHolder.mediaType!!)
                    mediaHolderList.remove(requestCode)
                }
            }
        }
    }

    private fun compressCameraImage(file: File?): File? {
        try {
            if (file == null || file.path.isEmpty())
                return file
            val imgBitmap = getBitmapFromFile(file) ?: return file
            val bytes = ByteArrayOutputStream()
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
            val fo: FileOutputStream
            fo = FileOutputStream(file)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return file
    }

    /**
     * Creating the file with folder name coffeeapp
     *
     * @return
     */
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss"/*, Locale.US*/).format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"
        if (mActivity != null) {
            return File(File(mActivity.filesDir, "coffeeapp"), imageFileName)
        } else if (mFragment != null && mFragment.context != null) {
            return File(
                File(mFragment.context!!.filesDir, "coffeeapp"),
                imageFileName
            )
        }
        return null
    }

    enum class Media {
        IMAGE, VIDEO
    }

    companion object {

        private val TAG = MediaHelper::class.java.name
        private val MAX_HEIGHT = 1280.0f
        private val MAX_WIDTH = 1280.0f

        @Throws(IOException::class)
        private fun copyStream(input: InputStream, output: OutputStream) {
            val buffer = ByteArray(1024)
            var bytesRead: Int
            do {
                bytesRead = input.read(buffer)
                output.write(buffer, 0, bytesRead)
            } while (bytesRead != -1)
        }

        private fun getBitmapFromFile(file: File): Bitmap? {
            var scaledBitmap: Bitmap? = null
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            var bmp: Bitmap? = BitmapFactory.decodeFile(file.path, bmOptions) //it will fill
            // bmOptions with image
            // height & width.
            var actualHeight = bmOptions.outHeight
            var actualWidth = bmOptions.outWidth
            var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
            val maxRatio = MAX_WIDTH / MAX_HEIGHT
            if (actualHeight > MAX_HEIGHT || actualWidth > MAX_WIDTH) {
                if (imgRatio < maxRatio) {
                    imgRatio = MAX_HEIGHT / actualHeight
                    actualWidth = (imgRatio * actualWidth).toInt()
                    actualHeight = MAX_HEIGHT.toInt()
                } else if (imgRatio > maxRatio) {
                    imgRatio = MAX_WIDTH / actualWidth
                    actualHeight = (imgRatio * actualHeight).toInt()
                    actualWidth = MAX_WIDTH.toInt()
                } else {
                    actualHeight = MAX_HEIGHT.toInt()
                    actualWidth = MAX_WIDTH.toInt()
                }
            }
            bmOptions.inSampleSize = calculateInSampleSize(bmOptions, actualWidth, actualHeight)
            // Decode bitmap with inSampleSize set
            bmOptions.inJustDecodeBounds = false
            bmOptions.inDither = false
            bmOptions.inPurgeable = true
            bmOptions.inInputShareable = true
            bmOptions.inTempStorage = ByteArray(16 * 1024)
            try {
                bmp = BitmapFactory.decodeFile(file.path, bmOptions)
            } catch (ex: OutOfMemoryError) {
                ex.printStackTrace()
            }

            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565)
            } catch (ex: OutOfMemoryError) {
                ex.printStackTrace()
            }

            val ratioX = actualWidth / bmOptions.outWidth.toFloat()
            val ratioY = actualHeight / bmOptions.outHeight.toFloat()
            val middleX = actualWidth / 2.0f
            val middleY = actualHeight / 2.0f
            val scaleMatrix = Matrix()
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
            val canvas = Canvas(scaledBitmap!!)
            canvas.matrix = scaleMatrix
            canvas.drawBitmap(bmp!!, middleX - bmp.width / 2, middleY - bmp.height / 2, Paint(Paint.FILTER_BITMAP_FLAG))
            bmp?.recycle()

            try {
                val exif = ExifInterface(file.path)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
                val m = Matrix()
                if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                    m.postRotate(180f)
                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                    m.postRotate(90f)
                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                    m.postRotate(270f)
                }
                scaledBitmap = Bitmap.createBitmap(
                    scaledBitmap, 0, 0, scaledBitmap.width,
                    scaledBitmap.height, m, true
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return scaledBitmap
        }

        fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
                val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            }
            val totalPixels = (width * height).toFloat()
            val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
            return inSampleSize
        }
    }
}