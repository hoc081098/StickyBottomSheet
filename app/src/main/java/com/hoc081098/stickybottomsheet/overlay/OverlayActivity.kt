package com.hoc081098.stickybottomsheet.overlay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.hoc081098.stickybottomsheet.ActivityComponents
import com.hoc081098.stickybottomsheet.R
import com.hoc081098.stickybottomsheet.databinding.ActivityOverlayBinding
import com.hoc081098.viewbindingdelegate.viewBinding

class OverlayActivity : AppCompatActivity(R.layout.activity_overlay) {
    private val binding by viewBinding<ActivityOverlayBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ActivityComponents.put(this, OverlayPresenter(this))

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(
                    /* containerViewId = */ R.id.fragmentContainerView,
                    /* fragment = */ OverlayFragment.newInstance(),
                    /* tag = */ OverlayFragment::class.java.simpleName
                )
            }
        }
    }

    override fun onDestroy() {
        ActivityComponents.remove(this)
        super.onDestroy()
    }

    companion object {
        fun intent(context: Context) = Intent(context, OverlayActivity::class.java)
    }
}