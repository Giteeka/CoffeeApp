package com.app.mycoffeeapp.data

import com.app.mycoffeeapp.data.local.AppDbHelper
import com.app.mycoffeeapp.data.local.DbHelper
import com.app.mycoffeeapp.data.model.Item
import io.reactivex.Observable
import javax.inject.Inject

/**
 *
 * Created by admin on 8/5/2019.
 */

class AppDataManager @Inject constructor(val dbHelper: DbHelper) : DataManager {

    override fun insertItem(item: Item): Observable<Long> {
        return dbHelper.insertItem(item)
    }

}