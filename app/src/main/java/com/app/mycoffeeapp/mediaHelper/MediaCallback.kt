package com.app.mycoffeeapp.mediaHelper

import java.io.File

abstract class MediaCallback {

    var file: File? = null
    var mediaType: MediaHelper.Media? = null
    var media: MediaHelperImageCompression.Media? = null

    abstract fun onResult(status: Boolean, file: File, mediaType: MediaHelper.Media)

    abstract fun onCancel()

}