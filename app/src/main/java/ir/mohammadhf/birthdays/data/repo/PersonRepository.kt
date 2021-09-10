package ir.mohammadhf.birthdays.data.repo

import android.graphics.Bitmap
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ir.mohammadhf.birthdays.data.model.Person
import ir.mohammadhf.birthdays.data.model.PersonDao
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class PersonRepository @Inject constructor(private val personDao: PersonDao) {

    fun createPerson(
        person: Person,
        avatar: Bitmap,
        imageFile: File
    ): Single<Long> =
        if (saveAvatarToStorage(avatar, imageFile))
            personDao.insert(person.apply { avatarPath = imageFile.path })
        else Single.create { it.onError(Exception("Could not save avatar in storage")) }

    fun changePerson(
        person: Person,
        avatar: Bitmap,
        imageFile: File
    ): Completable =
        if (saveAvatarToStorage(avatar, imageFile))
            personDao.update(person.apply { avatarPath = imageFile.path })
        else Completable.create { it.onError(Exception("Could not save avatar in storage")) }

    fun getAllPersons(): Flowable<List<Person>> =
        personDao.getAll()

    fun deletePerson(person: Person): Completable =
        personDao.delete(person)

    fun getPerson(id: Long): Single<Person> =
        personDao.getById(id)

    fun getPersonByBirthDate(month: Int, day: Int): Single<List<Person>> =
        personDao.getByBirthdate(month, day)

    private fun saveAvatarToStorage(source: Bitmap, imageFile: File): Boolean =
        source.compress(Bitmap.CompressFormat.JPEG, 70, FileOutputStream(imageFile))
}

