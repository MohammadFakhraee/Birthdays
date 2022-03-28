package ir.mohammadhf.birthdays.data.repo

import android.graphics.Bitmap
import ir.mohammadhf.birthdays.data.local.StoryFrameDao
import ir.mohammadhf.birthdays.data.model.StoryFrame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class StoryFrameRepository @Inject constructor(private val storyFrameDao: StoryFrameDao) {

    suspend fun saveGiftFrame(
        storyFrame: StoryFrame,
        frameBitmap: Bitmap,
        prevBitmap: Bitmap,
        parentFile: File
    ) = withContext(Dispatchers.IO) {
        val catFile = File(parentFile, storyFrame.category + "/")
        if (!catFile.exists()) catFile.mkdirs()
        val frameDif = async {
            val frameFile = File(parentFile, storyFrame.category + "/" + storyFrame.name)
            saveBitmapToFile(frameBitmap, frameFile, 100)
            frameFile.path
        }

        val prevDif = async {
            val prevFile = File(parentFile, storyFrame.category + "/" + "prev" + storyFrame.name)
            saveBitmapToFile(prevBitmap, prevFile, 65)
            prevFile.path
        }

        storyFrameDao.insertOne(
            storyFrame.apply {
                framePath = frameDif.await()
                previewPath = prevDif.await()
            }
        )
    }

    suspend fun getAll(): List<StoryFrame> =
        withContext(Dispatchers.IO) { storyFrameDao.getAll() }

    suspend fun getByCategory(category: String): List<StoryFrame> =
        withContext(Dispatchers.IO) { storyFrameDao.getByCategory(category) }

    suspend fun getOne(id: Long): StoryFrame =
        withContext(Dispatchers.IO) { storyFrameDao.getOne(id) }

    private fun saveBitmapToFile(bitmap: Bitmap, file: File, quality: Int) =
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, FileOutputStream(file))
}