package ir.mohammadhf.birthdays.feature.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.mohammadhf.birthdays.core.bases.BaseFragment
import ir.mohammadhf.birthdays.databinding.FragmentSettingBinding

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingBinding>() {

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingBinding =
        FragmentSettingBinding.inflate(inflater, container, false)

    override fun initial() {
        requireBinding {
            manageGroupsTv.setOnClickListener {
                findNavController().navigate(
                    SettingsFragmentDirections.actionSettingsFragmentToGroupsMangerFragment()
                )
            }

            backIb.setOnClickListener { findNavController().popBackStack() }
        }
    }

    override fun subscribe() {}

    override fun isBottomNavShown(): Boolean = false
}