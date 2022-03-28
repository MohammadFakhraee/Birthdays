package ir.mohammadhf.birthdays.data

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.mohammadhf.birthdays.data.local.GroupDao
import ir.mohammadhf.birthdays.data.local.PersonDao
import ir.mohammadhf.birthdays.data.local.StoryFrameDao
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.data.model.Person
import ir.mohammadhf.birthdays.data.model.StoryFrame

@Database(entities = [Person::class, Group::class, StoryFrame::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun personDao(): PersonDao

    abstract fun groupDao(): GroupDao

    abstract fun storyFrameDao(): StoryFrameDao
}