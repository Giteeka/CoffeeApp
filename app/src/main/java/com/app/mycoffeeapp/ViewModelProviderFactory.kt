package com.app.mycoffeeapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.mycoffeeapp.data.DataManager
import com.app.mycoffeeapp.ui.splash.SplashViewModel
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 * Created by admin on 8/5/2019.
 */
@Singleton
class ViewModelProviderFactory @Inject constructor(var dataManager: DataManager) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(dataManager) as T
        }
        return super.create(modelClass)
    }
}