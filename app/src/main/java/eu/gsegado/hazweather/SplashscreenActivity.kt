package eu.gsegado.hazweather

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import eu.gsegado.hazweather.home.HomeActivity
import kotlinx.android.synthetic.main.activity_splashscreen.*

class SplashscreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        animation_view.addAnimatorUpdateListener {
            if (it.animatedFraction >= 0.25) {
                startActivity(Intent(this, HomeActivity::class.java))
                it.cancel()
                finish()
            }
        }
    }

    override fun onDestroy() {
        animation_view.removeAllAnimatorListeners()
        animation_view.removeAllUpdateListeners()
        super.onDestroy()
    }
}
