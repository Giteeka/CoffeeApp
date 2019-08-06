package com.app.mycoffeeapp.ui.home

import com.app.mycoffeeapp.data.DataManager
import com.app.mycoffeeapp.ui.base.BaseViewHolder
import com.app.mycoffeeapp.ui.base.BaseViewModel
import com.app.mycoffeeapp.utils.Logg

class HomeViewModel(dataManager: DataManager) : BaseViewModel<HomeNavigator>(dataManager) {
    var TAG = "HomeViewModel"

    fun init() {
        Logg.e(TAG, "init: ")
    }

}