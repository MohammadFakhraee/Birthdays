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
import ir.mohammadhf.birthdays.core.InitialSharedPreferences
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.databinding.FragmentSplashBinding
import ir.mohammadhf.birthdays.feature.receiver.setTheAlarm
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    private val splashViewModel: SplashViewModel by viewModels()

    @Inject
    lateinit var initialSharedPreferences: InitialSharedPreferences

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSplashBinding =
        FragmentSplashBinding.inflate(inflater, container, false)

    override fun initial() {
        if (!initialSharedPreferences.isAlarmSet()) {
            setTheAlarm(requireContext())
            initialSharedPreferences.setAlarmSet(true)
        }
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

    companion object {
        const val NOTIFICATION_REQUEST_CODE = 1
    }
}