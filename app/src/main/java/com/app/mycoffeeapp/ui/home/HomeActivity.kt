package com.app.mycoffeeapp.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.app.mycoffeeapp.R
import com.app.mycoffeeapp.BR
import com.app.mycoffeeapp.ViewModelProviderFactory
import com.app.mycoffeeapp.databinding.ActivityHomeBinding
import com.app.mycoffeeapp.ui.base.BaseActivity
import com.app.mycoffeeapp.ui.insertupdate.InsertTodoActivity
import com.app.mycoffeeapp.utils.Logg
import javax.inject.Inject

class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(), HomeNavigator {


    override val title: String? = "Home"


    override var TAG = "HomeActivity"

    @set:Inject
    var factory: ViewModelProviderFactory? = null

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.activity_home

    override val toolbar: Toolbar?
        get() = viewDataBinding?.actToolbar as Toolbar?

    companion object {
        fun newIntent(context: BaseActivity<*, *>?): Intent? {
            context?.let {
                return Intent(it, HomeActivity::class.java)
            }
            return null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getViewModel()?.init()
        getViewModel()?.setNavigator(this)
    }

    override fun getViewModel(): HomeViewModel? {
        return ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)
    }

    override fun openInsertScreen() {
        Logg.e(TAG, ":openInsertScreen ")
        startActivity(InsertTodoActivity.newIntent(this))
    }

}
