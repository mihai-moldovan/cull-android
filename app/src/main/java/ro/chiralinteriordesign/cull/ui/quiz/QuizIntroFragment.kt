package ro.chiralinteriordesign.cull.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}