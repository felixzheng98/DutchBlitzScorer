package com.zhengineer.dutchblitzscorer.ui.fragments

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zhengineer.dutchblitzscorer.R
import com.zhengineer.dutchblitzscorer.databinding.FragmentInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfoFragment : Fragment() {
    private lateinit var binding: FragmentInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.infoScreen.apply {
            text = Html.fromHtml(
                getString(R.string.info_screen),
                Html.FROM_HTML_MODE_COMPACT
            )
            isClickable = true
            movementMethod = LinkMovementMethod.getInstance()
        }
    }
}