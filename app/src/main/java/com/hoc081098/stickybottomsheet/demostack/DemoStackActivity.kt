package com.hoc081098.stickybottomsheet.demostack

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.AnimRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.eventFlow
import androidx.lifecycle.lifecycleScope
import com.hoc081098.stickybottomsheet.MainActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun ComponentActivity.logLifecycleEvents() {
    lifecycle.eventFlow
        .onEach { println(">>> ${this@logLifecycleEvents::class.java.simpleName} Event: $it") }
        .launchIn(lifecycleScope)
}

@JvmInline
value class OverrideTransitionType private constructor(val value: Int) {
    companion object {
        val Open = OverrideTransitionType(0)
        val Close = OverrideTransitionType(1)
    }
}

fun Activity.overridePendingTransitionCompat(
    overrideType: OverrideTransitionType,
    @AnimRes enterAnim: Int,
    @AnimRes exitAnim: Int,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(
            when (overrideType) {
                OverrideTransitionType.Open -> Activity.OVERRIDE_TRANSITION_OPEN
                OverrideTransitionType.Close -> Activity.OVERRIDE_TRANSITION_CLOSE
                else -> error("Unsupported override type: $overrideType")
            },
            enterAnim,
            exitAnim,
        )
    } else {
        @Suppress("DEPRECATION")
        overridePendingTransition(enterAnim, exitAnim)
    }
}

class DemoStackActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text("Demo stack")
                            }
                        )
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Red.copy(alpha = 0.25f))
                            .padding(paddingValues)
                    ) {
                        Button(onClick = {
                            startActivity(Child1Activity.createIntent(this@DemoStackActivity))
                        }) {
                            Text("To child 1")
                        }
                    }
                }
            }
        }

        logLifecycleEvents()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        overridePendingTransitionCompat(
            OverrideTransitionType.Open,
            0,
            0,
        )
        println(">>> onNewIntent: $intent")
    }

    companion object {
        fun createIntent(context: Context) = Intent(context, DemoStackActivity::class.java)
    }
}

class Child1Activity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text("Child 1")
                            }
                        )
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Green.copy(alpha = 0.25f))
                            .padding(paddingValues)
                    ) {
                        Button(onClick = {
                            startActivity(MainActivity.createIntent(this@Child1Activity))
                        }) {
                            Text("To Main")
                        }
                    }
                }
            }
        }

        logLifecycleEvents()
    }

    companion object {
        fun createIntent(context: Context) = Intent(context, Child1Activity::class.java)
    }
}