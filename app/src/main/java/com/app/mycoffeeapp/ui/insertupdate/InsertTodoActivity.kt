package com.app.mycoffeeapp.ui.insertupdate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProviders
import com.app.mycoffeeapp.R
import com.app.mycoffeeapp.ViewModelProviderFactory
import com.app.mycoffeeapp.databinding.ActivityInsertTodoBinding
import com.app.mycoffeeapp.mediaHelper.MediaCallback
import com.app.mycoffeeapp.mediaHelper.MediaHelper
import com.app.mycoffeeapp.ui.AddImageDialog
import com.app.mycoffeeapp.ui.base.BaseActivity
import com.app.mycoffeeapp.utils.CommonUtils
import com.app.mycoffeeapp.utils.Logg
import java.io.File
import javax.inject.Inject

class InsertTodoActivity : BaseActivity<ActivityInsertTodoBinding, InsertUpdateViewModel>(),
    InsertUpdateNavigator {
    override fun showMessage(s: String) {
        CommonUtils.showErrorMessage(s)
    }

    companion object {
        fun newIntent(context: BaseActivity<*, *>?): Intent? {
            context?.let { return Intent(context, InsertTodoActivity::class.java) }
            return null
        }
    }

    @set:Inject
    var factory: ViewModelProviderFactory? = null

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.activity_insert_todo

    override val title: String?
        get() = "Add Item"

    override val toolbar: Toolbar?
        get() = viewDataBinding?.actToolbar as Toolbar?


    override fun getViewModel(): InsertUpdateViewModel? {
        return ViewModelProviders.of(this, factory).get(InsertUpdateViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getViewModel()?.setNavigator(this)
    }

    override fun goBack() {
        finish()
    }

    override fun onCameraSelected() {
        Logg.e(TAG, "onCameraSelected: ")
        mMediaHelper?.takePictureFromCamera(object : MediaCallback() {
            override fun onCancel() {

            }

            override fun onResult(status: Boolean, file: File, mediaType: MediaHelper.Media) {
                if (status && file?.exists()) {
                    val mFilePhoto = file
                    Log.e("FILE PATH", "mFilePhoto: " + mFilePhoto?.absolutePath)
                    getViewModel()?.setPath(mFilePhoto?.absolutePath)

                } else {
                    showError("sdsdasd")
                }
            }
        }, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        mMediaHelper?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onGallerySelected() {
        Logg.e(TAG, "onGallerySelected: ")
        mMediaHelper?.takePictureFromGallery(object : MediaCallback() {
            override fun onCancel() {

            }


            override fun onResult(status: Boolean, file: File, mediaType: MediaHelper.Media) {
                if (status && file != null && file.exists()) {
                    var mFilePhoto = file
                    Log.e("FILE PATH", "mFilePhoto: " + mFilePhoto.absolutePath)
                    getViewModel()?.setPath(mFilePhoto?.absolutePath)

                } else {
                    showError("sdasdad")
                }
            }

        }, false)
    }

    var mMediaHelper: MediaHelper? = null

    override fun openImageSelectionDialog() {
        Logg.e(TAG, "openImageSelectionDialog: ")
        if (mMediaHelper == null)
            mMediaHelper = MediaHelper(this, MediaHelper.Media.IMAGE)
        val imageDialog = AddImageDialog.newInstance()
        imageDialog.setCancelable(true)
        imageDialog.show(supportFragmentManager)
    }

    private fun showError(s: String) {

    }


}
