package ir.mohammadhf.birthdays.feature.story.frame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ir.mohammadhf.birthdays.core.bases.BaseFragment
import ir.mohammadhf.birthdays.data.FrameSelectEvent
import ir.mohammadhf.birthdays.databinding.FragmentFramesBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
class FramesFragment : BaseFragment<FragmentFramesBinding>() {
    private val framesViewModel: FramesViewModel by viewModels()

    @Inject
    lateinit var framesAdapter: FramesAdapter

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFramesBinding =
        FragmentFramesBinding.inflate(inflater, container, false)

    override fun initial() {
        framesViewModel.getFramesByCategory("birthday") { giftFrames ->
            framesAdapter.submitList(giftFrames)
        }

        requireBinding {
            giftListRv.adapter = framesAdapter
            giftListRv.layoutManager = GridLayoutManager(
                requireContext(),
                2,
                RecyclerView.VERTICAL,
                false
            )
        }
    }

    override fun subscribe() {
    }

    override fun isBottomNavShown(): Boolean = true

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFrameSelected(frameSelectEvent: FrameSelectEvent) {
        findNavController().navigate(
            FramesFragmentDirections.actionGiftFragmentToStoryBuilderFragment(frameSelectEvent.id)
        )
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
}