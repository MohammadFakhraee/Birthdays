package ir.mohammadhf.birthdays.core.bases

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

// CAUTION: Make sure T class is a data class!
class BaseDiffUtil<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem == newItem

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem == newItem
}