package com.zhengineer.dutchblitzscorer.settings.categories

import android.os.Bundle
import androidx.preference.Preference
import com.zhengineer.commons.settings.SettingsPrefFragment
import com.zhengineer.dutchblitzscorer.R
import com.zhengineer.dutchblitzscorer.database.repository.GameRepository
import com.zhengineer.dutchblitzscorer.settings.SettingsDialogs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsGameDatabaseFragment : SettingsPrefFragment(false, R.string.game_database) {

    @Inject
    lateinit var gameRepository: GameRepository

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_game_database_settings)
        findPreference<Preference>("delete_all_data")?.setOnPreferenceClickListener {
            SettingsDialogs.deleteAllDataDialog(requireContext()) {
                // Not using viewLifecycleScope so that it isn't cancelled based on lifecycle
                CoroutineScope(Dispatchers.IO).launch {
                    gameRepository.deleteAllGames()
                    gameRepository.cleanupPlayers()
                }
            }
            true
        }
    }
}