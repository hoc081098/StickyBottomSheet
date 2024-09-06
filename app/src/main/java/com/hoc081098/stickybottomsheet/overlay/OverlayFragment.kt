package com.hoc081098.stickybottomsheet.overlay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.hoc081098.stickybottomsheet.ActivityComponents
import com.hoc081098.stickybottomsheet.R
import com.hoc081098.stickybottomsheet.databinding.FragmentOverlayBinding
import com.hoc081098.viewbindingdelegate.viewBinding

class OverlayFragment : Fragment(R.layout.fragment_overlay) {
    private val overlayViewModel by viewModels<OverlayViewModel>()
    private val binding by viewBinding<FragmentOverlayBinding>()

    private val overlayPresenter
        get() = ActivityComponents.require(
            requireActivity(),
            OverlayPresenter::class
        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textView.setOnClickListener {
            overlayViewModel.toggle()
        }

        binding.button.setOnClickListener {
            overlayViewModel.toggle()
            parentFragmentManager.commit {
                replace(
                    R.id.fragmentContainerView,
                    SecondFragment(),
                    SecondFragment::class.java.simpleName
                )
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }

        overlayPresenter.attach(lifecycleOwner = viewLifecycleOwner)
    }

    override fun onDestroyView() {
        overlayPresenter.removeOverlay()
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = OverlayFragment()
    }
}

class SecondFragment : Fragment() {
    private val overlayPresenter
        get() = ActivityComponents.require(
            requireActivity(),
            OverlayPresenter::class
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                Box(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxSize()
                        .background(androidx.compose.ui.graphics.Color.Red),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    androidx.compose.material3.Text(text = "Second Fragment")
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        overlayPresenter.removeOverlay()
    }
}