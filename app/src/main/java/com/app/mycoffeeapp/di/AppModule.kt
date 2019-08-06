package com.app.mycoffeeapp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.app.mycoffeeapp.data.AppDataManager
import com.app.mycoffeeapp.data.DataManager
import com.app.mycoffeeapp.data.local.AppDbHelper
import com.app.mycoffeeapp.data.local.DbHelper
import com.app.mycoffeeapp.data.local.RoomDatabaseHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 *
 * Created by admin on 8/5/2019.
 */

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application;
    }

    @Provides
    @Singleton
    fun provideDataManger(dbHelper: DbHelper): DataManager {
        return AppDataManager(dbHelper);
    }

    @Provides
    @Singleton
    fun provideDbHelper(roomDatabaseHelper: RoomDatabaseHelper): DbHelper {
        return AppDbHelper(roomDatabaseHelper)
    }

    @Provides
    @DatabaseInfo
    fun provideDbName(): String {
        return "coffee-database"
    }


    @Provides
    @Singleton
    fun provideDatbase(context: Context, @DatabaseInfo name: String): RoomDatabaseHelper {
        return Room.databaseBuilder(context, RoomDatabaseHelper::class.java, name).fallbackToDestructiveMigration()
            .build()
    }


}