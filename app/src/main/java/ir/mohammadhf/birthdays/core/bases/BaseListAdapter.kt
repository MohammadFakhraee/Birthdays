package ir.mohammadhf.birthdays.core.bases

import androidx.recyclerview.widget.ListAdapter

abstract class BaseListAdapter<T, VH : BaseViewHolder<T>>
    : ListAdapter<T, VH>(BaseDiffUtil<T>()) {

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position))
}