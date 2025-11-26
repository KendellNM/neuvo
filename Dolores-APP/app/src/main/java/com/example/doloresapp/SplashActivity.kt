package com.example.doloresapp

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.doloresapp.data.local.TokenStore

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init token store to decide navigation
        TokenStore.init(applicationContext)

        // Edge-to-edge and content
        enableEdgeToEdge()
        setContentView(R.layout.start_layout)

        // Apply TOP, SIDE and BOTTOM insets so bottom-anchored elements don't clash with nav bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.start_root)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        // Views
        val title1: View? = findViewById(R.id.materialTextView)
        val heroImage: View? = findViewById(R.id.imageView1)
        val title2: View? = findViewById(R.id.materialTextView2)
        val shelfTop: View? = findViewById(R.id.mostrador1)
        val shelf: View? = findViewById(R.id.mostrador)
        val logo: View? = findViewById(R.id.imageView2)
        val bgBack: View? = findViewById(R.id.bgCircleBack)
        val bgFront: View? = findViewById(R.id.bgCircleFront)

        // Helpers
        val interp = FastOutSlowInInterpolator()
        fun dp(v: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, resources.displayMetrics)

        // Initial state
        listOfNotNull(title1, heroImage, title2, shelfTop, shelf, logo).forEach { v ->
            v.alpha = 0f
            v.translationY = dp(24f)
        }
        logo?.apply {
            scaleX = 0.88f
            scaleY = 0.88f
        }
        // Parallax for circles
        bgBack?.apply { alpha = 0f; translationY = -dp(18f) }
        bgFront?.apply { alpha = 0f; translationY = -dp(28f) }

        // Animate background circles first with subtle parallax
        bgBack?.animate()?.alpha(1f)?.translationY(0f)?.setDuration(600L)?.setInterpolator(interp)?.start()
        bgFront?.animate()?.alpha(1f)?.translationY(0f)?.setDuration(700L)?.setStartDelay(60L)?.setInterpolator(interp)?.start()

        // Staggered content with slight overlap
        val sequence = listOfNotNull(title1, title2, heroImage, shelfTop, shelf, logo)
        val baseDuration = 420L
        val step = 80L
        sequence.forEachIndexed { i, v ->
            v.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(baseDuration)
                .setStartDelay(180L + i * step)
                .setInterpolator(interp)
                .withEndAction {
                    if (v === logo) {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(300L).setInterpolator(interp).start()
                    }
                }
                .start()
        }

        // Navigate after the sequence
        val totalDelay = 180L + (sequence.size * step) + baseDuration + 300L
        window.decorView.postDelayed({
            val token = TokenStore.getToken()
            if (token.isNullOrBlank()) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                // Ir directo a HomeActivity (maneja roles)
                startActivity(Intent(this, com.example.doloresapp.presentation.ui.HomeActivity::class.java))
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, totalDelay)
    }
}
