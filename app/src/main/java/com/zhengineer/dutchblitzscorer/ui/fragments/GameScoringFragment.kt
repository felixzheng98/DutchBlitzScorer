package com.zhengineer.dutchblitzscorer.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zhengineer.commons.ui.CustomDividerItemDecoration
import com.zhengineer.dutchblitzscorer.R
import com.zhengineer.dutchblitzscorer.databinding.FragmentGameScoringBinding
import com.zhengineer.dutchblitzscorer.ui.rv.ScoreAdapter
import com.zhengineer.dutchblitzscorer.viewmodels.GameScoringViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameScoringFragment : Fragment(), MenuProvider {
    private lateinit var binding: FragmentGameScoringBinding
    private val viewModel: GameScoringViewModel by hiltNavGraphViewModels(R.id.nav_graph_scoring)
    private lateinit var adapter: ScoreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(GameScoringFragmentDirections.actionJumpToMainFragment())
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameScoringBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        with(binding) {
            chipRecordRound.setOnClickListener {
                viewModel.initRecordRound()
                findNavController().navigate(
                    GameScoringFragmentDirections.actionGameScoringFragmentToRecordRoundFragment(
                        0
                    )
                )
            }

            chipEditDeleteRound.setOnClickListener {
                dialogEditDeleteTip(requireContext())
            }

            recyclerViewScore.apply {
                adapter = ScoreAdapter(viewModel).also { this@GameScoringFragment.adapter = it }
                addItemDecoration(
                    CustomDividerItemDecoration(
                        requireContext(),
                        DividerItemDecoration.HORIZONTAL
                    )
                )
                addItemDecoration(
                    CustomDividerItemDecoration(
                        requireContext(),
                        DividerItemDecoration.VERTICAL
                    )
                )
            }
        }

        with(viewModel) {
            players.observe(viewLifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    binding.recyclerViewScore.layoutManager =
                        GridLayoutManager(requireContext(), it.size + 1)
                }
            }

            scores.observe(viewLifecycleOwner) {
                if (it != null) {
                    adapter.updateDataset(it)
                    binding.root.post {
                        binding.root.fullScroll(View.FOCUS_DOWN)
                    }
                }
            }

            gameTitle.observe(viewLifecycleOwner) {
                if (it != null) {
                    (requireActivity() as AppCompatActivity).supportActionBar?.subtitle = it
                }
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

    override fun onStop() {
        super.onStop()
        (requireActivity() as AppCompatActivity).supportActionBar?.subtitle = null
    }

    companion object {
        private fun dialogHelp(context: Context) {
            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_title_help_game_scoring)
                .setMessage(R.string.dialog_message_help_game_scoring)
                .setPositiveButton(R.string.okay, null)
                .show()
        }

        fun dialogEditDelete(
            context: Context,
            round: Int,
            editAction: () -> Unit,
            deleteAction: () -> Unit
        ) {
            MaterialAlertDialogBuilder(context)
                .setTitle("Edit or delete round $round")
                .setSingleChoiceItems(R.array.edit_or_delete, -1) { dialog, which ->
                    when (which) {
                        0 -> editAction()
                        1 -> deleteAction()
                        else -> throw IllegalStateException("Invalid choice: $which")
                    }
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        private fun dialogEditDeleteTip(context: Context) {
            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_title_edit_delete_tip)
                .setMessage(R.string.dialog_message_edit_delete_tip)
                .setPositiveButton(R.string.okay, null)
                .show()
        }
    }
}