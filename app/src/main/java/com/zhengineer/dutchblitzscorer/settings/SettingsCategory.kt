package com.zhengineer.dutchblitzscorer.settings

import android.content.ActivityNotFoundException
import android.content.Context
import com.zhengineer.commons.settings.SettingsPrefFragment
import com.zhengineer.dutchblitzscorer.settings.categories.SettingsGameDatabaseFragment

/**
 * Settings categories from Main settings screen
 */
enum class SettingsCategory(
    val settingsKey: String,
    val fragment: (() -> SettingsPrefFragment)?,
    val otherAction: ((context: Context) -> Unit)?,
) {
    ContactDeveloper("contact_developer", null, { SettingsDialogs.contactDeveloperDialog(it) }),
    RateApp("rate_app", null, {
        val appPackageName = it.packageName
        try {
            it.startActivity(
                android.content.Intent(
                    android.content.Intent.ACTION_VIEW,
                    android.net.Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (nfe: ActivityNotFoundException) {
            it.startActivity(
                android.content.Intent(
                    android.content.Intent.ACTION_VIEW,
                    android.net.Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }),
    ShareApp("share_app", null, {
        val sendIntent: android.content.Intent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(
                android.content.Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=${it.packageName}"
            )
            type = "text/plain"
        }

        it.startActivity(
            android.content.Intent.createChooser(
                sendIntent,
                "Share Play Store link using..."
            )
        )
    }),
    Database("database", { SettingsGameDatabaseFragment() }, null);
}
