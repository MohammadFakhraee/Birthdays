package ir.mohammadhf.birthdays.core.bases

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(rootView: View) :
    RecyclerView.ViewHolder(rootView) {

    abstract fun bind(item: T)
}