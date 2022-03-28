package ir.mohammadhf.birthdays.feature.story.frame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mohammadhf.birthdays.data.model.StoryFrame
import ir.mohammadhf.birthdays.data.repo.StoryFrameRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FramesViewModel @Inject constructor(
    private val storyFrameRepository: StoryFrameRepository
) : ViewModel() {

    fun getFramesByCategory(category: String, onLoadCategory: (List<StoryFrame>) -> Unit) {
        viewModelScope.launch {
            onLoadCategory(storyFrameRepository.getByCategory(category))
        }
    }
}