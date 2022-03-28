package ir.mohammadhf.birthdays.data.repo

import io.reactivex.Single
import ir.mohammadhf.birthdays.data.local.AvatarLocalDataSource
import ir.mohammadhf.birthdays.data.model.Avatar
import javax.inject.Inject

class AvatarRepository @Inject constructor(
    private val avatarLocalDataSource: AvatarLocalDataSource
) {

    fun getList(): Single<ArrayList<Avatar>> =
        avatarLocalDataSource.getAvatars()
}