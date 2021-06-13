package ir.mohammadhf.birthdays

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ApplicationComponent
import ir.mohammadhf.birthdays.databinding.ActivityMainBinding
import ir.mohammadhf.birthdays.di.WrapContextEntry
import ir.mohammadhf.birthdays.utils.WrapContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        const val AVATAR_FOLDER_NAME = "person_avatar"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun attachBaseContext(newBase: Context?) {
        val wrapper =
            EntryPointAccessors.fromApplication(newBase!!, WrapContextEntry::class.java).wrapper
        super.attachBaseContext(wrapper.setLocale(newBase))
    }
}