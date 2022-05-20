package ir.mohammadhf.birthdays.feature.story.builder

import android.Manifest
import android.app.Activity.INPUT_METHOD_SERVICE
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ir.mohammadhf.birthdays.R
import ir.mohammadhf.birthdays.core.bases.BaseFragment
import ir.mohammadhf.birthdays.databinding.FragmentStoryBuilderBinding
import ir.mohammadhf.birthdays.feature.story.builder.StoryBuildViewModel.StoryUiState.*
import ir.mohammadhf.birthdays.feature.story.layouts.Layout
import ir.mohammadhf.birthdays.feature.story.layouts.PicLayout
import ir.mohammadhf.birthdays.feature.story.layouts.StickerLayout
import ir.mohammadhf.birthdays.feature.story.layouts.TextLayout
import ir.mohammadhf.birthdays.utils.AnimationFactory
import ir.mohammadhf.birthdays.utils.sdk29OrUp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.io.IOException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class StoryBuilderFragment : BaseFragment<FragmentStoryBuilderBinding>() {
    private val storyBuilderViewModel: StoryBuildViewModel by viewModels()

    private val args: StoryBuilderFragmentArgs by navArgs()

    @Inject
    lateinit var colorPickerListAdapter: ColorPickerListAdapter

    @Inject
    lateinit var typeFacePickerListAdapter: TypeFacePickerListAdapter

    @Inject
    lateinit var animationFactory: AnimationFactory

    private var selectedLayout: Layout? = null

    // The permissions callback, which handles the user's response to the
    // system permissions dialog.
    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted.
                saveBitmap()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                requireBinding {
                    Snackbar.make(
                        root,
                        getString(R.string.external_storage_denial),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

    // The activity result callback, which handles the user's response to the
    // requested intent.

    private val activityContentCallback =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let { imageUri ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    // request to load chosen bitmap on a different thread using coroutine's async
                    val deferredBitmap = getSoftwareBitmapFromUriAsync(imageUri)
                    // waiting for bitmap result
                    val picBitmap = deferredBitmap.await()
                    // calling main thread in order to change the view
                    withContext(Dispatchers.Main) {
                        // require binding class of the ui
                        requireBinding {
                            // set the chosen pic to the layouts
                            storyView.changePic(picBitmap)
                        }
                    }
                }
                // request the view model that default ui state is required
                storyBuilderViewModel.onDefaultSelection()
            }
        }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentStoryBuilderBinding =
        FragmentStoryBuilderBinding.inflate(inflater, container, false)

    override fun initial() {
        storyBuilderViewModel.loadColors(requireContext())
        storyBuilderViewModel.loadTypeFaces(requireContext())

        requireBinding {
            pickerRv.adapter = colorPickerListAdapter
            colorTypeFacePickerIb.tag = TAG_COLOR_PICKER
            // Whenever user touches these icons, ui will tell its viewModel to change ui state
            choosePhotoIv.setOnClickListener { storyBuilderViewModel.addImage() }
            chooseStickerIv.setOnClickListener { storyBuilderViewModel.addSticker() }
            chooseTextIv.setOnClickListener { storyBuilderViewModel.addText() }
            // If user touches outside of the StoryView, ui will tell the viewModel
            root.setOnClickListener { storyBuilderViewModel.onDefaultSelection() }
            // Tells the viewModel that add text button is clicked
            chooseTextIv.setOnClickListener { storyBuilderViewModel.addText() }
            // Pops a navigation stack
            backBtn.setOnClickListener { findNavController().popBackStack() }
            // Calls the method to save the story layout's bitmap in external storage
            saveIv.setOnClickListener { startSaveBitmapFlow() }

            removeLayoutIb.setOnClickListener {
                storyView.removeLayout(selectedLayout)
                storyBuilderViewModel.onDefaultSelection()
            }

            storyView.setOnLayoutTouchedListener { layout ->
                selectedLayout = layout
                when (layout) {
                    is StickerLayout -> storyBuilderViewModel.onStickerTouched()
                    is TextLayout -> storyBuilderViewModel.onTextTouched()
                    is PicLayout -> storyBuilderViewModel.onImageTouched()
                    else -> storyBuilderViewModel.onDefaultSelection()
                }
            }
            // Whenever user touches the out area of edit text, fragment tells the viewModel
            // to change the state to the default
            addTextLayout.setOnClickListener { storyBuilderViewModel.onDefaultSelection() }

            addTextTv.setOnClickListener {
                if (addTextEt.text.toString().isNotEmpty()) {
                    val layout = TextLayout(
                        addTextEt.text.toString(),
                        storyBuilderViewModel.selectedColor.value.color,
                        addTextEt.textSize,
                        storyBuilderViewModel.selectedTypeFace.value.typeFace,
                        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                    )
                    storyView.addLayout(layout)
                    storyBuilderViewModel.onDefaultSelection()
                }
            }

            colorTypeFacePickerIb.setOnClickListener {
                val imageId = when (it.tag) {
                    TAG_COLOR_PICKER -> {
                        it.tag = TAG_TYPEFACE_PICKER
                        requireBinding { pickerRv.adapter = typeFacePickerListAdapter }
                        R.drawable.ic_color_picker
                    }
                    TAG_TYPEFACE_PICKER -> {
                        it.tag = TAG_COLOR_PICKER
                        requireBinding { pickerRv.adapter = colorPickerListAdapter }
                        R.drawable.ic_baseline_text_24
                    }
                    else -> R.drawable.ic_baseline_text_24
                }
                val fadeOut = animationFactory.fadeOutQuick.apply {
                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(p0: Animation?) {}

                        override fun onAnimationEnd(p0: Animation?) {
                            it.startAnimation(animationFactory.fadeInQuick)
                            (it as ImageButton).setImageResource(imageId)
                        }

                        override fun onAnimationRepeat(p0: Animation?) {}
                    })
                }
                it.startAnimation(fadeOut)
            }

            nextTextStyleIb.setOnClickListener {
                // calls the ui to change TextLayout style
                storyView.nextDrawTypeOfSelectedLayout()
                selectedLayout?.takeIf { it is TextLayout }
                    ?.run { this as TextLayout }
                    ?.let { textLay -> changeNextStyleIbIcon(textLay.drawType) }
            }
        }
    }

    override fun subscribe() {
        // Start a coroutine in the lifecycle scope
        viewLifecycleOwner.lifecycleScope.launch {
            // Launch a coroutine to get user's selected storyFrame from file
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
                            Default -> showDefaultUI()
                            AddImage -> showSelectImageUI()
                            ImageTouch -> showOnImageTouchUI()
                            StickerTouch -> showOnStickerTouchUI()
                            AddSticker -> TODO()
                            TextTouch -> showOnTextTouchUI()
                            AddText -> showAddTextUI()
                        }
                    }
                }

                launch(Dispatchers.Main) {
                    storyBuilderViewModel.colorList.collect { colorList ->
                        colorPickerListAdapter.submitList(colorList)
                    }
                }

                launch(Dispatchers.Main) {
                    storyBuilderViewModel.selectedColor.collect {
                        requireBinding { addTextEt.setTextColor(it.color) }
                        colorPickerListAdapter.changeItem(it)
                    }
                }

                launch(Dispatchers.Main) {
                    storyBuilderViewModel.typeFaceList.collect { typeFaceList ->
                        typeFacePickerListAdapter.submitList(typeFaceList)
                    }
                }

                launch {
                    storyBuilderViewModel.selectedTypeFace.collect {
                        requireBinding { addTextEt.typeface = it.typeFace }
                        typeFacePickerListAdapter.changeItem(it)
                    }
                }
            }
        }

        colorPickerListAdapter.setOnItemSelectedListener { color ->
            storyBuilderViewModel.changeSelectedColor(color)
        }

        typeFacePickerListAdapter.setOnItemSelectedListener { typeFaceItem ->
            storyBuilderViewModel.changeSelectedTypeFace(typeFaceItem)
        }
    }

    private fun showDefaultUI() {
        requireBinding {
            backBtn.changeVisibility(true)
            choosePhotoIv.changeVisibility(true)
            chooseStickerIv.changeVisibility(false)
            chooseTextIv.changeVisibility(true)
            shareFab.changeVisibility(false)
            saveIv.changeVisibility(true)
            removeLayoutIb.changeVisibility(false)
            addTextLayout.changeVisibility(false)
            addTextEt.hideSoftKeyboard()
            nextTextStyleIb.changeVisibility(false)
        }
    }

    private fun showSelectImageUI() {
        activityContentCallback.launch("image/*")
    }

    private fun showOnImageTouchUI() {
        requireBinding {
            backBtn.changeVisibility(false)
            choosePhotoIv.changeVisibility(false)
            chooseStickerIv.changeVisibility(false)
            chooseTextIv.changeVisibility(false)
            shareFab.changeVisibility(false)
            saveIv.changeVisibility(false)
            removeLayoutIb.changeVisibility(true)
            addTextLayout.changeVisibility(false)
            addTextEt.hideSoftKeyboard()
            nextTextStyleIb.changeVisibility(false)
        }
    }

    private fun showOnStickerTouchUI() {
        requireBinding {
            backBtn.changeVisibility(false)
            choosePhotoIv.changeVisibility(false)
            chooseStickerIv.changeVisibility(false)
            chooseTextIv.changeVisibility(false)
            shareFab.changeVisibility(false)
            saveIv.changeVisibility(false)
            removeLayoutIb.changeVisibility(true)
            addTextLayout.changeVisibility(false)
            addTextEt.hideSoftKeyboard()
            nextTextStyleIb.changeVisibility(false)
        }
    }

    private fun showAddTextUI() =
        requireBinding {
            backBtn.changeVisibility(false)
            choosePhotoIv.changeVisibility(false)
            chooseStickerIv.changeVisibility(false)
            chooseTextIv.changeVisibility(false)
            shareFab.changeVisibility(false)
            saveIv.changeVisibility(false)
            removeLayoutIb.changeVisibility(false)
            addTextLayout.changeVisibility(true)
            addTextEt.setText("")
            addTextEt.showSoftKeyboard()
            nextTextStyleIb.changeVisibility(false)
        }

    private fun showOnTextTouchUI() {
        requireBinding {
            backBtn.changeVisibility(false)
            choosePhotoIv.changeVisibility(false)
            chooseStickerIv.changeVisibility(false)
            chooseTextIv.changeVisibility(false)
            shareFab.changeVisibility(false)
            saveIv.changeVisibility(false)
            removeLayoutIb.changeVisibility(true)
            addTextLayout.changeVisibility(false)
            addTextEt.hideSoftKeyboard()
            nextTextStyleIb.changeVisibility(true)
            selectedLayout?.takeIf { it is TextLayout }
                ?.run { this as TextLayout }
                ?.let { textLay -> changeNextStyleIbIcon(textLay.drawType) }
        }
    }

    private fun changeNextStyleIbIcon(drawType: TextLayout.DrawType) {
        requireBinding {
            when (drawType) {
                TextLayout.DrawType.EMPTY_OUTSIDE -> {
                    nextTextStyleIb.setBackgroundResource(R.drawable.bg_circle_white)
                    nextTextStyleIb.setColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.black)
                    )
                }
                TextLayout.DrawType.FILLED_COLORED -> {
                    nextTextStyleIb.setBackgroundResource(R.drawable.bg_circle_transparent_black)
                    nextTextStyleIb.setColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.white)
                    )
                }
                TextLayout.DrawType.FILLED_WHITE -> {
                    nextTextStyleIb.setBackgroundColor(Color.TRANSPARENT)
                    nextTextStyleIb.setColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.black)
                    )
                }
            }
        }
    }

    /**
     * Is an extension function for views. Changes the visibility of view as requested.
     * If the view is visible and the caller requests to make it invisible, it calls
     * View.animateViewToGone(), whereas if the view's visibility is GONE and caller requests to
     * make it visible, it calls View.animateViewToVisible(). In other situations, it doesn't do
     * anything.
     * @param shouldVisible is caller's requested visibility.
     */
    private fun View.changeVisibility(shouldVisible: Boolean) {
        if (isVisible && !shouldVisible) animateViewToGone()
        else if (!isVisible && shouldVisible) animateViewToVisible()
    }

    /**
     * Is an extension function for views. Changes the visibility of view to VISIBLE with
     * a fadeInQuick animation.
     */
    private fun View.animateViewToVisible() {
        visibility = VISIBLE
        startAnimation(animationFactory.fadeInQuick)
    }

    /**
     * Is an extension function for views. Changes the visibility of view to GONE with
     * a fadeOutQuick animation.
     */
    private fun View.animateViewToGone() {
        visibility = GONE
        startAnimation(animationFactory.fadeOutQuick)
    }

    /**
     * Is an extension function for editText. Tells the ui to show the soft keyboard.
     */
    private fun EditText.showSoftKeyboard() {
        requestFocus()
        (requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    /**
     * Is an extension function for editText. Tells the ui to hide the soft keyboard.
     */
    private fun EditText.hideSoftKeyboard() {
        (requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(windowToken, 0)
    }

    /**
     * Requests the [android.Manifest.permission.WRITE_EXTERNAL_STORAGE] permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    private fun startSaveBitmapFlow() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                saveBitmap()
            }
            hasWriteExternalStoragePermission() -> saveBitmap()
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ->
                requireBinding {
                    val snackBar = Snackbar.make(
                        root,
                        getString(R.string.external_storage_permission_required),
                        Snackbar.LENGTH_INDEFINITE
                    )
                    snackBar.setAction(getString(R.string.accept_permission)) {
                        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                    snackBar.show()
                }
            else -> requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun saveBitmap() {
        Log.i("PermissionManager", "User granted permission and Save bitmap is called")

        requireBinding {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val resBitmap = storyView.getResultBitmap()
                val imageCollection = sdk29OrUp {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "${UUID.randomUUID()}.jpg")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.WIDTH, resBitmap.width)
                    put(MediaStore.Images.Media.HEIGHT, resBitmap.height)
                }
                try {
                    requireActivity().contentResolver.insert(imageCollection, contentValues)
                        ?.also { uri ->
                            requireActivity().contentResolver.openOutputStream(uri)
                                .use { outputStream ->
                                    if (!resBitmap.compress(
                                            Bitmap.CompressFormat.JPEG,
                                            100,
                                            outputStream
                                        )
                                    )
                                        throw IOException("Couldn't save bitmap")
                                    withContext(Dispatchers.Main) {
                                        Snackbar.make(
                                            root,
                                            getString(R.string.success_in_external_storage_saving),
                                            Snackbar.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        } ?: throw IOException("Couldn't create MediaStore entry")
                } catch (e: IOException) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Snackbar.make(
                            root,
                            getString(R.string.error_in_external_storage_saving),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun hasWriteExternalStoragePermission(): Boolean =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private suspend fun getSoftwareBitmapFromUriAsync(imageUri: Uri): Deferred<Bitmap> =
        coroutineScope {
            async(Dispatchers.IO) {
                val bitmap = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        imageUri
                    )
                } else {
                    val source =
                        ImageDecoder.createSource(requireActivity().contentResolver, imageUri)
                    ImageDecoder.decodeBitmap(source)
                }
                bitmap.copy(Bitmap.Config.ARGB_8888, false)
            }
        }

    override fun isBottomNavShown(): Boolean = false

    companion object {
        const val TAG_COLOR_PICKER = 1
        const val TAG_TYPEFACE_PICKER = 0
    }
}