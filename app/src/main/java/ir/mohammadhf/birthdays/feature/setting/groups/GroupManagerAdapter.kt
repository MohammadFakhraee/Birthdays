package ir.mohammadhf.birthdays.feature.setting.groups

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import io.reactivex.subjects.BehaviorSubject
import ir.mohammadhf.birthdays.core.bases.BaseListAdapter
import ir.mohammadhf.birthdays.core.bases.BaseViewHolder
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.databinding.ItemGroupsManagerBinding
import javax.inject.Inject

class GroupManagerAdapter @Inject constructor() :
    BaseListAdapter<Group, GroupManagerAdapter.GroupManagerViewHolder>() {
    val deleteGroupSelectBS = BehaviorSubject.create<Group>()
    val editGroupSelectBS = BehaviorSubject.create<Group>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupManagerViewHolder =
        GroupManagerViewHolder(
            ItemGroupsManagerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    inner class GroupManagerViewHolder(private val itemGroupManageBinding: ItemGroupsManagerBinding) :
        BaseViewHolder<Group>(itemGroupManageBinding.root) {

        override fun bind(item: Group) {
            itemGroupManageBinding.apply {
                groupView.text = item.name
                groupView.groupColor = item.color
                groupView.setTextColor(Color.WHITE)

                deleteIb.setOnClickListener {
                    deleteGroupSelectBS.onNext(item)
                }

                groupView.setOnClickListener {
                    editGroupSelectBS.onNext(item)
                }
            }
        }
    }
}