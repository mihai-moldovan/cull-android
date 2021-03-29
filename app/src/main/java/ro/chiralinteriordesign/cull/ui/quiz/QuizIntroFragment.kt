package ro.chiralinteriordesign.cull.ui.quiz

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayout
import ro.chiralinteriordesign.cull.databinding.QuizIntroFragmentBinding
import ro.chiralinteriordesign.cull.utils.pushFragment


class QuizIntroFragment : Fragment() {

    private var binding: QuizIntroFragmentBinding? = null
    private val viewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = QuizIntroFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding ?: return
        binding.btnStart.setOnClickListener {
            viewModel.resetQuiz()
            parentFragmentManager.pushFragment(QuizQuestionListFragment())
        }

        val v = binding.grayscaleImageView
        val matrix = ColorMatrix()
        matrix.setSaturation(0f) //0 means grayscale
        val cf = ColorMatrixColorFilter(matrix)
        v.colorFilter = cf
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onResume() {
        super.onResume()
        binding?.grayscaleImageView?.let { iv ->
            iv.alpha = 1.0f
            iv.visibility = View.VISIBLE
            iv.animate()
                .alpha(0f)
                .setDuration(1000)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        iv.visibility = View.GONE
                    }
                })
                .setStartDelay(1000)
        }
    }

}