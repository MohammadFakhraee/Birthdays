package ir.mohammadhf.birthdays.feature.create.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import io.reactivex.subjects.BehaviorSubject
import ir.mohammadhf.birthdays.core.bases.BaseListAdapter
import ir.mohammadhf.birthdays.core.bases.BaseViewHolder
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.databinding.ItemGroupBinding

class CreatePersonGroupAdapter :
    BaseListAdapter<Group, CreatePersonGroupAdapter.GroupViewModel>() {
    val selectedGroupBehaveSub = BehaviorSubject.create<Long>()

    private var currentSelectedPos: Int? = null
    private var prevSelectedPos: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewModel =
        GroupViewModel(
            ItemGroupBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    fun selectGroupById(groupId: Long) {
        selectedGroupBehaveSub.onNext(groupId)

        if (itemCount > 0)
            for (i in 0 until itemCount) if (getItem(i).id == groupId) notifyItemChanged(i)
    }

    inner class GroupViewModel(
        private val itemGroupBinding: ItemGroupBinding
    ) : BaseViewHolder<Group>(itemGroupBinding.root) {

        override fun bind(item: Group) {
            itemGroupBinding.root.let {
                it.groupColor = item.color
                it.text = item.name

                val isSelected = adapterPosition == currentSelectedPos
                it.isGroupSelected = isSelected
                it.setTextColor(if (isSelected) Color.WHITE else Color.BLACK)

                selectedGroupBehaveSub.value?.let { id ->
                    val bool = id == item.id
                    it.isGroupSelected = bool
                    if (bool) currentSelectedPos = adapterPosition
                }

                it.setOnClickListener {
                    selectedGroupBehaveSub.onNext(item.id)

                    prevSelectedPos = currentSelectedPos
                    currentSelectedPos = adapterPosition
                    prevSelectedPos?.let { prev -> notifyItemChanged(prev) }
                    currentSelectedPos?.let { curr -> notifyItemChanged(curr) }
                }
            }
        }
    }
}