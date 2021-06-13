package ir.mohammadhf.birthdays.feature.profile

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ir.mohammadhf.birthdays.R
import ir.mohammadhf.birthdays.core.bases.BaseFragment
import ir.mohammadhf.birthdays.databinding.FragmentPersonProfileBinding
import ir.mohammadhf.birthdays.utils.DateManager
import ir.mohammadhf.birthdays.utils.ImageLoader
import javax.inject.Inject

@AndroidEntryPoint
class PersonProfileFragment : BaseFragment<FragmentPersonProfileBinding>() {
    private val personProfileViewModel: PersonProfileViewModel by viewModels()

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var dateManager: DateManager

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPersonProfileBinding =
        FragmentPersonProfileBinding.inflate(inflater, container, false)

    override fun initial() {
        val args: PersonProfileFragmentArgs by navArgs()
        personProfileViewModel.run {
            getPerson(args.personId)
        }

        requireBinding {
            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            editTv.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    PersonProfileFragmentDirections.actionPersonProfileFragmentToCreatePersonFragment(
                        args.personId
                    )
                )
            )

            deleteTv.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle(
                        getString(
                            R.string.delete_title,
                            personProfileViewModel.personBehaveSub.value!!.name
                        )
                    )
                    .setMessage(getString(R.string.delete_message))
                    .setPositiveButton(getString(R.string.delete_accept))
                    { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        compositeDisposable.add(
                            personProfileViewModel.deleteUser()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    findNavController().popBackStack()
                                }
                        )
                    }.setNegativeButton(getString(R.string.delete_reject))
                    { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }.show()
            }
        }
    }

    override fun subscribe() {
        compositeDisposable.add(
            personProfileViewModel.personBehaveSub
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    requireBinding {
                        imageLoader.load(requireContext(), it.avatarPath, circleImageView)
                        nameTv.text = it.name
                        birthdayDateTv.text = dateManager.stringFormattedDate(
                            intArrayOf(
                                it.birthdayYear?.apply { } ?: 0,
                                it.birthdayMonth,
                                it.birthdayDay
                            )
                        )

                        val notifyYear = if (it.isAfterToday(dateManager.getTodayDateArray()))
                            dateManager.getTodayDateArray()[0]
                        else dateManager.getTodayDateArray()[0] + 1
                        alarmTv.text = dateManager.stringFormattedDate(
                            intArrayOf(
                                notifyYear,
                                it.notifyMonth,
                                it.notifyDay
                            )
                        )
                    }
                }
        )

        compositeDisposable.add(
            personProfileViewModel.groupBehaveSub
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    requireBinding {
                        groupGv.setColor(it.color)
                        groupGv.text = it.name
                    }
                }
        )

        compositeDisposable.add(
            personProfileViewModel.counterBehaveSub
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    requireBinding {
                        timeCounterTv.text = it
                    }
                }
        )

        compositeDisposable.add(
            personProfileViewModel.onBirthdayBehaveSub
                .subscribe {
                    requireBinding {
                        if (it) {
                            timeCounterTv.visibility = GONE
                            timeTemplateTv.text = getString(R.string.today_is_the_day)
                        } else {
                            timeCounterTv.visibility = VISIBLE
                            timeTemplateTv.text = getString(R.string.birthday_counter_template)
                        }
                    }
                }
        )
    }
}