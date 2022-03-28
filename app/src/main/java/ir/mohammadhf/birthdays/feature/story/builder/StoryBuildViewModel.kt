package ir.mohammadhf.birthdays.feature.story.builder

import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mohammadhf.birthdays.core.bases.BaseViewModel
import ir.mohammadhf.birthdays.data.model.StoryFrame
import ir.mohammadhf.birthdays.data.repo.StoryFrameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class StoryBuildViewModel @Inject constructor(
    private val storyFrameRepository: StoryFrameRepository
) : BaseViewModel() {
    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(StoryUiState.Default)

    // Ui collects its state from this StateFlow
    val uiState: StateFlow<StoryUiState> = _uiState

    suspend fun loadFrame(id: Long): StoryFrame =
        storyFrameRepository.getOne(id)

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

