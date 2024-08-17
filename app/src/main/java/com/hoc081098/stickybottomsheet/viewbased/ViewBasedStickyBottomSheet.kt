package com.hoc081098.stickybottomsheet.viewbased

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hoc081098.stickybottomsheet.R
import com.hoc081098.stickybottomsheet.bottomSheetDialogDefaultHeight
import com.hoc081098.stickybottomsheet.databinding.FragmentViewBasedStickyBottomSheetBinding
import com.hoc081098.viewbindingdelegate.viewBinding

/**
 * [Credits](https://github.com/dorianpavetic/StickyBottomSheet).
 */
class ViewBasedStickyBottomSheet :
    BottomSheetDialogFragment(R.layout.fragment_view_based_sticky_bottom_sheet) {
    private val binding by viewBinding<FragmentViewBasedStickyBottomSheetBinding>()

    private var buttonHeight = 0
    private var collapsedMargin = 0
    private var expandedHeight = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sheetRecyclerview.run {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            adapter = ItemAdapter(strings = List(100) { "Item $it" })
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { setupRatio(dialog) }
        dialog.behavior.addBottomSheetCallback(
            object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) = Unit
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.sheetButton.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        if (slideOffset > 0) {
                            //Sliding happens from 0 (Collapsed) to 1 (Expanded) - if so, calculate margins
                            this.topMargin =
                                (((expandedHeight - buttonHeight) - collapsedMargin) * slideOffset + collapsedMargin).toInt()
                        } else {
                            // If not sliding above expanded, set initial margin
                            this.topMargin = collapsedMargin
                        }
                    }
                }
            }
        )
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return

        // Retrieve bottom sheet parameters
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            .apply { state = BottomSheetBehavior.STATE_COLLAPSED }

        // Calculate expanded height and peek height
        expandedHeight = requireActivity().bottomSheetDialogDefaultHeight
        val peekHeight =
            (expandedHeight * 0.7f).toInt() // Peek height to 70% of expanded height (Change based on your view)

        // Setup bottom sheet
        bottomSheet.updateLayoutParams {
            this.height = expandedHeight
        }
        bottomSheetBehavior.skipCollapsed = false
        bottomSheetBehavior.peekHeight = peekHeight
        bottomSheetBehavior.isHideable = true

        // Calculate button margin from top
        buttonHeight =
            binding.sheetButton.height + 40 // How tall is the button + experimental distance from bottom (Change based on your view)
        collapsedMargin = peekHeight - buttonHeight // Button margin in bottom sheet collapsed state

        // Set initial top margin of button
        binding.sheetButton.updateLayoutParams<ConstraintLayout.LayoutParams> {
            this.topMargin = collapsedMargin
        }

        // OPTIONAL - Setting up margins
        binding.sheetRecyclerview.updateLayoutParams<ConstraintLayout.LayoutParams> {
            val k =
                (buttonHeight - 60) / buttonHeight.toFloat() // 60 is amount that you want to be hidden behind button
            this.bottomMargin =
                (k * buttonHeight).toInt() // Recyclerview bottom margin (from button)
        }
    }

    companion object {
        fun newInstance(): ViewBasedStickyBottomSheet = ViewBasedStickyBottomSheet()
    }
}

