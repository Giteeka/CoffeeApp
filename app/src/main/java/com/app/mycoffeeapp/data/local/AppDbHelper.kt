package com.app.mycoffeeapp.data.local

import com.app.mycoffeeapp.data.model.Item
import io.reactivex.Observable
import javax.inject.Inject

/**
 *
 * Created by admin on 8/7/2019.
 */
class AppDbHelper(@set:Inject var databaseHelper: RoomDatabaseHelper) : DbHelper {
    override fun insertItem(item: Item): Observable<Long> {
        return Observable.fromCallable {
            return@fromCallable databaseHelper.itemDao().insert(item)
        }
    }
}