package com.hoc081098.stickybottomsheet

import android.os.Looper
import android.view.View
import androidx.annotation.CheckResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

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