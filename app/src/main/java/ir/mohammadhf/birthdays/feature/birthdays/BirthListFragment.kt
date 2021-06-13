package ir.mohammadhf.birthdays.feature.birthdays

import android.view.LayoutInflater
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
import ir.mohammadhf.birthdays.databinding.FragmentBirthListBinding
import ir.mohammadhf.birthdays.feature.birthdays.adapters.BirthGroupsAdapter
import ir.mohammadhf.birthdays.feature.birthdays.adapters.BirthdayListAdapter

@AndroidEntryPoint
class BirthListFragment : BaseFragment<FragmentBirthListBinding>() {
    private val birthListViewModel: BirthListViewModel by viewModels()

    private lateinit var birthAdapter: BirthdayListAdapter
    private lateinit var groupsAdapter: BirthGroupsAdapter

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBirthListBinding =
        FragmentBirthListBinding.inflate(inflater, container, false)

    override fun initial() {
        getPersonsWithFilter(ArrayList())
        birthListViewModel.getGroupList()

        birthAdapter = BirthdayListAdapter()
        groupsAdapter = BirthGroupsAdapter()

        requireBinding {
            birthdaysRv.adapter = birthAdapter
            groupsRv.adapter = groupsAdapter

            addFab.setOnClickListener {
                findNavController().navigate(
                    BirthListFragmentDirections.actionPersonListFragmentToCreatePersonFragment()
                )
            }

            filterIv.setOnClickListener {
                val bool = !groupsRv.isVisible
                groupsRv.isVisible = bool

                filterIv.setImageResource(
                    if (bool) R.drawable.ic_filter_fill_32
                    else R.drawable.ic_filter_empty_32
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
                            animation.duration = 300
                            animation.repeatMode = Animation.REVERSE
                            animation.repeatCount =
                                if (arrowDownIv.isVisible) Animation.INFINITE
                                else 0
                            animation.interpolator = LinearInterpolator()
                            arrowDownIv.startAnimation(animation)
                        }
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

        compositeDisposable.add(
            birthAdapter.selectedPersonBehaveSub
                .subscribe {
                    findNavController().navigate(
                        BirthListFragmentDirections
                            .actionPersonListFragmentToPersonProfileFragment(it.id)
                    )
                }
        )

        compositeDisposable.add(
            groupsAdapter.groupSelectedBehaveSub
                .subscribe { getPersonsWithFilter(it) }
        )
    }

    private fun getPersonsWithFilter(filterID: ArrayList<Long>) {
        birthListViewModel.getPersonList(filterID)
    }
}