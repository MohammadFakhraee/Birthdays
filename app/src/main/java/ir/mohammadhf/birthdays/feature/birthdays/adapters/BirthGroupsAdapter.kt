package ir.mohammadhf.birthdays.feature.birthdays.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import ir.mohammadhf.birthdays.core.bases.BaseListAdapter
import ir.mohammadhf.birthdays.core.bases.BaseViewHolder
import ir.mohammadhf.birthdays.data.GroupListSelectEvent
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.databinding.ItemGroupBinding
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class BirthGroupsAdapter @Inject constructor() :
    BaseListAdapter<Group, BirthGroupsAdapter.BirthGroupViewHolder>() {

    private val selectedGroups = arrayListOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirthGroupViewHolder =
        BirthGroupViewHolder(
            ItemGroupBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    inner class BirthGroupViewHolder(
        private val itemGroupBinding: ItemGroupBinding
    ) : BaseViewHolder<Group>(itemGroupBinding.root) {

        override fun bind(item: Group) {
            itemGroupBinding.root.let {
                it.groupColor = item.color
                it.isGroupSelected = false
                it.text = item.name
                it.setTextColor(Color.BLACK)

                it.setOnClickListener { _ ->
                    it.isGroupSelected = !it.isGroupSelected
                    it.setTextColor(if (it.isGroupSelected) Color.WHITE else Color.BLACK)

                    val selectedGroupsEvent = GroupListSelectEvent(
                        selectedGroups.apply {
                            if (it.isGroupSelected) add(item.id)
                            else remove(item.id)
                        }
                    )
                    EventBus.getDefault().post(selectedGroupsEvent)
                }
            }
        }
    }
}