package ir.mohammadhf.birthdays.feature.gift

import android.view.LayoutInflater
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import ir.mohammadhf.birthdays.core.bases.BaseFragment
import ir.mohammadhf.birthdays.databinding.FragmentGiftBinding

@AndroidEntryPoint
class GiftFragment : BaseFragment<FragmentGiftBinding>() {
    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGiftBinding =
        FragmentGiftBinding.inflate(inflater, container, false)

    override fun initial() {
    }

    override fun subscribe() {
    }

    override fun isBottomNavShown(): Boolean = true
}