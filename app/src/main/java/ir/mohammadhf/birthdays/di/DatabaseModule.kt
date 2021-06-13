package ir.mohammadhf.birthdays.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.mohammadhf.birthdays.data.AppDatabase
import ir.mohammadhf.birthdays.data.model.GroupDao
import ir.mohammadhf.birthdays.data.model.PersonDao
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "birthday_db"
        ).build()

    @Provides
    @Singleton
    fun providePersonDao(appDatabase: AppDatabase): PersonDao =
        appDatabase.personDao()

    @Provides
    @Singleton
    fun provideGroupDao(appDatabase: AppDatabase): GroupDao =
        appDatabase.groupDao()
}