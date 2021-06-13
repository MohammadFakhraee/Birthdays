package ir.mohammadhf.birthdays

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import dagger.hilt.android.HiltAndroidApp
import ir.mohammadhf.birthdays.utils.WrapContext
import java.util.*
import javax.inject.Inject

@HiltAndroidApp
class BirthdayApp : Application()