package ir.mohammadhf.birthdays.feature.birthdays.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ir.mohammadhf.birthdays.R
import ir.mohammadhf.birthdays.core.bases.BaseViewHolder
import ir.mohammadhf.birthdays.data.BirthdaySelectEvent
import ir.mohammadhf.birthdays.data.model.BirthdayMulti
import ir.mohammadhf.birthdays.data.model.BirthdayMulti.TYPES.TYPE_PERSON
import ir.mohammadhf.birthdays.data.model.BirthdayMulti.TYPES.TYPE_TITLE
import ir.mohammadhf.birthdays.data.model.BirthdayPerson
import ir.mohammadhf.birthdays.data.model.BirthdayTitle
import ir.mohammadhf.birthdays.databinding.ItemDateTitleBinding
import ir.mohammadhf.birthdays.databinding.ItemPersonBirthdayBinding
import ir.mohammadhf.birthdays.utils.ImageLoader
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class BirthdayListAdapter @Inject constructor(
    private val imageLoader: ImageLoader
) :
    ListAdapter<BirthdayMulti, RecyclerView.ViewHolder>(MultiViewDifUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_TITLE)
            return TitleViewHolder(
                ItemDateTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        else if (viewType == TYPE_PERSON)
            return PersonViewHolder(
                ItemPersonBirthdayBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        throw IllegalArgumentException("The viewType is not valid: $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TitleViewHolder) holder.bind(getItem(position) as BirthdayTitle)
        else if (holder is PersonViewHolder) holder.bind(getItem(position) as BirthdayPerson)
    }

    override fun getItemViewType(position: Int): Int =
        getItem(position).getType()

    inner class TitleViewHolder(
        private val itemDateTitleBinding: ItemDateTitleBinding
    ) : BaseViewHolder<BirthdayTitle>(itemDateTitleBinding.root) {

        override fun bind(item: BirthdayTitle) {
            itemDateTitleBinding.dateTitleTv.text = item.text
        }
    }

    inner class PersonViewHolder(
        private val itemPersonBirthdayBinding: ItemPersonBirthdayBinding
    ) : BaseViewHolder<BirthdayPerson>(itemPersonBirthdayBinding.root) {

        override fun bind(item: BirthdayPerson) {
            itemPersonBirthdayBinding.run {
                imageLoader.load(root.context, item.image, personImageCiv)

                nameTv.text = item.name

                descTv.text =
                    if (item.daysLeft == 0)
                        root.context.getString(R.string.today_is_the_day)
                    else {
                        if (item.daysLeft == 1)
                            if (item.age != null) {
                                root.context.getString(
                                    R.string.turns_age_day,
                                    item.age,
                                    item.daysLeft
                                )
                            } else {
                                root.context.getString(R.string.birthday_in_day, item.daysLeft)
                            }
                        else
                            if (item.age != null) {
                                root.context.getString(
                                    R.string.turns_age_days,
                                    item.age,
                                    item.daysLeft
                                )
                            } else {
                                root.context.getString(R.string.birthday_in_days, item.daysLeft)
                            }
                    }

                root.setOnClickListener {
                    EventBus.getDefault().post(BirthdaySelectEvent(item.id))
                }
            }
        }
    }
}

class MultiViewDifUtil : DiffUtil.ItemCallback<BirthdayMulti>() {
    override fun areItemsTheSame(oldItem: BirthdayMulti, newItem: BirthdayMulti): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(
        oldItem: BirthdayMulti,
        newItem: BirthdayMulti
    ): Boolean =
        oldItem.isEqualTo(newItem)
}