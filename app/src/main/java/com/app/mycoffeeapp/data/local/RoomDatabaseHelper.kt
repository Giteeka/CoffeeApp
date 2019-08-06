package com.app.mycoffeeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.mycoffeeapp.data.model.Item

/**
 *
 * Created by admin on 8/6/2019.
 */
@Database(entities = [Item::class], version = 2)
abstract class RoomDatabaseHelper : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}