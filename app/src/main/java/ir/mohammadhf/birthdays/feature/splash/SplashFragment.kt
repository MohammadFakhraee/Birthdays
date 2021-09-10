package ir.mohammadhf.birthdays.feature.splash

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ir.mohammadhf.birthdays.R
import ir.mohammadhf.birthdays.core.bases.BaseFragment
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.databinding.FragmentSplashBinding
import ir.mohammadhf.birthdays.feature.notify.setTheAlarm

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSplashBinding =
        FragmentSplashBinding.inflate(inflater, container, false)

    override fun initial() {
        splashViewModel.settingAlarm()
    }

    override fun subscribe() {
        compositeDisposable.add(
            splashViewModel.saveGroups(getGroups())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    findNavController().navigate(
                        SplashFragmentDirections
                            .actionSplashFragmentToPersonListFragment()
                    )
                }
        )

        compositeDisposable.add(
            splashViewModel.onSettingAlarmBehaveSub
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it) setTheAlarm(requireContext(), false)
                }
        )
    }

    private fun getGroups(): Array<Group> =
        arrayOf(
            Group(
                0L,
                getString(R.string.family),
                ContextCompat.getColor(requireContext(), R.color.group_blue_light)
            ),
            Group(
                0L,
                getString(R.string.friends),
                ContextCompat.getColor(requireContext(), R.color.group_yellow)
            ),
            Group(
                0L,
                getString(R.string.work),
                ContextCompat.getColor(requireContext(), R.color.group_red_dark)
            )
        )

    override fun isBottomNavShown(): Boolean = false

    companion object {
        const val NOTIFICATION_REQUEST_CODE = 1
    }
}