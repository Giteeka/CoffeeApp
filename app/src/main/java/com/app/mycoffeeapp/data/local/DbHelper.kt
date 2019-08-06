package com.app.mycoffeeapp.data.local

import com.app.mycoffeeapp.data.model.Item
import io.reactivex.Observable

/**
 *
 * Created by admin on 8/7/2019.
 */
interface DbHelper {
  fun insertItem(item: Item) : Observable<Long>
}