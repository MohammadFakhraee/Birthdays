package ir.mohammadhf.birthdays.data.repo

import io.reactivex.Completable
import io.reactivex.Single
import ir.mohammadhf.birthdays.data.local.GroupDao
import ir.mohammadhf.birthdays.data.model.Group
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GroupRepository @Inject constructor(private val groupDao: GroupDao) {

    fun getAll(): Single<List<Group>> = groupDao.getAll()

    fun getById(groupId: Long): Single<Group> = groupDao.getById(groupId)

    suspend fun saveGroups(groups: List<Group>) {
        // Simulate loading page
        delay(2000L)
        withContext(Dispatchers.IO) { groupDao.insertAll(groups[0], groups[1], groups[2]) }
    }

    fun insertOneGroup(group: Group): Single<Long> =
        groupDao.insertOne(group)

    fun updateGroup(group: Group): Completable =
        groupDao.update(group)

    fun deleteGroup(group: Group): Completable =
        groupDao.delete(group)
}
