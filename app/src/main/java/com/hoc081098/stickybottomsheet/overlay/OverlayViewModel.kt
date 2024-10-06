package com.hoc081098.stickybottomsheet.overlay

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object OverlayRepository {
    private val isVisibleStateFlow = MutableStateFlow(false)

    fun observe() = isVisibleStateFlow.asStateFlow()

    fun toggle() = isVisibleStateFlow.update { !it }
}

class OverlayViewModel : ViewModel() {
    val isVisibleFlow = OverlayRepository.observe()

    fun toggle() = OverlayRepository.toggle()
}

