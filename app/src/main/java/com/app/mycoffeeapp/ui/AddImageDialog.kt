package com.app.mycoffeeapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.app.mycoffeeapp.R
import com.app.mycoffeeapp.databinding.DialogAddImageBinding
import com.app.mycoffeeapp.ui.base.BaseDialog
import dagger.android.support.AndroidSupportInjection

class AddImageDialog : BaseDialog() {

    var binding: DialogAddImageBinding? = null
    fun dismissDialog() {
        dismissDialog(TAG)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<DialogAddImageBinding>(inflater, R.layout.dialog_add_image, container, false)
        val view = binding?.root
        AndroidSupportInjection.inject(this)
        init()
        return view
    }

    private fun init() {
        binding?.camera?.setOnClickListener {
            baseActivity?.onCameraSelected()
            dismissDialog()
        }
        binding?.gallery?.setOnClickListener {
            baseActivity?.onGallerySelected()
            dismissDialog()
        }
    }

    fun show(fragmentManager: FragmentManager) {
        super.show(fragmentManager, TAG)
    }


    companion object {

        private val TAG = AddImageDialog::class.java.simpleName

        fun newInstance(): AddImageDialog {
            val fragment = AddImageDialog()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }
}
