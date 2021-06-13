package ir.mohammadhf.birthdays.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object SharedPrefModule {

    @Provides
    @ActivityScoped
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("initial_pref", Context.MODE_PRIVATE)
}