package com.hoc081098.stickybottomsheet.two_composeviews

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hoc081098.stickybottomsheet.R
import com.hoc081098.stickybottomsheet.bottomSheetDialogDefaultHeight
import com.hoc081098.stickybottomsheet.bottomSheetSlides
import com.hoc081098.stickybottomsheet.databinding.FragmentTwoComposeViewsStickyBottomSheetBinding
import com.hoc081098.stickybottomsheet.requireBottomSheetBehavior
import com.hoc081098.viewbindingdelegate.viewBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class TwoComposeViewsStickyBottomSheet :
    BottomSheetDialogFragment(R.layout.fragment_two_compose_views_sticky_bottom_sheet) {
    private val binding by viewBinding<FragmentTwoComposeViewsStickyBottomSheetBinding>()

    private val buttonHeightFlow = MutableStateFlow(0)
    private var collapsedMargin = 0
    private var expandedHeight = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = List(100) { "Item $it" }

        binding.composeView.setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)
        binding.composeView.setContent {
            val nestedScrollConnection = rememberNestedScrollInteropConnection()
            MaterialTheme {
                Surface(
                    modifier = Modifier.wrapContentHeight(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(nestedScrollConnection)
                    ) {
                        items(items = items) { item ->
                            Text(
                                modifier = Modifier.padding(16.dp),
                                text = item,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
            }
        }

        binding.sheetButtonComposeView
            .setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)
        binding.sheetButtonComposeView.setContent {
            MaterialTheme {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { buttonHeightFlow.value = it.height },
                    onClick = { dismiss() },
                ) {
                    Text("Dismiss")
                }
            }
        }

        observeBottomSheetSlides()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeBottomSheetSlides() {
        val bottomSheetBehavior = requireBottomSheetBehavior()

        val topMarginFlow = buttonHeightFlow
            .flatMapLatest { buttonHeight ->
                bottomSheetBehavior
                    .bottomSheetSlides()
                    .buffer(Channel.UNLIMITED)
                    .onStart { emit(bottomSheetBehavior.calculateSlideOffset()) }
                    .map { slideOffset ->
                        check(collapsedMargin > 0) { "collapsedMargin must be greater than 0" }
                        check(expandedHeight > 0) { "collapsedMargin must be greater than 0" }

                        if (slideOffset > 0) {
                            // Sliding happens from 0 (Collapsed) to 1 (Expanded) - if so, calculate margins
                            (((expandedHeight - buttonHeight) - collapsedMargin) * slideOffset + collapsedMargin).toInt()
                        } else {
                            // If not sliding above expanded, set initial margin
                            collapsedMargin
                        }
                    }
            }
            .distinctUntilChanged()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                topMarginFlow.collect { newTopMargin ->
                    binding.sheetButtonComposeView
                        .updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            this.topMargin = newTopMargin
                        }
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { setupRatio(dialog) }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return

        // Calculate expanded height and peek height
        expandedHeight = requireActivity().bottomSheetDialogDefaultHeight
        val peekHeight =
            (expandedHeight * 0.7f).toInt() // Peek height to 70% of expanded height (Change based on your view)

        // Setup bottom sheet
        bottomSheet.updateLayoutParams { this.height = expandedHeight }
        bottomSheetDialog.behavior.run {
            state = BottomSheetBehavior.STATE_COLLAPSED
            skipCollapsed = false
            this.peekHeight = peekHeight
            isHideable = true
        }

        // Calculate button margin from top
        buttonHeightFlow.value =
            binding.sheetButtonComposeView.height + 40 // How tall is the button + experimental distance from bottom (Change based on your view)
        collapsedMargin =
            peekHeight - buttonHeightFlow.value // Button margin in bottom sheet collapsed state

        // Set initial top margin of button
        binding.sheetButtonComposeView
            .updateLayoutParams<ViewGroup.MarginLayoutParams> { this.topMargin = collapsedMargin }
    }

    companion object {
        fun newInstance(): TwoComposeViewsStickyBottomSheet = TwoComposeViewsStickyBottomSheet()
    }
}