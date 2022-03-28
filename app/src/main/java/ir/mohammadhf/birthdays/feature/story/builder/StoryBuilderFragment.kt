package ir.mohammadhf.birthdays.feature.story.builder

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ir.mohammadhf.birthdays.core.bases.BaseFragment
import ir.mohammadhf.birthdays.databinding.FragmentStoryBuilderBinding
import ir.mohammadhf.birthdays.feature.story.builder.StoryBuildViewModel.StoryUiState.*
import ir.mohammadhf.birthdays.feature.story.layouts.PicLayout
import ir.mohammadhf.birthdays.feature.story.layouts.StickerLayout
import ir.mohammadhf.birthdays.feature.story.layouts.TextLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class StoryBuilderFragment : BaseFragment<FragmentStoryBuilderBinding>() {
    private val storyBuilderViewModel: StoryBuildViewModel by viewModels()

    private val args: StoryBuilderFragmentArgs by navArgs()

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentStoryBuilderBinding =
        FragmentStoryBuilderBinding.inflate(inflater, container, false)

    override fun initial() {
        storyBuilderViewModel

        requireBinding {
            // Whenever user touches these icons, ui will tell its viewModel to change ui state
            choosePhotoIv.setOnClickListener { storyBuilderViewModel.addImage() }
            chooseStickerIv.setOnClickListener { storyBuilderViewModel.addSticker() }
            chooseTextIv.setOnClickListener { storyBuilderViewModel.addText() }

            storyView.setOnLayoutTouchedListener { layout ->
                when (layout) {
                    is PicLayout -> storyBuilderViewModel.onImageTouched()
                    is StickerLayout -> storyBuilderViewModel.onStickerTouched()
                    is TextLayout -> storyBuilderViewModel.onTextTouched()
                    else -> storyBuilderViewModel.onDefaultSelection()
                }
            }
        }
    }

    override fun subscribe() {
        // Start a coroutine in the lifecycle scope
        viewLifecycleOwner.lifecycleScope.launch {
            // Launch a coroutine to get user selected storyFrame from file
            launch(Dispatchers.IO) {
                val storyFrame = storyBuilderViewModel.loadFrame(args.frameId)
                val frameBitmap = BitmapFactory.decodeFile(storyFrame.framePath)
                withContext(Dispatchers.Main) {
                    requireBinding { storyView.changeFrame(frameBitmap) }
                }
            }
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    storyBuilderViewModel.uiState.collect { uiState ->
                        when (uiState) {
                            Default -> showDefault()
                            AddImage -> showAddImage()
                            ImageTouch -> showImageTouch()
                            StickerTouch -> showStickerTouch()
                            AddSticker -> TODO()
                            TextTouch -> showTextTouch()
                            AddText -> TODO()
                        }
                    }
                }
            }
        }
    }

    private fun showDefault() {
        requireBinding {
            backBtn.visibility = VISIBLE
            choosePhotoIv.visibility = VISIBLE
            chooseStickerIv.visibility = VISIBLE
            chooseTextIv.visibility = VISIBLE
            shareFab.visibility = VISIBLE
            removeLayoutIb.visibility = GONE
            saveIv.visibility = VISIBLE
        }
    }

    private fun showAddImage() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    private fun showImageTouch() {
        requireBinding {
            backBtn.visibility = GONE
            choosePhotoIv.visibility = VISIBLE
            chooseStickerIv.visibility = GONE
            chooseTextIv.visibility = GONE
            shareFab.visibility = GONE
            removeLayoutIb.visibility = VISIBLE
            saveIv.visibility = GONE
        }
    }

    private fun showStickerTouch() {
        requireBinding {
            backBtn.visibility = GONE
            choosePhotoIv.visibility = GONE
            chooseStickerIv.visibility = GONE
            chooseTextIv.visibility = GONE
            shareFab.visibility = GONE
            removeLayoutIb.visibility = VISIBLE
            saveIv.visibility = GONE
        }
    }

    private fun showTextTouch() {
        requireBinding {
            backBtn.visibility = GONE
            choosePhotoIv.visibility = GONE
            chooseStickerIv.visibility = GONE
            chooseTextIv.visibility = VISIBLE
            shareFab.visibility = GONE
            removeLayoutIb.visibility = VISIBLE
            saveIv.visibility = GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMAGE_REQUEST_CODE) {
            data?.data?.let { imageUri ->
                val picBitmap =
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                        MediaStore.Images.Media.getBitmap(
                            requireActivity().contentResolver,
                            imageUri
                        )
                    } else {
                        val source =
                            ImageDecoder.createSource(requireActivity().contentResolver, imageUri)
                        ImageDecoder.decodeBitmap(source)
                    }
                requireBinding {
                    storyView.changePic(picBitmap)
                }
                storyBuilderViewModel.onDefaultSelection()
            }
        }
    }

    override fun isBottomNavShown(): Boolean = false

    companion object {
        const val IMAGE_REQUEST_CODE = 3001
    }
}