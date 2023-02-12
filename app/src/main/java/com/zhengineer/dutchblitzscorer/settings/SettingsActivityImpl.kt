package com.zhengineer.dutchblitzscorer.settings

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import com.zhengineer.commons.settings.SettingsActivity
import com.zhengineer.dutchblitzscorer.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivityImpl : SettingsActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentById(binding.fragmentContainer.id)
        if (fragment == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(binding.fragmentContainer.id, MainSettingsFragment())
                .commit()
        }
    }

    override fun getToolbar(): Toolbar = binding.toolbar

    override fun setupContentView() {
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun setToolbarIconAndTitle(isRoot: Boolean, @StringRes title: Int) {
        super.setToolbarIconAndTitle(isRoot, title)
        binding.collapsingToolbarId.title = resources.getString(title)
    }

    companion object {
        @Suppress("unused")
        private val TAG: String = "SettingsActivityImpl"
    }
}