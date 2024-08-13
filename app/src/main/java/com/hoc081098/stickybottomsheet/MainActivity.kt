package com.hoc081098.stickybottomsheet

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hoc081098.stickybottomsheet.databinding.ActivityMainBinding
import com.hoc081098.stickybottomsheet.viewbased.ViewBasedStickyBottomSheet
import com.hoc081098.viewbindingdelegate.viewBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding by viewBinding<ActivityMainBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonViewBased.setOnClickListener {
            ViewBasedStickyBottomSheet.newInstance().show(
                supportFragmentManager,
                ViewBasedStickyBottomSheet::class.java.simpleName
            )
        }
    }
}

class DemoBottomSheetFragment : BottomSheetDialogFragment() {
    private val yS = MutableStateFlow<Float>(-1F)
    private val load = MutableStateFlow(false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    Surface(
                        modifier = Modifier.nestedScroll(androidx.compose.ui.platform.rememberNestedScrollInteropConnection())
                    ) {
                        if (load.collectAsState().value) {
                            Box(modifier = Modifier.height(100.dp)) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            return@Surface
                        }
                        BottomSheetContent(
                            modifier = Modifier,
                            expected = 3,
                            onChange = { v ->
                                yS.value = v
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        val old = yS.value
        yS.value = -1F
        lifecycleScope.launch {
            if (old > 0) {
                requireBottomSheetBehavior().run {
                    peekHeight = old.toInt()
                    state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

            val h =
                yS.first { it >= 0 }

                    .toInt().coerceAtLeast(200)
            println(">>> h=$h")
            requireBottomSheetBehavior().run {
                peekHeight = h
                state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        load.value = true
        lifecycleScope.launch {
            delay(1000)
            load.value = false
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        println(">>> onCancel")
    }
}


fun BottomSheetDialogFragment.requireBottomSheetBehavior() =
    (dialog as BottomSheetDialog).behavior


@Composable
fun BottomSheetContent(
    expected: Int,
    onChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = listOf(
        androidx.compose.ui.graphics.Color.Red,
        androidx.compose.ui.graphics.Color.Green,
        androidx.compose.ui.graphics.Color.Blue,
        androidx.compose.ui.graphics.Color.Yellow,
        androidx.compose.ui.graphics.Color.Magenta,
        androidx.compose.ui.graphics.Color.Cyan,
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        itemsIndexed(List(10) { it }) { index, item ->
            Text(
                text = "Item $item",
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .background(colors[index % colors.size])
                    .then(
                        if (item == expected + 1) {
                            Modifier.onGloballyPositioned {
                                val localToRoot = it.localToRoot(Offset.Zero)
                                onChange(localToRoot.y)
                                println(">>> onGloballyPositioned: $localToRoot")
                            }
                        } else {
                            Modifier
                        }
                    ),
            )
        }
    }
}
