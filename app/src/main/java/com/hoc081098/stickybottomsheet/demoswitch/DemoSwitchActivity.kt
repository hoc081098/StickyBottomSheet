package com.hoc081098.stickybottomsheet.demoswitch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DemoSwitchViewModel : ViewModel() {
    private val _checked = MutableStateFlow(true)

    var checked_ by mutableStateOf(true)

    val checkedFlow = _checked.asStateFlow()
    fun setChecked(value: Boolean) {
        _checked.value = value
    }

    init {
        snapshotFlow { checked_ }
            .onEach {
                println("[1] Checked: $it")
            }
            .launchIn(viewModelScope)

        _checked
            .onEach {
                println("[2] Checked: $it")
            }
            .launchIn(viewModelScope)
    }
}

class DemoSwitchActivity : AppCompatActivity() {
    private val vm by viewModels<DemoSwitchViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val checked by vm.checkedFlow.collectAsStateWithLifecycle()

            MaterialTheme {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("Demo Switch") }
                        )
                    },
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Switch(
                                checked = checked,
                                onCheckedChange = vm::setChecked,
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun createIntent(context: Context) =
            Intent(context, DemoSwitchActivity::class.java)
    }
}