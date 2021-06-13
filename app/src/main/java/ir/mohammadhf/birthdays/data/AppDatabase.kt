package ir.mohammadhf.birthdays.data

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.data.model.GroupDao
import ir.mohammadhf.birthdays.data.model.Person
import ir.mohammadhf.birthdays.data.model.PersonDao

@Database(entities = [Person::class, Group::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun personDao(): PersonDao

    abstract fun groupDao(): GroupDao
}