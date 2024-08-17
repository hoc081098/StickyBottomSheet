package com.hoc081098.stickybottomsheet

import android.app.Activity
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import androidx.annotation.CheckResult
import androidx.annotation.Px
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

@get:Px
val Activity.bottomSheetDialogDefaultHeight: Int
    // Calculates height for 90% of fullscreen
    get() = windowHeight * 90 / 100

@get:Px
val Activity.windowHeight: Int
    get() {
        // Calculates window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

fun BottomSheetDialogFragment.requireBottomSheetBehavior() =
    (dialog as BottomSheetDialog).behavior

@CheckResult
fun BottomSheetBehavior<*>.bottomSheetSlides(): Flow<Float> = callbackFlow {
    check(Looper.getMainLooper() === Looper.myLooper()) {
        "Expected to be called on the main thread but was " + Thread.currentThread().name
    }
    val callback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) = Unit

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            trySend(slideOffset)
        }
    }
    addBottomSheetCallback(callback)
    awaitClose { removeBottomSheetCallback(callback) }
}.conflate()