package com.hoc081098.stickybottomsheet.two_composeviews

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.PEEK_HEIGHT_AUTO
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hoc081098.stickybottomsheet.R
import com.hoc081098.stickybottomsheet.bottomSheetSlides
import com.hoc081098.stickybottomsheet.databinding.FragmentTwoComposeViewsStickyBottomSheetBinding
import com.hoc081098.stickybottomsheet.requireBottomSheetBehavior
import com.hoc081098.viewbindingdelegate.viewBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class TwoComposeViewsStickyBottomSheet :
    BottomSheetDialogFragment(R.layout.fragment_two_compose_views_sticky_bottom_sheet) {
    private val binding by viewBinding<FragmentTwoComposeViewsStickyBottomSheetBinding>()

    private val buttonHeightFlow = MutableStateFlow(0)
    private val expandedHeightFlow = MutableStateFlow(0)
    private val peekHeightFlow = expandedHeightFlow
        .map { it * 0.7f }
        .filter { it > 0 }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            behavior.run {
                state = BottomSheetBehavior.STATE_COLLAPSED
                peekHeight = PEEK_HEIGHT_AUTO
                skipCollapsed = false
                isHideable = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = List(100) { "Item $it" }

        binding.composeView.setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)
        binding.composeView.setContent {
            val nestedScrollConnection = rememberNestedScrollInteropConnection()
            MaterialTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { expandedHeightFlow.value = it.height },
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

        binding.sheetButtonComposeView.setViewCompositionStrategy(
            DisposeOnViewTreeLifecycleDestroyed
        )
        binding.sheetButtonComposeView.setContent {
            MaterialTheme {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { buttonHeightFlow.value = it.height }
                        .padding(all = 24.dp),
                    onClick = { dismiss() },
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
                ) {
                    Text("Dismiss")
                }
            }
        }

        observeBottomSheetSlides()
        observePeekHeightFlow()
    }

    private fun observePeekHeightFlow() {
        val bottomSheetBehavior = requireBottomSheetBehavior()
        peekHeightFlow
            .onEach { bottomSheetBehavior.setPeekHeight(it.toInt(), true) }
            .launchIn(lifecycleScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeBottomSheetSlides() {
        val bottomSheetBehavior = requireBottomSheetBehavior()

        val topMarginFlow = combine(buttonHeightFlow, expandedHeightFlow, peekHeightFlow, ::Triple)
            .distinctUntilChanged()
            .flatMapLatest { (buttonHeight, expandedHeight, peekHeight) ->
                if (buttonHeight <= 0 || expandedHeight <= 0 || peekHeight <= 0f) {
                    return@flatMapLatest flowOf(-1)
                }

                // Button margin in bottom sheet collapsed state
                val collapsedMargin = peekHeight - buttonHeight

                bottomSheetBehavior
                    .bottomSheetSlides()
                    .buffer(Channel.UNLIMITED)
                    .onStart { emit(bottomSheetBehavior.calculateSlideOffset()) }
                    .map { slideOffset ->
                        if (slideOffset > 0) {
                            // Sliding happens from 0 (Collapsed) to 1 (Expanded) - if so, calculate margins
                            (((expandedHeight - buttonHeight) - collapsedMargin) * slideOffset + collapsedMargin).toInt()
                        } else {
                            // If not sliding above expanded, set initial margin
                            collapsedMargin.toInt()
                        }
                    }
            }
            .distinctUntilChanged()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                topMarginFlow.collect { newTopMargin ->
                    if (newTopMargin > 0) {
                        binding.sheetButtonComposeView.isVisible = true
                    } else {
                        binding.sheetButtonComposeView.isInvisible = true
                    }
                    binding.sheetButtonComposeView
                        .updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            this.topMargin = newTopMargin.coerceAtLeast(0)
                        }
                }
            }
        }
    }

    companion object {
        fun newInstance(): TwoComposeViewsStickyBottomSheet = TwoComposeViewsStickyBottomSheet()
    }
}
