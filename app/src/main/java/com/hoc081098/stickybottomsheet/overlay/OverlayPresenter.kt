package com.hoc081098.stickybottomsheet.overlay

import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.LazyThreadSafetyMode.NONE

class OverlayPresenter constructor(
    private val activity: ComponentActivity,
) {
    init {
        println(">>> OverlayPresenter created: $this")
    }

    private val viewModel by activity.viewModels<OverlayViewModel>()
    private val rootView by lazy(NONE) {
        activity.findViewById<ViewGroup>(android.R.id.content)
    }

    private var job: Job? = null

    fun attach(
        lifecycleOwner: LifecycleOwner,
    ) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .isVisibleFlow
                    .collect(::onOverlayVisibilityChanged)
            }
        }
    }

    fun removeOverlay() {
        job?.cancel()
        job = null
        onOverlayVisibilityChanged(false)
    }

    private fun onOverlayVisibilityChanged(visible: Boolean) {
        println("onOverlayVisibilityChanged: $visible")

        if (visible) {
            rootView.findViewWithTag<OverlayComposeView>(OverlayComposeView.TAG)?.let { return }

            val myComposeView = OverlayComposeView(activity).apply {
                tag = OverlayComposeView.TAG
            }
            rootView.addView(
                myComposeView,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )

            myComposeView.bringToFront()
            rootView.requestLayout()
        } else {
            rootView
                .findViewWithTag<OverlayComposeView>(OverlayComposeView.TAG)
                ?.let { rootView.removeView(it) }
        }
    }
}