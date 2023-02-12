package com.zhengineer.dutchblitzscorer

import android.app.Application
import android.content.SharedPreferences
import com.google.android.material.color.DynamicColors
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.zhengineer.commons.util.ThemeUtil.setupTheme
import com.zhengineer.dutchblitzscorer.common.Constants
import com.zhengineer.dutchblitzscorer.database.repository.GameRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@HiltAndroidApp
class MainApplication : Application() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var gameRepository: GameRepository

    @Inject
    lateinit var crashlytics: FirebaseCrashlytics

    override fun onCreate() {
        super.onCreate()
        setupTheme()
        // Apply dynamic colors if supported and turned on in settings
        if (DynamicColors.isDynamicColorAvailable() && sharedPreferences.getBoolean(
                Constants.SHARED_PREF_DYNAMIC_COLORS_KEY, false
            )
        ) {
            DynamicColors.applyToActivitiesIfAvailable(this)
        }

        // Attempt to clean up database at start up
        MainScope().launch {
            try {
                gameRepository.cleanupDeletedGames()
                gameRepository.cleanupPlayers()
            } catch (e: Exception) {
                crashlytics.recordException(e)
            }
        }

    }
}