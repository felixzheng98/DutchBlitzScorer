package com.zhengineer.dutchblitzscorer.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zhengineer.dutchblitzscorer.MainActivity
import com.zhengineer.dutchblitzscorer.R
import com.zhengineer.dutchblitzscorer.common.Constants
import com.zhengineer.dutchblitzscorer.databinding.FragmentMainBinding
import com.zhengineer.dutchblitzscorer.settings.SettingsActivityImpl
import com.zhengineer.dutchblitzscorer.ui.rv.CustomItemKeyProvider
import com.zhengineer.dutchblitzscorer.ui.rv.GameAdapter
import com.zhengineer.dutchblitzscorer.ui.rv.GameItemDetailsLookup
import com.zhengineer.dutchblitzscorer.viewmodels.MainFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class MainFragment : Fragment(), MenuProvider {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val viewModel: MainFragmentViewModel by viewModels()
    private lateinit var binding: FragmentMainBinding

    private lateinit var adapter: GameAdapter
    private var tracker: SelectionTracker<Long>? = null
    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // This menu should only be shown in this fragment as it is a fragment specific menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // show onboarding dialog if user hasn't seen before
        if (!sharedPreferences.getBoolean(Constants.SHARED_PREF_ONBOARDING_DIALOG_KEY, false)) {
            onboardingDialog(requireContext(), sharedPreferences)
        }

        viewModel.resetSelection()

        with(binding) {
            fab.setOnClickListener {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToCreateGameFragment())
            }

            buttonInfo.setOnClickListener {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToInfoFragment())
            }

            recyclerViewGames.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = GameAdapter(emptyList()).also { this@MainFragment.adapter = it }
                emptyView = binding.emptyView
                addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        DividerItemDecoration.VERTICAL
                    )
                )

                tracker = SelectionTracker.Builder(
                    SELECTION_ID,
                    this,
                    CustomItemKeyProvider(this, this@MainFragment.adapter),
                    GameItemDetailsLookup(this),
                    StorageStrategy.createLongStorage()
                ).build()
            }
        }

        adapter.tracker = tracker
        tracker!!.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                viewModel.updateSelection(tracker!!.selection.size())
            }
        })

        with(viewModel) {
            games.observe(viewLifecycleOwner) {
                if (it != null) {
                    adapter.updateDataset(it)
                }
            }

            // Only refresh all after the first selection or once everything is deselected to show
            // checkbox on all games during selection
            itemsSelectedDistinct.observe(viewLifecycleOwner) {
                if (it != null) {
                    adapter.notifyDataSetChanged()
                    requireActivity().invalidateMenu()
                }
            }

            itemSelectionCount.observe(viewLifecycleOwner) {
                if (it != null) {
                    if (it > 0) {
                        (requireActivity() as MainActivity).setToolbarTitle(
                            titleStr = resources.getQuantityString(
                                R.plurals.games_selected,
                                it,
                                it
                            )
                        )
                        binding.fab.visibility = View.GONE
                    } else {
                        (requireActivity() as MainActivity).setToolbarTitle(R.string.app_name)
                        binding.fab.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
        this.menu = menu
    }

    override fun onPrepareMenu(menu: Menu) {
        super.onPrepareMenu(menu)
        viewModel.itemsSelectedDistinct.value?.let {
            menu.setGroupVisible(R.id.selection_group, it)
            menu.setGroupVisible(R.id.regular_group, !it)
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val i = Intent(requireActivity(), SettingsActivityImpl::class.java)
                startActivity(i)
                true
            }
            R.id.action_info -> {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToInfoFragment())
                true
            }
            R.id.delete_games -> {
                dialogDeleteGames(requireContext()) {
                    viewModel.markGamesDeleted(tracker!!.selection.toList())
                    tracker!!.clearSelection()
                }
                true
            }
            R.id.cancel_selection -> {
                viewModel.resetSelection()
                tracker!!.clearSelection()
            }
            else -> false
        }
    }

    companion object {
        private const val SELECTION_ID = "game-selection"

        private fun dialogDeleteGames(context: Context, positiveAction: () -> Unit) {
            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_title_delete_selected_games)
                .setMessage(R.string.dialog_message_delete_selected_games)
                .setPositiveButton(R.string.delete) { _, _ ->
                    positiveAction()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        private fun onboardingDialog(context: Context, sharedPreferences: SharedPreferences) {
            MaterialAlertDialogBuilder(
                context,
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
            )
                .setIcon(R.drawable.ic_celebration)
                .setTitle(R.string.dialog_title_onboarding)
                .setMessage(R.string.dialog_message_onboarding)
                .setPositiveButton(R.string.got_it) { _, _ ->
                    sharedPreferences.edit()
                        .putBoolean(Constants.SHARED_PREF_ONBOARDING_DIALOG_KEY, true)
                        .apply()
                }
                .setCancelable(false) // Don't allow users to click outside
                .show()
        }
    }
}