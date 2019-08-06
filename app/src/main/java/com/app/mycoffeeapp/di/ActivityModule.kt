package com.app.mycoffeeapp.di

import com.app.mycoffeeapp.ui.home.HomeActivity
import com.app.mycoffeeapp.ui.insertupdate.InsertTodoActivity
import com.app.mycoffeeapp.ui.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 *
 * Created by admin on 8/5/2019.
 */
@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun bindHomeActivity(): HomeActivity

    @ContributesAndroidInjector
    abstract fun bindInsertItemActivity(): InsertTodoActivity
}