package com.app.mycoffeeapp.mediaHelper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.app.mycoffeeapp.BuildConfig
import com.app.mycoffeeapp.ui.base.BaseActivity
import com.app.mycoffeeapp.ui.base.BaseFragment
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Media FileDirectoryUtils : Helps developer to take picture from gallery, camera, video from gallery and
 * load them in image view If it is video thumbnail is shown on it.
 *
 * @author Dipen Jansari
 */
@SuppressLint("UseSparseArrays")
class MediaHelper {
    private var currentCodeToSend = 100
    private var mediaHolderList: HashMap<Int, MediaCallback>? = null
    private var mMediaType: Media? = null

    private var mActivity: Activity? = null
    private var mFragment: Fragment? = null
    private var isCompress = true


    constructor(activity: BaseActivity<*, *>, type: Media) {
        mActivity = activity
        mediaHolderList = HashMap()

    }

    constructor(fragment: BaseFragment<*, *>, type: Media) {
        mFragment = fragment
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
            if (imageSelectIntent.resolveActivity(mActivity?.packageManager) != null) {
                val photoURI =
                    FileProvider.getUriForFile(mActivity!!, BuildConfig.APPLICATION_ID + ".provider", currentFile!!)
                imageSelectIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                // Grant URI permission START
                // Enableing the permission at runtime
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageSelectIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                } else {
                    val resInfoList = mActivity?.packageManager?.queryIntentActivities(
                        imageSelectIntent, PackageManager
                            .MATCH_DEFAULT_ONLY
                    )
                    if (resInfoList != null) {
                        for (resolveInfo in resInfoList) {
                            val packageName = resolveInfo.activityInfo.packageName
                            mActivity?.grantUriPermission(
                                packageName, photoURI, Intent
                                    .FLAG_GRANT_WRITE_URI_PERMISSION
                            )
                        }
                    }
                }
                currentCodeToSend = currentCodeToSend + 1
                callback.mediaType = mMediaType
                callback.file = currentFile
                mediaHolderList!![currentCodeToSend] = callback
                mActivity?.startActivityForResult(
                    Intent.createChooser(imageSelectIntent, "Select " + "Picture"),
                    currentCodeToSend
                )
            }
        } else if (mFragment != null && mFragment?.context != null && mFragment?.context!!
                .packageManager != null
        ) {
            if (imageSelectIntent.resolveActivity(mFragment?.context!!.packageManager) != null) {
                val photoURI = FileProvider.getUriForFile(
                    mFragment?.context!!, BuildConfig
                        .APPLICATION_ID + ".provider", currentFile!!
                )
                imageSelectIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                // Grant URI permission START
                // Enableing the permission at runtime
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageSelectIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                } else {
                    val resInfoList = mFragment?.context!!.packageManager
                        .queryIntentActivities(
                            imageSelectIntent, PackageManager
                                .MATCH_DEFAULT_ONLY
                        )
                    for (resolveInfo in resInfoList) {
                        val packageName = resolveInfo.activityInfo.packageName
                        mFragment?.context!!.grantUriPermission(
                            packageName, photoURI, Intent
                                .FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                    }
                }
                currentCodeToSend = currentCodeToSend + 1
                callback.mediaType = mMediaType
                callback.file = currentFile
                mediaHolderList!![currentCodeToSend] = callback
                mFragment?.startActivityForResult(
                    Intent.createChooser(imageSelectIntent, "Select " + "Picture"),
                    currentCodeToSend
                )
            }
        }
    }

    fun takePictureFromCamera(callback: MediaCallback, isCompress: Boolean) {
        this.isCompress = false
        takePictureFromCamera(callback)
    }

    fun takePictureFromGallery(callback: MediaCallback, isCompress: Boolean) {
        mMediaType = Media.IMAGE
        this.isCompress = false
        val imageSelectIntent = Intent()
        imageSelectIntent.action = Intent.ACTION_PICK
        imageSelectIntent.type = "image/*"
        currentCodeToSend = currentCodeToSend + 1
        callback.mediaType = mMediaType
        mediaHolderList!![currentCodeToSend] = callback
        mActivity?.startActivityForResult(
            Intent.createChooser(imageSelectIntent, "Select " + "Picture"),
            currentCodeToSend
        ) ?: mFragment?.startActivityForResult(
            Intent.createChooser(imageSelectIntent, "Select " + "Picture"),
            currentCodeToSend
        )
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (mediaHolderList!!.containsKey(requestCode)) {
                val mediaHolder = mediaHolderList!![requestCode]
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
                                inputStream = mActivity?.contentResolver?.openInputStream(
                                    data?.data!!
                                )
                            } else if (mFragment != null) {
                                inputStream = mFragment?.activity!!.contentResolver
                                    .openInputStream(data?.data!!)
                            }
                            if (inputStream != null) {
                                val fileOutputStream = FileOutputStream(mediaHolder.file)
                                copyStream(inputStream, fileOutputStream)
                                fileOutputStream.close()
                                inputStream.close()
                            }
                        } catch (e: Exception) {
                            mediaHolder.onResult(false, null!!, mediaHolder.mediaType!!)

                        }

                    }
                    if (isCompress)
                        mediaHolder.file = compressCameraImage(mediaHolder.file)
                    mediaHolder.onResult(true, mediaHolder.file!!, mediaHolder.mediaType!!)
                    mediaHolderList!!.remove(requestCode)
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (mediaHolderList!!.containsKey(requestCode)) {
                val mediaHolder = mediaHolderList!![requestCode]
                mediaHolder?.onCancel()
            }
        }
    }

    private fun compressCameraImage(file: File?): File? {
        try {
            if (file == null || file.path.isEmpty()) return file
            val imgBitmap = getBitmapFromFile(file, 700) ?: return file
            val bytes = ByteArrayOutputStream()
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
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
     * Creating the file with folder name
     *
     * @return
     */
    private fun createImageFile(): File? {

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss"/*, Locale.US*/).format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"

        if (mActivity != null) {
            return File(File(mActivity?.filesDir, "coffeeapp"), imageFileName)
        } else if (mFragment != null && mFragment?.context != null) {
            return File(
                File(mFragment?.context!!.filesDir, "coffeeapp"),
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

        @Throws(IOException::class)
        private fun copyStream(input: InputStream, output: OutputStream) {
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } >= 0) {
                output.write(buffer, 0, bytesRead)
            }

        }

        private fun getBitmapFromFile(file: File, targetSize: Int): Bitmap? {
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.path, bmOptions) //it will fill bmOptions with image
            // height & width.
            bmOptions.inSampleSize = calculateInSampleSize(bmOptions, targetSize, targetSize)
            // Decode bitmap with inSampleSize set
            bmOptions.inJustDecodeBounds = false
            var profilePic = BitmapFactory.decodeFile(file.path, bmOptions)
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
                profilePic = Bitmap.createBitmap(
                    profilePic, 0, 0, profilePic.width,
                    profilePic.height, m, true
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return profilePic
        }

        private fun calculateInSampleSize(
            ourOption: BitmapFactory.Options,
            imageWidth: Int, imageHeight: Int
        ): Int {
            val height = ourOption.outHeight
            val width = ourOption.outWidth
            var inSampleSize = 1
            if (height > imageHeight || width > imageWidth) {
                if (width > height) {
                    inSampleSize = Math.round(height.toFloat() / imageHeight.toFloat())
                } else {
                    inSampleSize = Math.round(width.toFloat() / imageWidth.toFloat())
                }
            }
            return inSampleSize
        }
    }
}