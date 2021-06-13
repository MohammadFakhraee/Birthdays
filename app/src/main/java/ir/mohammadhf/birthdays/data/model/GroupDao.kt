package ir.mohammadhf.birthdays.data.model

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface GroupDao {

    @Insert
    fun insertOne(group: Group): Single<Long>

    @Insert
    fun insertAll(vararg group: Group): Completable

    @Update
    fun update(group: Group): Completable

    @Delete
    fun delete(group: Group): Completable

    @Query("SELECT * FROM groups")
    fun getAll(): Single<List<Group>>

    @Query("SELECT * FROM groups WHERE id = :groupId")
    fun getById(groupId: Long): Single<Group>
}