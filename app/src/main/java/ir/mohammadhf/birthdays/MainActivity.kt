package ir.mohammadhf.birthdays

import android.content.Context
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import ir.mohammadhf.birthdays.databinding.ActivityMainBinding
import ir.mohammadhf.birthdays.di.WrapContextEntry
import ir.mohammadhf.birthdays.utils.WrapContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        const val AVATAR_FOLDER_NAME = "person_avatar"
        const val GIFT_FRAME_FOLDER_NAME = "gift_frames"
    }

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var wrapper: WrapContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//         setup bottom navigation view with navController
        navController = binding.navHostContainer.getFragment<NavHostFragment>().navController
        binding.botNav.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    fun changeBottomNavVisibility(show: Boolean) {
        binding.botNav.visibility = if (show) VISIBLE else GONE
//        binding.botNav.visibility = VISIBLE
    }

    override fun attachBaseContext(newBase: Context) {
        wrapper = EntryPointAccessors.fromApplication(newBase, WrapContextEntry::class.java).wrapper
        super.attachBaseContext(wrapper.setLocale(newBase))
    }
}