package com.zhengineer.dutchblitzscorer.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zhengineer.dutchblitzscorer.R

object SettingsDialogs {
    private val EMAIL_LIST = arrayOf("help+dbs@zhengineer.com")

    fun contactDeveloperDialog(context: Context) {
        MaterialAlertDialogBuilder(
            context,
            com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        )
            .setTitle(R.string.contact_developer)
            .setMessage(R.string.contact_developer_message)
            .setIcon(R.drawable.ic_contact_mail)
            .setPositiveButton(R.string.cont) { _, _ ->
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, EMAIL_LIST)
                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        "${context.getString(R.string.app_name)} ${context.getString(R.string.version_name)} - Help"
                    )
                }

                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(Intent.createChooser(intent, "Send email using..."))
                } else {
                    MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.no_email_app)
                        .setMessage("Please email ${EMAIL_LIST[0]} for help.")
                        .setPositiveButton(R.string.okay, null)
                        .show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    fun deleteAllDataDialog(context: Context, positiveAction: () -> Unit) {
        MaterialAlertDialogBuilder(
            context,
            com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        )
            .setTitle(R.string.delete_game_data)
            .setMessage(R.string.delete_warning_message)
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(R.string.delete) { _, _ ->
                positiveAction()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}