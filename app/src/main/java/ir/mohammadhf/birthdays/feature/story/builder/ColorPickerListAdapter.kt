package ir.mohammadhf.birthdays.feature.story.builder

import android.view.LayoutInflater
import android.view.ViewGroup
import ir.mohammadhf.birthdays.core.bases.BaseListAdapter
import ir.mohammadhf.birthdays.core.bases.BaseViewHolder
import ir.mohammadhf.birthdays.data.model.ColorItem
import ir.mohammadhf.birthdays.databinding.ItemAvatarBinding
import javax.inject.Inject

class ColorPickerListAdapter @Inject constructor() :
    BaseListAdapter<ColorItem, ColorPickerListAdapter.ColorPickerViewHolder>() {

    private var onItemSelectListener: ((item: ColorItem) -> Unit)? = null

    private var currentSelectedPos: Int? = null
    private var lastSelectedPos: Int? = null

    fun setOnItemSelectedListener(listener: (item: ColorItem) -> Unit) {
        this.onItemSelectListener = listener
    }

    fun changeItem(item: ColorItem) {
        currentList.forEachIndexed { index, color ->
            if (color == item) {
                lastSelectedPos = currentSelectedPos
                currentSelectedPos = index
                lastSelectedPos?.let { notifyItemChanged(it) }
                notifyItemChanged(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorPickerViewHolder =
        ColorPickerViewHolder(
            ItemAvatarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    inner class ColorPickerViewHolder(
        private val itemAvatarBinding: ItemAvatarBinding
    ) : BaseViewHolder<ColorItem>(itemAvatarBinding.root) {
        override fun bind(item: ColorItem) {
            itemAvatarBinding.root.apply {
                setColorFilter(item.color)

                borderWidth =
                    if (adapterPosition == currentSelectedPos) 6
                    else 0

                setOnClickListener { onItemSelectListener?.invoke(item) }
            }
        }
    }
}