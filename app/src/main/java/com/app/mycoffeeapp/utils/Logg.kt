package com.app.mycoffeeapp.utils

import android.util.Log
import com.app.mycoffeeapp.BuildConfig

/**
 *
 * Created by admin on 8/5/2019.
 */
object Logg {
    var showLog = BuildConfig.DEBUG

    fun e(tag: String, message: String) {
        if (showLog)
            Log.e(tag, message)
    }
}