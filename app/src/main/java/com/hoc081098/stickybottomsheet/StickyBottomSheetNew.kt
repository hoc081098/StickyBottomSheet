package com.hoc081098.stickybottomsheet

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hoc081098.stickybottomsheet.databinding.FragmentStickyBottomSheetNewBinding
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class StickyBottomSheetNew : BottomSheetDialogFragment() {
    private var topMargin by mutableIntStateOf(0)
    private var buttonHeight = 0

    private var binding: FragmentStickyBottomSheetNewBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStickyBottomSheetNewBinding.inflate(inflater, container, false)

        val items = initString()

        binding!!.compsoeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        binding!!.compsoeView.setContent {
            val conn = rememberNestedScrollInteropConnection()
            MaterialTheme {
                Surface(
                    modifier = Modifier.wrapContentHeight(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .nestedScroll(conn)
                        ) {
                            items(items = items) { item ->
                                Text(text = item)
                            }
                        }
                    }
                }
            }
        }

        binding!!.sheetButton.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        binding!!.sheetButton.setContent {
            MaterialTheme {
                Button(
                    onClick = {
                       dismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Text("Close")
                }
            }
        }


        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dialog = requireDialog() as BottomSheetDialog
        dialog.behavior.bottomSheetSlides()
            .buffer(Channel.UNLIMITED)
            .onStart { emit(dialog.behavior.calculateSlideOffset()) }
            .map {slideOffset->
                if (slideOffset > 0) //Sliding happens from 0 (Collapsed) to 1 (Expanded) - if so, calculate margins
                    (((expandedHeight - buttonHeight) - collapsedMargin) * slideOffset + collapsedMargin).toInt()
                else  //If not sliding above expanded, set initial margin
                    collapsedMargin
            }
            .onEach {topMarginNN ->
                binding!!.sheetButton.updateLayoutParams {
                    (this as ViewGroup.MarginLayoutParams).topMargin = topMarginNN
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener { dialogInterface: DialogInterface -> setupRatio(dialogInterface as BottomSheetDialog) }

//        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(object :
//            BottomSheetCallback() {
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                topMargin = if (slideOffset > 0) //Sliding happens from 0 (Collapsed) to 1 (Expanded) - if so, calculate margins
//                    (((expandedHeight - buttonHeight) - collapsedMargin) * slideOffset + collapsedMargin).toInt()
//                else  //If not sliding above expanded, set initial margin
//                    collapsedMargin
//                binding!!.sheetButton.updateLayoutParams {
//                    (this as ViewGroup.MarginLayoutParams).topMargin = this@StickyBottomSheetNew.topMargin
//                }
//            }
//        })


        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                ?: return

        //Retrieve bottom sheet parameters
        BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
        val bottomSheetLayoutParams = bottomSheet.layoutParams
        bottomSheetLayoutParams.height = bottomSheetDialogDefaultHeight

        expandedHeight = bottomSheetLayoutParams.height
        val peekHeight =
            (expandedHeight * 0.5).toInt() //Peek height to 70% of expanded height (Change based on your view)

        //Setup bottom sheet
        bottomSheet.layoutParams = bottomSheetLayoutParams
        BottomSheetBehavior.from(bottomSheet).skipCollapsed = false
        BottomSheetBehavior.from(bottomSheet).peekHeight = peekHeight
        BottomSheetBehavior.from(bottomSheet).isHideable = true

        //Calculate button margin from top
        buttonHeight = binding!!.sheetButton.height
        check(buttonHeight > 0)
        buttonHeight += 40 //How tall is the button + experimental distance from bottom (Change based on your view)
        collapsedMargin = peekHeight - buttonHeight //Button margin in bottom sheet collapsed state
        topMargin = collapsedMargin
    }

    private val bottomSheetDialogDefaultHeight: Int
        //Calculates height for 90% of fullscreen
        get() = windowHeight * 90 / 100

    private val windowHeight: Int
        //Calculates window height for fullscreen use
        get() {
            val displayMetrics = DisplayMetrics()
            (requireContext() as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }

    private fun initString(): List<String> {
        val list: MutableList<String> = ArrayList()
        for (i in 0..999) list.add("Item $i")
        return list
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println(">>> onDestroyView")
    }

    companion object {
        private var collapsedMargin = 0
        private var buttonHeight = 0
        private var expandedHeight = 0

        fun newInstance(): StickyBottomSheetNew {
            return StickyBottomSheetNew()
        }
    }
}