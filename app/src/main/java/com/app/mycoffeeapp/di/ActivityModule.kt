package com.app.mycoffeeapp.di

import com.app.mycoffeeapp.ui.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 *
 * Created by admin on 8/5/2019.
 */
@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun bindSplashActivity() : SplashActivity
}