package ir.mohammadhf.birthdays.data.repo

import io.reactivex.Completable
import io.reactivex.Single
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.data.model.GroupDao
import javax.inject.Inject

class GroupRepository @Inject constructor(private val groupDao: GroupDao) {

    fun getAll(): Single<List<Group>> = groupDao.getAll()

    fun getById(groupId: Long): Single<Group> = groupDao.getById(groupId)

    fun saveGroups(groups: Array<Group>): Completable =
        groupDao.insertAll(groups[0], groups[1], groups[2])
}