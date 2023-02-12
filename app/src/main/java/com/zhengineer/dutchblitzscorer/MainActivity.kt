package com.zhengineer.dutchblitzscorer

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.zhengineer.dutchblitzscorer.databinding.ActivityMainBinding
import com.zhengineer.dutchblitzscorer.ui.fragments.CreateGameFragment
import com.zhengineer.dutchblitzscorer.ui.fragments.GameScoringFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return when (navController.currentDestination?.id) {
            R.id.CreateGameFragment -> {
                CreateGameFragment.backDialog(this) {
                    navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
                }
                true
            }
            R.id.GameScoringFragment -> {
                navController.navigate(
                    GameScoringFragmentDirections.actionJumpToMainFragment()
                )
                true
            }
            else -> navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        }
    }

    fun setToolbarTitle(
        @StringRes title: Int? = null,
        titleStr: String? = null,
    ) {
        binding.toolbar.apply {
            if (title != null) {
                setTitle(title)
            } else {
                setTitle(titleStr!!)
            }
        }
    }
}