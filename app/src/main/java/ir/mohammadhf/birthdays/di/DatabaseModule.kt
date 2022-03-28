package ir.mohammadhf.birthdays.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.mohammadhf.birthdays.data.AppDatabase
import ir.mohammadhf.birthdays.data.local.GroupDao
import ir.mohammadhf.birthdays.data.local.PersonDao
import ir.mohammadhf.birthdays.data.local.StoryFrameDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "birthday_db"
        ).build()

    @Singleton
    @Provides
    fun providePersonDao(appDatabase: AppDatabase): PersonDao =
        appDatabase.personDao()

    @Singleton
    @Provides
    fun provideGroupDao(appDatabase: AppDatabase): GroupDao =
        appDatabase.groupDao()

    @Singleton
    @Provides
    fun provideStoryFrameDao(appDatabase: AppDatabase): StoryFrameDao =
        appDatabase.storyFrameDao()
}