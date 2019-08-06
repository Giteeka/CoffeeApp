package com.app.mycoffeeapp.ui.insertupdate

import com.app.mycoffeeapp.data.DataManager
import com.app.mycoffeeapp.data.model.Item
import com.app.mycoffeeapp.ui.base.BaseViewModel
import com.app.mycoffeeapp.utils.Logg

class InsertUpdateViewModel(dataManager: DataManager) : BaseViewModel<InsertUpdateNavigator>(dataManager) {

    var TAG = "InsertUpdateVM"
    public fun add(text: String) {
        Logg.e(TAG, "text : $text")
        val todo = Item()
        todo.name = text
        setIsLoading(true)
//        val subscribe =
//            dbHelper.insert(todo).subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe(
//                {
//                    Log.e(TAG, "on next value : $it")
//                    todo._Id = it
//                },
//                {
//
//                    Log.e(TAG, "Error : " + it.message)
//                },
//                {
//                    Log.e(TAG, ":completed ")
//                    getNavigator()?.showMessage("Inserted successfully")
//                    getNavigator()?.goBack()
//                }
//            )


    }

}