package ir.mohammadhf.birthdays.data

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import ir.mohammadhf.birthdays.R
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.data.model.StoryFrame
import javax.inject.Inject

class InitialDataGenerator @Inject constructor() {

    fun generateGroups(context: Context):ArrayList<Group> =
        arrayListOf(
            Group(
                0L,
                context.getString(R.string.family),
                ContextCompat.getColor(context, R.color.group_blue_light)
            ),
            Group(
                0L,
                context.getString(R.string.friends),
                ContextCompat.getColor(context, R.color.group_yellow)
            ),
            Group(
                0L,
                context.getString(R.string.work),
                ContextCompat.getColor(context, R.color.group_red_dark)
            )
        )

    fun generateFrames(resources: Resources): Pair<ArrayList<StoryFrame>, ArrayList<Bitmap>> {
        val giftFrames = arrayListOf<StoryFrame>()
        val giftBitmaps = arrayListOf<Bitmap>()

        giftFrames.add(
            StoryFrame(
                0L, "hbd_01", "birthday",
                1, "", ""
            )
        )
        giftBitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.hbd_frame_01))

        giftFrames.add(
            StoryFrame(
                0L, "hbd_02", "birthday",
                1, "", ""
            )
        )
        giftBitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.hbd_frame_02))

        giftFrames.add(
            StoryFrame(
                0L, "hbd_03", "birthday",
                1, "", ""
            )
        )
        giftBitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.hbd_frame_03))

        giftFrames.add(
            StoryFrame(
                0L, "hbd_04", "birthday",
                1, "", ""
            )
        )
        giftBitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.hbd_frame_04))

        giftFrames.add(
            StoryFrame(
                0L, "hbd_05", "birthday",
                1, "", ""
            )
        )
        giftBitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.hbd_frame_05))

        giftFrames.add(
            StoryFrame(
                0L, "hbd_06", "birthday",
                1, "", ""
            )
        )
        giftBitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.hbd_frame_06))

        giftFrames.add(
            StoryFrame(
                0L, "hbd_07", "birthday",
                1, "", ""
            )
        )
        giftBitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.hbd_frame_07))

        giftFrames.add(
            StoryFrame(
                0L, "hbd_08", "birthday",
                1, "", ""
            )
        )
        giftBitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.hbd_frame_08))

        giftFrames.add(
            StoryFrame(
                0L, "hbd_09", "birthday",
                1, "", ""
            )
        )
        giftBitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.hbd_frame_09))

        return giftFrames to giftBitmaps
    }

    fun generateColors(): ArrayList<Int> =
        arrayListOf(
            R.color.dark_blue,
            R.color.blue,
            R.color.lightBlue,
            R.color.ultra_light_blue,
            R.color.cyan,
            R.color.deepPurple,
            R.color.purple,
            R.color.ultra_light_purple,
            R.color.red,
            R.color.pink,
            R.color.deepOrange,
            R.color.orange,
            R.color.yellow,
            R.color.green,
            R.color.lightGreen,
            R.color.lime,
            R.color.black,
            R.color.gray700,
            R.color.gray500
        )

    fun generateTypeFaceId(): ArrayList<Int> =
        arrayListOf(
            R.font.duman,
            R.font.shabnam,
            R.font.digi_hekayat,
            R.font.digi_hekayat_bold,
            R.font.gandom,
            R.font.khandevane,
            R.font.mikhak_medium
        )
}