package com.zhengineer.dutchblitzscorer.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zhengineer.commons.util.Utils.hideKeyboard
import com.zhengineer.dutchblitzscorer.R
import com.zhengineer.dutchblitzscorer.common.result.RecordRoundResult
import com.zhengineer.dutchblitzscorer.databinding.FragmentRecordRoundBinding
import com.zhengineer.dutchblitzscorer.ui.rv.RecordRoundAdapter
import com.zhengineer.dutchblitzscorer.viewmodels.GameScoringViewModel

class RecordRoundFragment : Fragment(), MenuProvider {
    private val viewModel: GameScoringViewModel by hiltNavGraphViewModels(R.id.nav_graph_scoring)
    private val args: RecordRoundFragmentArgs by navArgs()

    private lateinit var binding: FragmentRecordRoundBinding
    private lateinit var adapter: RecordRoundAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordRoundBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // If there is roundNumber greater than 0 in navArgs, then we are editing an existing round
        if (args.roundNumber > 0) {
            (requireActivity() as AppCompatActivity).supportActionBar?.title =
                getString(R.string.title_edit_round, args.roundNumber)
        }

        with(binding) {
            recyclerViewRecord.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = RecordRoundAdapter().also { this@RecordRoundFragment.adapter = it }
            }

            fabSave.setOnClickListener {
                it?.hideKeyboard()
                if (viewModel.recordRoundAllZeroScores()) {
                    dialogZeroScoresWarning(requireContext()) {
                        recordRoundAndContinue()
                    }
                } else {
                    recordRoundAndContinue()
                }
            }
        }

        viewModel.recordRound.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                adapter.updateData(it)
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

    private fun recordRoundAndContinue() {
        val response = if (args.roundNumber > 0) {
            // if a round arg was specified, we are editing an existing round
            viewModel.recordRound(args.roundNumber)
        } else {
            // recording a new round
            viewModel.recordRound()
        }
        when (response) {
            is RecordRoundResult.Error -> dialogErrorSaving(
                requireContext(),
                response.error.errorDialogTitle,
                response.error.errorDialogMessage
            )
            RecordRoundResult.Success -> findNavController().navigate(
                RecordRoundFragmentDirections.actionRecordRoundFragmentToGameScoringFragment()
            )
        }
    }

    companion object {
        private fun dialogZeroScoresWarning(context: Context, positiveAction: () -> Unit) {
            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_title_zero_scores)
                .setMessage(R.string.dialog_message_zero_scores)
                .setPositiveButton(R.string.save) { _, _ ->
                    positiveAction()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        private fun dialogErrorSaving(
            context: Context, @StringRes title: Int, @StringRes description: Int
        ) {
            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton(R.string.okay, null)
                .show()
        }

        private fun dialogHelp(context: Context) {
            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_title_help_record_round)
                .setMessage(R.string.dialog_message_help_record_round)
                .setPositiveButton(R.string.okay, null)
                .show()
        }
    }
}