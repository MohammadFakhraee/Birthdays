package ir.mohammadhf.birthdays.feature.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import ir.mohammadhf.birthdays.core.bases.BaseFragment
import ir.mohammadhf.birthdays.data.model.CalendarDay
import ir.mohammadhf.birthdays.databinding.FragmentCalendarBinding
import ir.mohammadhf.birthdays.feature.birthdays.adapters.BirthdayListAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
class CalendarFragment : BaseFragment<FragmentCalendarBinding>() {
    private val calendarViewModel: CalendarViewModel by viewModels()

    @Inject
    lateinit var monthDaysAdapter: MonthDaysAdapter

    @Inject
    lateinit var birthdayListAdapter: BirthdayListAdapter

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCalendarBinding =
        FragmentCalendarBinding.inflate(inflater, container, false)

    override fun initial() {
        calendarViewModel.loadThisMonth()

        requireBinding {
            monthlyRv.let {
                it.layoutManager = GridLayoutManager(
                    requireContext(), 7, RecyclerView.VERTICAL, false
                )
                it.adapter = monthDaysAdapter
            }

            birthListRv.adapter = birthdayListAdapter

            nextMonthIb.setOnClickListener { calendarViewModel.loadNextMonth() }

            prevMonthIb.setOnClickListener { calendarViewModel.loadPrevMonth() }
        }
    }

    override fun subscribe() {
        compositeDisposable.addAll(
            calendarViewModel.monthTitleBS.observeOn(AndroidSchedulers.mainThread())
                .subscribe { requireBinding { monthAndYearTv.text = it } },

            calendarViewModel.monthDaysListBS.observeOn(AndroidSchedulers.mainThread())
                .subscribe { monthDaysAdapter.submitList(it) },

            calendarViewModel.birthdaysBS.observeOn(AndroidSchedulers.mainThread())
                .subscribe { birthdaysList ->
                    requireBinding {
                        if (birthdaysList.isEmpty()) {
                            emptyStateLl.visibility = VISIBLE
                            birthListRv.visibility = GONE
                        } else {
                            emptyStateLl.visibility = GONE
                            birthListRv.visibility = VISIBLE
                        }
                        birthdayListAdapter.submitList(birthdaysList)
                    }
                }
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun calendarDaySelected(calendarDay: CalendarDay) {
        calendarViewModel.onUserDateSelect(calendarDay)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        EventBus.getDefault().register(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    override fun isBottomNavShown(): Boolean = true
}