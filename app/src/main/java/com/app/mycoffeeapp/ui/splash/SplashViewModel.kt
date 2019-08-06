package com.app.mycoffeeapp.ui.splash

import com.app.mycoffeeapp.data.DataManager
import com.app.mycoffeeapp.ui.base.BaseViewModel
import com.app.mycoffeeapp.utils.Logg
import java.lang.Exception

/**
 *
 * Created by admin on 8/5/2019.
 */
class SplashViewModel(dataManager: DataManager) : BaseViewModel<SplashNavigator>(dataManager) {
    val TAG = "SplashViewModel";
    fun loadData() {
        // check user is logged in or not
        var data = dataManager.insertData()
        Logg.e(TAG, ": $data")

        Thread(Runnable {
            try {
                Thread.sleep(1000)
            } catch (e: Exception) {

            } finally {
                getNavigator()?.openHome()
            }
        }).start()
    }


}