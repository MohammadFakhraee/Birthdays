package ir.mohammadhf.birthdays.feature.story.builder

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mohammadhf.birthdays.core.bases.BaseViewModel
import ir.mohammadhf.birthdays.data.InitialDataGenerator
import ir.mohammadhf.birthdays.data.model.ColorItem
import ir.mohammadhf.birthdays.data.model.StoryFrame
import ir.mohammadhf.birthdays.data.model.TypeFaceItem
import ir.mohammadhf.birthdays.data.repo.StoryFrameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryBuildViewModel @Inject constructor(
    private val storyFrameRepository: StoryFrameRepository,
    private val initialDataGenerator: InitialDataGenerator
) : BaseViewModel() {
    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(StoryUiState.Default)

    // Ui collects its state from this StateFlow
    val uiState: StateFlow<StoryUiState> = _uiState

    // Backing property to avoid color list update from other classes
    private val _colorList = MutableStateFlow(arrayListOf<ColorItem>())

    // Ui collects colors from this StateFlow
    val colorList: StateFlow<ArrayList<ColorItem>> = _colorList

    // Backing property to avoid color selection from other classes
    private val _selectedColor = MutableStateFlow(ColorItem(-1, android.graphics.Color.BLACK))

    // Ui gets current selected color from this property
    val selectedColor: StateFlow<ColorItem> = _selectedColor

    // Backing property to avoid typeFace changes from other classes
    private val _typeFaceList = MutableStateFlow(arrayListOf<TypeFaceItem>())

    // ui collects TypeFace list from this property
    val typeFaceList: StateFlow<ArrayList<TypeFaceItem>> = _typeFaceList

    // Backing property to avoid selected TypeFace update from other classes
    private val _selectedTypeFace = MutableStateFlow(TypeFaceItem(-1, Typeface.DEFAULT))

    // Ui gets current selected TypeFace from this property
    val selectedTypeFace: StateFlow<TypeFaceItem> = _selectedTypeFace

    suspend fun loadFrame(id: Long): StoryFrame =
        storyFrameRepository.getOne(id)

    fun loadColors(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_colorList.value.isEmpty()) {
                _colorList.value =
                    initialDataGenerator.generateColors().mapIndexed { index, color ->
                        ColorItem(index, ContextCompat.getColor(context, color))
                    } as ArrayList
                _selectedColor.value = _colorList.value[0]
            }
        }
    }

    fun loadTypeFaces(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_typeFaceList.value.isEmpty()) {
                _typeFaceList.value =
                    initialDataGenerator.generateTypeFaceId().mapIndexed { index, typeFaceId ->
                        TypeFaceItem(index, ResourcesCompat.getFont(context, typeFaceId)!!)
                    } as ArrayList
                _selectedTypeFace.value = _typeFaceList.value[0]
            }
        }
    }

    fun onDefaultSelection() {
        _uiState.value = StoryUiState.Default
    }

    fun onImageTouched() {
        _uiState.value = StoryUiState.ImageTouch
    }

    fun addImage() {
        _uiState.value = StoryUiState.AddImage
    }

    fun onStickerTouched() {
        _uiState.value = StoryUiState.StickerTouch
    }

    fun addSticker() {
        _uiState.value = StoryUiState.AddSticker
    }

    fun onTextTouched() {
        _uiState.value = StoryUiState.TextTouch
    }

    fun addText() {
        _uiState.value = StoryUiState.AddText
        _selectedColor.value = _colorList.value[0]
        _selectedTypeFace.value = _typeFaceList.value[0]
    }

    fun changeSelectedColor(colorItem: ColorItem) {
        _selectedColor.value = colorItem
    }

    fun changeSelectedTypeFace(typeFaceItem: TypeFaceItem) {
        _selectedTypeFace.value = typeFaceItem
    }

    // Different types of state shown in ui
    enum class StoryUiState {
        Default,
        ImageTouch,
        AddImage,
        StickerTouch,
        AddSticker,
        TextTouch,
        AddText
    }
}

