package com.app.mycoffeeapp.ui.insertupdate

import android.util.Log
import androidx.databinding.ObservableField
import com.app.mycoffeeapp.data.DataManager
import com.app.mycoffeeapp.data.model.Item
import com.app.mycoffeeapp.ui.base.BaseViewModel
import com.app.mycoffeeapp.utils.Logg
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class InsertUpdateViewModel(dataManager: DataManager) : BaseViewModel<InsertUpdateNavigator>(dataManager) {

    var TAG = "InsertUpdateVM"
    var path: ObservableField<String?> = ObservableField("")

    fun setPath(value: String?) = this.path.set(value)

    fun add(text: String, price: String) {
        Logg.e(TAG, "text : $text")
        val todo = Item()
        todo.name = text
        todo.price = price
        todo.createdAt = System.currentTimeMillis().toString()
        todo.imageLocalPath = path.get()

        setIsLoading(true)
        val subscribe =
            dataManager.insertItem(todo).subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    {
                        Log.e(TAG, "on next value : $it")
                        todo.id = it
                    },
                    {

                        Log.e(TAG, "Error : " + it.message)
                    },
                    {
                        Log.e(TAG, ":completed ")
                        getNavigator()?.showMessage("Inserted successfully")
                        getNavigator()?.goBack()
                    }
                )


    }

}