package ir.mohammadhf.birthdays.feature.birthdays.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import io.reactivex.subjects.BehaviorSubject
import ir.mohammadhf.birthdays.core.bases.BaseListAdapter
import ir.mohammadhf.birthdays.core.bases.BaseViewHolder
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.databinding.ItemGroupBinding

class BirthGroupsAdapter: BaseListAdapter<Group, BirthGroupsAdapter.BirthGroupViewHolder>() {
    val groupSelectedBehaveSub = BehaviorSubject.create<ArrayList<Long>>()

    init {
        groupSelectedBehaveSub.onNext(ArrayList())
    }

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
            itemGroupBinding.root.run {
                setColor(item.color)
                selectGroup(false)
                text = item.name

                setOnClickListener {
                    selectGroup(!isGroupSelected())

                    groupSelectedBehaveSub.onNext(
                        groupSelectedBehaveSub.value!!.apply {
                            if (isGroupSelected()) add(item.id)
                            else remove(item.id)
                        }
                    )
                }
            }
        }
    }
}