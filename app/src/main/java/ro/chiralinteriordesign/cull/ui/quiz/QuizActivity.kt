package ro.chiralinteriordesign.cull.ui.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.ui.BaseActivity

class QuizActivity : BaseActivity() {

    private val viewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        if (savedInstanceState == null) {
            if (supportFragmentManager.findFragmentById(R.id.childContainer) == null) {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.childContainer, QuizIntroFragment())
                    .commitNow()
            }
        }
    }
}