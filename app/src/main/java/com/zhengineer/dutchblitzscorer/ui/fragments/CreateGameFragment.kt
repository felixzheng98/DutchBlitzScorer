package com.zhengineer.dutchblitzscorer.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zhengineer.dutchblitzscorer.R
import com.zhengineer.dutchblitzscorer.common.result.CreateGameResult
import com.zhengineer.dutchblitzscorer.databinding.FragmentNewGamePlayersBinding
import com.zhengineer.dutchblitzscorer.ui.rv.PlayerInfoAdapter
import com.zhengineer.dutchblitzscorer.viewmodels.CreateGameViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * The screen where people describe the number of players and put their name and colors
 */
@AndroidEntryPoint
class CreateGameFragment : Fragment(), MenuProvider {
    private lateinit var binding: FragmentNewGamePlayersBinding
    private val viewModel: CreateGameViewModel by viewModels()

    private lateinit var adapter: PlayerInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backDialog(requireContext()) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewGamePlayersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        with(binding) {
            dropdownNumPlayers.editText!!.apply {
                (this as AutoCompleteTextView).setText(viewModel.numPlayers.value.toString(), false)
                addTextChangedListener {
                    if (it != null) {
                        viewModel.numPlayers.value = it.toString().toInt()
                    }
                }
            }

            recyclerViewPlayers.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = PlayerInfoAdapter(viewModel.players).also {
                    this@CreateGameFragment.adapter = it
                }
            }

            fab.setOnClickListener {
                val gameId = when (val createGameResult = viewModel.createGame()) {
                    is CreateGameResult.Error -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(createGameResult.error.errorDialogTitle)
                            .setMessage(createGameResult.error.errorDialogMessage)
                            .setPositiveButton(R.string.okay, null)
                            .show()
                        return@setOnClickListener
                    }
                    is CreateGameResult.Success -> {
                        createGameResult.gameId
                    }
                }

                findNavController().navigate(
                    CreateGameFragmentDirections.actionGameConfigFragmentToGameScoringFragment(
                        gameId
                    )
                )
            }

            gameTitle.editText!!.apply {
                setText(viewModel.gameTitle)
                addTextChangedListener {
                    if (it != null) {
                        viewModel.gameTitle = it.toString()
                    }
                }
            }
        }

        viewModel.numPlayers.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.updatePlayerCount(it)
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_help, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_help) {
            dialogHelp(requireContext())
            return true
        }
        return false
    }

    companion object {
        fun backDialog(context: Context, positiveAction: () -> Unit) {
            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_title_quit_create)
                .setMessage(R.string.dialog_message_quit_create)
                .setPositiveButton(R.string.quit) { _, _ ->
                    positiveAction()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        private fun dialogHelp(context: Context) {
            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_title_help_create_game)
                .setMessage(R.string.dialog_message_help_create_game)
                .setPositiveButton(R.string.okay, null)
                .show()
        }
    }
}
