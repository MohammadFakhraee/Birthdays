package ir.mohammadhf.birthdays.data.model

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface PersonDao {

    @Insert
    fun insert(person: Person): Single<Long>

    @Update
    fun update(person: Person): Completable

    @Delete
    fun delete(person: Person): Completable

    @Query("SELECT * FROM persons ORDER BY id DESC")
    fun getAll(): Flowable<List<Person>>

    @Query("SELECT * FROM persons WHERE id = :personId")
    fun getById(personId: Long): Single<Person>

    @Query("SELECT * FROM persons WHERE birthdayMonth == :month AND birthdayDay == :day")
    fun getByBirthdate(month: Int, day: Int): Single<List<Person>>
}