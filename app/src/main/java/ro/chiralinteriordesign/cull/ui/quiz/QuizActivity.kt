package ro.chiralinteriordesign.cull.ui.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.ui.BaseActivity

class QuizActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        if (supportFragmentManager.findFragmentById(R.id.childContainer) == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.childContainer, QuizIntroFragment())
                .commitNow()
        }
    }

}