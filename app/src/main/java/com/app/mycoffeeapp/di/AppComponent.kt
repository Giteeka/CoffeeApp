package com.app.mycoffeeapp.di

import android.app.Application
import com.app.mycoffeeapp.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

/**
 *
 * Created by admin on 8/5/2019.
 */

@Singleton
@Component(modules = [AppModule::class, AndroidInjectionModule::class, ActivityModule::class])
interface AppComponent {

    fun inject(app: App)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application) : Builder


        fun build(): AppComponent
    }

}