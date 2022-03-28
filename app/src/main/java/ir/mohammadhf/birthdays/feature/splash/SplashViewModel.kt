package ir.mohammadhf.birthdays.feature.splash

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mohammadhf.birthdays.MainActivity
import ir.mohammadhf.birthdays.core.InitialSharedPreferences
import ir.mohammadhf.birthdays.core.bases.BaseViewModel
import ir.mohammadhf.birthdays.data.InitialDataGenerator
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.data.model.StoryFrame
import ir.mohammadhf.birthdays.data.repo.GroupRepository
import ir.mohammadhf.birthdays.data.repo.StoryFrameRepository
import ir.mohammadhf.birthdays.feature.notify.setTheAlarm
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val storyFrameRepository: StoryFrameRepository,
    private val initialSharedPreferences: InitialSharedPreferences,
    private val initialDataGenerator: InitialDataGenerator
) : BaseViewModel() {

    fun initSetup(context: Context, onFinish: () -> Unit) {
        viewModelScope.launch {
            val groupJob = launch { saveGroup(initialDataGenerator.generateGroups(context)) }
            val framesJob = launch {
                val (giftFrames, giftBitmaps) = initialDataGenerator.generateFrames(context.resources)
                saveGiftFrames(
                    giftFrames,
                    giftBitmaps,
                    context.getDir(
                        MainActivity.GIFT_FRAME_FOLDER_NAME,
                        Context.MODE_PRIVATE
                    )
                )
            }
            val alarmJob = launch { setAlarm(context) }
            groupJob.join()
            framesJob.join()
            alarmJob.join()
            onFinish()
        }
    }

    private suspend fun saveGroup(groups: List<Group>) {
        if (!initialSharedPreferences.isGroupSaved()) {
            groupRepository.saveGroups(groups)
            initialSharedPreferences.setGroupSaved(true)
        }
    }

    private suspend fun saveGiftFrames(
        storyFrames: List<StoryFrame>,
        frameBitmaps: List<Bitmap>,
        parentFile: File
    ) {
        if (!initialSharedPreferences.isFramesSaved()) {
            for ((index, frame) in storyFrames.withIndex()) {
                storyFrameRepository.saveGiftFrame(
                    frame,
                    frameBitmaps[index],
                    frameBitmaps[index],
                    parentFile
                )
            }
            initialSharedPreferences.setGiftFrameSaved(true)
        }
    }

    fun setAlarm(context: Context) {
        if (!initialSharedPreferences.isAlarmSet()) {
            setTheAlarm(context, false)
            initialSharedPreferences.setAlarmSet(true)
        }
    }
}