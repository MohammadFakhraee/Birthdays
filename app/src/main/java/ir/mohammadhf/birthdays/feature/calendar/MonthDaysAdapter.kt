package ir.mohammadhf.birthdays.feature.calendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ir.mohammadhf.birthdays.R
import ir.mohammadhf.birthdays.core.bases.BaseListAdapter
import ir.mohammadhf.birthdays.core.bases.BaseViewHolder
import ir.mohammadhf.birthdays.data.model.CalendarDay
import ir.mohammadhf.birthdays.databinding.ItemCalendarDayBinding
import ir.mohammadhf.birthdays.utils.formatDay
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class MonthDaysAdapter @Inject constructor() :
    BaseListAdapter<CalendarDay, MonthDaysAdapter.CalendarDayViewHolder>() {

    var lastSelectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDayViewHolder =
        CalendarDayViewHolder(
            ItemCalendarDayBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    inner class CalendarDayViewHolder(private val itemCalendarDayBinding: ItemCalendarDayBinding) :
        BaseViewHolder<CalendarDay>(itemCalendarDayBinding.root) {
        override fun bind(item: CalendarDay) {
            itemCalendarDayBinding.root.let {
                it.text = item.ofMonth.formatDay()
                it.isGroupModeEnabled = item.isSelected || item.isToday
                it.isGroupSelected = item.isSelected

                val color = if (item.isInShownMonth) {
                    if (item.isSelected) Color.WHITE else Color.BLACK
                } else Color.LTGRAY
                it.setTextColor(color)

                if (item.hasEvent && !item.isSelected)
                    it.addBadge(ContextCompat.getColor(it.context, R.color.blue))
                else it.removeBadge()

                if (item.isSelected) lastSelectedPosition = adapterPosition

                it.setOnClickListener { _ ->
                    if (item.isInShownMonth) {
                        if (lastSelectedPosition >= 0) {
                            getItem(lastSelectedPosition).isSelected = false
                            notifyItemChanged(lastSelectedPosition)
                        }

                        item.isSelected = true
                        notifyItemChanged(adapterPosition)

                        EventBus.getDefault().post(item)
                    }
                }
            }
        }
    }
}