package ir.mohammadhf.birthdays.feature.create.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import io.reactivex.subjects.BehaviorSubject
import ir.mohammadhf.birthdays.core.bases.BaseListAdapter
import ir.mohammadhf.birthdays.core.bases.BaseViewHolder
import ir.mohammadhf.birthdays.data.model.Avatar
import ir.mohammadhf.birthdays.databinding.ItemAvatarBinding

class AvatarListAdapter:
    BaseListAdapter<Avatar, AvatarListAdapter.AvatarViewHolder>() {
    val selectedAvatarBehaveSub = BehaviorSubject.create<Avatar>()

    private var currentSelectedPos: Int? = null
    private var lastSelectedPos: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder =
        AvatarViewHolder(
            ItemAvatarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    inner class AvatarViewHolder(
        private val itemAvatarBinding: ItemAvatarBinding
    ) : BaseViewHolder<Avatar>(itemAvatarBinding.root) {

        override fun bind(item: Avatar) {
            itemAvatarBinding.root.run {
                setImageResource(item.resId)

                borderWidth =
                    if (adapterPosition == currentSelectedPos) 2
                    else 0

                setOnClickListener {
                    selectedAvatarBehaveSub.onNext(item)

                    lastSelectedPos = currentSelectedPos
                    currentSelectedPos = adapterPosition
                    currentSelectedPos?.let { notifyItemChanged(it) }
                    lastSelectedPos?.let { notifyItemChanged(it) }
                }
            }


        }
    }
}