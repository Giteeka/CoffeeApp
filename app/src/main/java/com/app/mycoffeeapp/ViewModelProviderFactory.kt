package com.app.mycoffeeapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.mycoffeeapp.data.DataManager
import com.app.mycoffeeapp.ui.home.HomeViewModel
import com.app.mycoffeeapp.ui.insertupdate.InsertUpdateViewModel
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
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(dataManager) as T
        } else if (modelClass.isAssignableFrom(InsertUpdateViewModel::class.java)) {
            return InsertUpdateViewModel(dataManager) as T
        }
        return super.create(modelClass)
    }
}