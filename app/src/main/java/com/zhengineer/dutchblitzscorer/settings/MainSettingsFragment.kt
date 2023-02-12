package com.zhengineer.dutchblitzscorer.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentTransaction
import androidx.preference.Preference
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zhengineer.commons.settings.SettingsPrefFragment
import com.zhengineer.commons.util.CommonPreferences
import com.zhengineer.commons.util.enums.Theme
import com.zhengineer.dutchblitzscorer.R
import com.zhengineer.dutchblitzscorer.common.Constants

class MainSettingsFragment : SettingsPrefFragment(true, R.string.settings),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_settings, rootKey)

        SettingsCategory.values().forEach { settingsCategory ->
            val preference = findPreference<Preference>(settingsCategory.settingsKey)
            if (preference != null) {
                preference.setOnPreferenceClickListener { navigate(settingsCategory) }
            } else {
                throw IllegalStateException(
                    "Failed to attach on preference click listener for key: " +
                            "${settingsCategory.settingsKey}. Make sure the entries in " +
                            "SettingsCategory enum and pref_settings XML match up!"
                )
            }
        }

        // only show dynamic colors setting if it is supported / available on the device
        if (!DynamicColors.isDynamicColorAvailable()) {
            findPreference<Preference>(Constants.SHARED_PREF_DYNAMIC_COLORS_KEY)?.isVisible = false
        }
    }

    /**
     * Navigate will either navigate to a new fragment or do another action if there is no fragment
     * that was listed in SettingsCategory enum
     */
    private fun navigate(settingsCategory: SettingsCategory): Boolean {
        if (settingsCategory.fragment != null) {
            val target = settingsCategory.fragment.invoke()
            parentFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_container, target)
                .addToBackStack(settingsCategory.settingsKey)
                .commit()
        } else if (settingsCategory.otherAction != null) {
            settingsCategory.otherAction.invoke(requireActivity())
        } else {
            throw IllegalStateException(
                "Was not able to handle SettingsCategory navigate: " +
                        "$settingsCategory. This should not occur as there should be either a " +
                        "fragment that it goes to or another action."
            )
        }

        return true
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences == null || key == null) {
            return
        }

        when (key) {
            CommonPreferences.THEME -> {
                AppCompatDelegate.setDefaultNightMode(
                    Theme.fromHumanReadableString(
                        sharedPreferences.getString(key, Theme.SYSTEM.name)!!
                    ).mode
                )
            }
            Constants.SHARED_PREF_DYNAMIC_COLORS_KEY -> {
                // Not automatically relaunching because there seems to be a bug
                // https://github.com/JakeWharton/ProcessPhoenix for Android 12, which is what is
                // used to relaunch the app normally
                dialogRequireRestart(requireContext())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    companion object {
        private const val TAG: String = "MainSettingsFragment"

        private fun dialogRequireRestart(context: Context) {
            MaterialAlertDialogBuilder(
                context,
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
            )
                .setIcon(R.drawable.ic_auto_fix)
                .setTitle(R.string.dialog_title_restart_required)
                .setMessage(R.string.dialog_message_restart_required)
                .setPositiveButton(R.string.okay, null)
                .show()
        }
    }
}