package com.app.mycoffeeapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.app.mycoffeeapp.BR
import com.app.mycoffeeapp.R
import com.app.mycoffeeapp.ViewModelProviderFactory
import com.app.mycoffeeapp.databinding.ActivitySplashBinding
import com.app.mycoffeeapp.ui.base.BaseActivity
import com.app.mycoffeeapp.ui.home.HomeActivity
import com.app.mycoffeeapp.utils.Logg
import javax.inject.Inject

class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>(),
    SplashNavigator {
    override val title: String?
        get() = ""

    override var TAG = "SplashActivity"

    override val toolbar: Toolbar?
        get() = null

    override fun openHome() {
        Logg.e(TAG, ": openHome")
        startActivity(HomeActivity.newIntent(this))
        finish()
    }

    override fun openLogin() {
        Logg.e(TAG, ": openLogin ")
    }

    @set:Inject
    var factory: ViewModelProviderFactory? = null

    override val bindingVariable: Int =
        BR.viewModel

    override val layoutId: Int
        get() = R.layout.activity_splash

    override fun getViewModel(): SplashViewModel =
        ViewModelProviders.of(this, factory)
            .get(SplashViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        getViewModel().setNavigator(this)
        getViewModel().loadData()
    }

    companion object {
        fun newIntent(baseActivity: BaseActivity<*, *>): Intent? {
            return Intent(baseActivity, SplashActivity::class.java)
        }
    }
}
