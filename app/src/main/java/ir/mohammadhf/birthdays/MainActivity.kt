package ir.mohammadhf.birthdays

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import ir.mohammadhf.birthdays.databinding.ActivityMainBinding
import ir.mohammadhf.birthdays.di.WrapContextEntry
import ir.mohammadhf.birthdays.utils.setupWithNavController

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        const val AVATAR_FOLDER_NAME = "person_avatar"
    }

    private lateinit var mainBinding: ActivityMainBinding
    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

//        if (savedInstanceState == null)
        setUpBottomNavigation()
        // Else, need to wait for onRestoreInstanceState
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the+
        // BottomNavigationBar with Navigation
        setUpBottomNavigation()
    }

    override fun attachBaseContext(newBase: Context?) {
        val wrapper =
            EntryPointAccessors.fromApplication(newBase!!, WrapContextEntry::class.java).wrapper
        super.attachBaseContext(wrapper.setLocale(newBase))
    }

    private fun setUpBottomNavigation() {
        val navGraphIds = listOf(
            R.navigation.main_nav,
            R.navigation.calendar_nav,
            R.navigation.gift_nav
        )

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = mainBinding.botNav.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this) {}

        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    fun changeBottomNavVisibility(show: Boolean) {
        mainBinding.botNav.visibility = if (show) View.VISIBLE else View.GONE
    }
}