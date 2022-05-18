package ir.mohammadhf.birthdays.feature.birthdays

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import ir.mohammadhf.birthdays.R
import ir.mohammadhf.birthdays.core.bases.BaseFragment
import ir.mohammadhf.birthdays.data.BirthdaySelectEvent
import ir.mohammadhf.birthdays.data.GroupListSelectEvent
import ir.mohammadhf.birthdays.databinding.FragmentBirthListBinding
import ir.mohammadhf.birthdays.feature.birthdays.adapters.BirthGroupsAdapter
import ir.mohammadhf.birthdays.feature.birthdays.adapters.BirthdayListAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
class BirthListFragment : BaseFragment<FragmentBirthListBinding>() {
    private val birthListViewModel: BirthListViewModel by viewModels()

    @Inject
    lateinit var birthAdapter: BirthdayListAdapter

    @Inject
    lateinit var groupsAdapter: BirthGroupsAdapter

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBirthListBinding =
        FragmentBirthListBinding.inflate(inflater, container, false)

    override fun initial() {
        if (!birthListViewModel.isInitialDone())
            findNavController().navigate(
                BirthListFragmentDirections.actionPersonListFragmentToSplashFragment()
            )

        getPersonsWithFilter(ArrayList())
        birthListViewModel.getGroupList()

        requireBinding {
            birthdaysRv.adapter = birthAdapter
            groupsRv.adapter = groupsAdapter

            addFab.setOnClickListener {
                findNavController().navigate(
                    BirthListFragmentDirections.actionPersonListFragmentToCreatePersonFragment()
                )
            }

            filterIb.setOnClickListener {
                val bool = !groupsRv.isVisible
                groupsRv.isVisible = bool

                filterIb.setImageResource(
                    if (bool) R.drawable.ic_baseline_filter_32
                    else R.drawable.ic_outline_filter_32
                )
            }

            settingIb.setOnClickListener {
                findNavController().navigate(
                    BirthListFragmentDirections.actionGlobalToSetting()
                )
            }
        }
    }

    override fun subscribe() {
        compositeDisposable.add(
            birthListViewModel.personListBS
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    birthAdapter.submitList(it)

                    requireBinding {
                        val bool = it.isEmpty()
                        birthdaysRv.visibility = if (bool) GONE else VISIBLE
                        emptyStateLl.visibility = if (bool) VISIBLE else GONE
                        arrowDownIv.visibility = if (bool) VISIBLE else GONE

                        if (bool) {
                            val animation = TranslateAnimation(
                                Animation.ABSOLUTE, 0f,
                                Animation.ABSOLUTE, 0f,
                                Animation.RELATIVE_TO_SELF, -0.5f,
                                Animation.RELATIVE_TO_SELF, 0f
                            )
                            animation.duration = 400
                            animation.repeatMode = Animation.REVERSE
                            animation.repeatCount =
                                if (bool) Animation.INFINITE
                                else 0
                            animation.interpolator = LinearInterpolator()
                            arrowDownIv.startAnimation(animation)
                        } else arrowDownIv.clearAnimation()
                    }
                }
        )

        compositeDisposable.add(
            birthListViewModel.groupListBS
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    groupsAdapter.submitList(it)
                }
        )

        compositeDisposable.add(
            birthListViewModel.loadingPersonsBS
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    requireBinding {
                        shimmerFl.visibility = if (it) VISIBLE else GONE
                    }
                }
        )
    }

    private fun getPersonsWithFilter(filterID: ArrayList<Long>) {
        birthListViewModel.getPersonList(filterID)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBirthdaySelectEvent(birthdaySelectEvent: BirthdaySelectEvent) {
        findNavController().navigate(
            BirthListFragmentDirections
                .actionPersonListFragmentToPersonProfileFragment(birthdaySelectEvent.id)
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGroupListSelectEvent(groupListSelectEvent: GroupListSelectEvent) {
        getPersonsWithFilter(groupListSelectEvent.groups)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        EventBus.getDefault().register(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun isBottomNavShown(): Boolean = true

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }
}