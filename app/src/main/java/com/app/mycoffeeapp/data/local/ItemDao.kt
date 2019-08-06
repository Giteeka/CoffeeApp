package com.app.mycoffeeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.app.mycoffeeapp.data.model.Item

/**
 *
 * Created by admin on 8/7/2019.
 */
@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Item) : Long?
}