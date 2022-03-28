package ir.mohammadhf.birthdays.feature.splash

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.mohammadhf.birthdays.core.bases.BaseFragment
import ir.mohammadhf.birthdays.databinding.FragmentSplashBinding

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSplashBinding =
        FragmentSplashBinding.inflate(inflater, container, false)

    override fun initial() {
        splashViewModel.initSetup(requireContext()) {
            findNavController().navigate(
                SplashFragmentDirections.actionSplashFragmentToPersonListFragment()
            )
        }
    }

    override fun subscribe() {
    }

    override fun isBottomNavShown(): Boolean = false

    companion object {
        const val NOTIFICATION_REQUEST_CODE = 1
    }
}