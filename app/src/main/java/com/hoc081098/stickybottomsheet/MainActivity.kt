package com.hoc081098.stickybottomsheet

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hoc081098.stickybottomsheet.databinding.ActivityMainBinding
import com.hoc081098.stickybottomsheet.one_composeview.OneComposeViewStickyBottomSheet
import com.hoc081098.stickybottomsheet.overlay.OverlayActivity
import com.hoc081098.stickybottomsheet.overlay.OverlayPresenter
import com.hoc081098.stickybottomsheet.overlay.OverlayViewModel
import com.hoc081098.stickybottomsheet.two_composeviews.TwoComposeViewsStickyBottomSheet
import com.hoc081098.stickybottomsheet.viewbased.ViewBasedStickyBottomSheet
import com.hoc081098.viewbindingdelegate.viewBinding


class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding by viewBinding<ActivityMainBinding>()
    private val overlayViewModel by viewModels<OverlayViewModel>()

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
        binding.buttonOneComposeview.setOnClickListener {
            OneComposeViewStickyBottomSheet.newInstance().show(
                supportFragmentManager,
                OneComposeViewStickyBottomSheet::class.java.simpleName,
            )
        }
        binding.buttonTwoComposeview.setOnClickListener {
            TwoComposeViewsStickyBottomSheet.newInstance().show(
                supportFragmentManager,
                TwoComposeViewsStickyBottomSheet::class.java.simpleName
            )
        }
        binding.buttonToggle.setOnClickListener {
            overlayViewModel.toggle()
        }
        binding.buttonToFragment.setOnClickListener {
            startActivity(OverlayActivity.intent(this))
        }

        OverlayPresenter(this).attach(lifecycleOwner = this)
    }
}

