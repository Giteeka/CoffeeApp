package com.app.mycoffeeapp.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.app.mycoffeeapp.BR
import com.app.mycoffeeapp.R
import com.app.mycoffeeapp.ViewModelProviderFactory
import com.app.mycoffeeapp.databinding.ActivitySplashBinding
import com.app.mycoffeeapp.ui.base.BaseActivity
import com.app.mycoffeeapp.utils.Logg
import javax.inject.Inject

class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>(),
    SplashNavigator {

    val TAG = "SplashActivity";

    override fun openHome() {
        Logg.e(TAG, ": openHome");

    }

    override fun openLogin() {
        Logg.e(TAG, ": openLogin ");
    }

    @set:Inject
    var factory: ViewModelProviderFactory? = null

    override val bindingVariable: Int =
        BR.viewModel

    override val layoutId: Int
        get() = R.layout.activity_splash

    override val viewModel: SplashViewModel =
        ViewModelProviders.of(this, factory)
            .get(SplashViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        viewModel.loadData()
    }

    companion object {
        fun newIntent(baseActivity: BaseActivity<*, *>): Intent? {
            return Intent(baseActivity, SplashActivity::class.java)
        }
    }
}
