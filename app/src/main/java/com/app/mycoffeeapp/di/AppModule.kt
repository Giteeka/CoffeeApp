package com.app.mycoffeeapp.di

import android.app.Application
import android.content.Context
import com.app.mycoffeeapp.data.AppDataManager
import com.app.mycoffeeapp.data.DataManager
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
    fun provideDataManger(): DataManager {
        return AppDataManager();
    }


}