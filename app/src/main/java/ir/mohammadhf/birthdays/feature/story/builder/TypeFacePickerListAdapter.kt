package ir.mohammadhf.birthdays.feature.story.builder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import ir.mohammadhf.birthdays.core.bases.BaseListAdapter
import ir.mohammadhf.birthdays.core.bases.BaseViewHolder
import ir.mohammadhf.birthdays.data.model.TypeFaceItem
import ir.mohammadhf.birthdays.databinding.ItemTypeFaceBinding
import javax.inject.Inject

class TypeFacePickerListAdapter @Inject constructor() :
    BaseListAdapter<TypeFaceItem, TypeFacePickerListAdapter.TypeFaceViewHolder>() {

    private var onItemSelectListener: ((item: TypeFaceItem) -> Unit)? = null

    private var currentSelectedPos: Int? = null
    private var lastSelectedPos: Int? = null

    fun setOnItemSelectedListener(listener: (item: TypeFaceItem) -> Unit) {
        this.onItemSelectListener = listener
    }

    fun changeItem(item: TypeFaceItem) {
        currentList.forEachIndexed { index, color ->
            if (color == item) {
                lastSelectedPos = currentSelectedPos
                currentSelectedPos = index
                lastSelectedPos?.let { notifyItemChanged(it) }
                notifyItemChanged(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeFaceViewHolder =
        TypeFaceViewHolder(
            ItemTypeFaceBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    inner class TypeFaceViewHolder(
        private val itemTypeFaceBinding: ItemTypeFaceBinding
    ) : BaseViewHolder<TypeFaceItem>(itemTypeFaceBinding.root) {

        override fun bind(item: TypeFaceItem) {
            itemTypeFaceBinding.circleImageView.setColorFilter(
                if (adapterPosition == currentSelectedPos) Color.BLACK else Color.GRAY
            )

            itemTypeFaceBinding.fontPrevTextView.typeface = item.typeFace

            itemTypeFaceBinding.root.setOnClickListener { onItemSelectListener?.invoke(item) }
        }
    }
}