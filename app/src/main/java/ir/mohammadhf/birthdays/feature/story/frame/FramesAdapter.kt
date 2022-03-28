package ir.mohammadhf.birthdays.feature.story.frame

import android.view.LayoutInflater
import android.view.ViewGroup
import ir.mohammadhf.birthdays.core.bases.BaseListAdapter
import ir.mohammadhf.birthdays.core.bases.BaseViewHolder
import ir.mohammadhf.birthdays.data.FrameSelectEvent
import ir.mohammadhf.birthdays.data.model.StoryFrame
import ir.mohammadhf.birthdays.databinding.ItemFrameBinding
import ir.mohammadhf.birthdays.utils.ImageLoader
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class FramesAdapter @Inject constructor() :
    BaseListAdapter<StoryFrame, FramesAdapter.RawFrameViewHolder>() {
    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RawFrameViewHolder =
        RawFrameViewHolder(
            ItemFrameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    inner class RawFrameViewHolder(
        private val itemFrameBinding: ItemFrameBinding
    ) : BaseViewHolder<StoryFrame>(itemFrameBinding.root) {
        override fun bind(item: StoryFrame) {
            imageLoader.load(
                itemFrameBinding.root.context,
                item.previewPath,
                itemFrameBinding.frameIv
            )
            itemFrameBinding.root.setOnClickListener {
                EventBus.getDefault().post(FrameSelectEvent(item.id))
            }
        }
    }
}