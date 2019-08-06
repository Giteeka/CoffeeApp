package com.app.mycoffeeapp.di

import com.app.mycoffeeapp.ui.AddImageDialog
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 *
 * Created by admin on 8/6/2019.
 */
@Module
abstract class DialogModule {
    @ContributesAndroidInjector
    abstract fun provideAddImageDialog(): AddImageDialog

}