package ir.mohammadhf.birthdays.data.local

import io.reactivex.Single
import ir.mohammadhf.birthdays.R
import ir.mohammadhf.birthdays.data.model.Avatar
import javax.inject.Inject

class AvatarLocalDataSource @Inject constructor() {

    fun getAvatars(): Single<ArrayList<Avatar>> =
        Single.create {
            if (!it.isDisposed) {
                val avatars = arrayListOf(
                    Avatar(R.drawable.ic_avatar_1, "Male_1"),
                    Avatar(R.drawable.ic_avatar_2, "Female_1"),
                    Avatar(R.drawable.ic_avatar_3, "Male_2"),
                    Avatar(R.drawable.ic_avatar_4, "Female_2"),
                    Avatar(R.drawable.ic_avatar_5, "Female_3"),
                    Avatar(R.drawable.ic_avatar_6, "Male_3"),
                    Avatar(R.drawable.ic_avatar_7, "Baby_1"),
                    Avatar(R.drawable.ic_avatar_8, "Baby_2")
                )
                it.onSuccess(avatars)
            }
        }
}