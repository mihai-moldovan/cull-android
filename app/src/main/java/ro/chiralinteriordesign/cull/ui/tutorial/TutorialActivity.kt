package ro.chiralinteriordesign.cull.ui.tutorial

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.Preferences
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.ActivityTutorialBinding
import ro.chiralinteriordesign.cull.databinding.TutorialItemBinding
import ro.chiralinteriordesign.cull.ui.quiz.QuizActivity

class TutorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTutorialBinding

    val images = listOf(
        R.drawable.onboarding_1,
        R.drawable.onboarding_2,
        R.drawable.onboarding_3,
    )
    val titles = listOf(
        R.string.tutorial_title_1,
        R.string.tutorial_title_2,
        R.string.tutorial_title_3,
    )

    val texts = listOf(
        R.string.tutorial_text_1,
        R.string.tutorial_text_2,
        R.string.tutorial_text_3,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.viewPager.adapter = Adapter(images)
        if (titles.size != texts.size || titles.size != images.size) {
            error("Images size and texts size do not match")
        }
        TabLayoutMediator(binding.dotsIndicator, binding.viewPager) { _, _ -> }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            private var currentPos = 0

            init {
                updateTexts()
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                val newPos = if (positionOffset > 0.5) position + 1 else position
                if (newPos != currentPos) {
                    currentPos = newPos
                    updateTexts()
                }
            }

            private fun updateTexts() {
                binding.titleView.setText(titles[currentPos])
                binding.textView.setText(texts[currentPos])
            }
        })

        binding.btnSkip.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        App.instance.preferences.put(Preferences.Key.TUTORIAL_SEEN, System.currentTimeMillis())
    }

    class ViewHolder(private val binding: TutorialItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @DrawableRes
        var page: Int? = null
            set(newValue) {
                field = newValue
                field?.let {
                    binding.imageView.setImageResource(it)
                } ?: run {
                    binding.imageView.setImageBitmap(null)
                }
            }
    }

    class Adapter(private val data: List<Int>) : RecyclerView.Adapter<ViewHolder>() {
        override fun getItemCount(): Int = data.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                TutorialItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.page = data[position]
        }
    }

}