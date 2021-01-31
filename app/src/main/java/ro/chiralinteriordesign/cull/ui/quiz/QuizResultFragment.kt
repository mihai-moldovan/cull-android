package ro.chiralinteriordesign.cull.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import ro.chiralinteriordesign.cull.databinding.QuizResultFragmentBinding
import ro.chiralinteriordesign.cull.model.quiz.QuizResult

private const val ARG_RESULT = "result"

class QuizResultFragment : Fragment() {
    private lateinit var result: QuizResult

    private var binding: QuizResultFragmentBinding? = null
    private val viewModel: QuizViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            result = it.getSerializable(ARG_RESULT) as QuizResult
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = QuizResultFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding ?: return
        binding.titleView.text = result.title
        binding.textView.text = result.text
        Glide
            .with(binding.imageView)
            .load(result.photoAbsoluteURL)
            .into(binding.imageView)

        binding.btnRedo.setOnClickListener {
            viewModel.resetQuiz()
            parentFragmentManager.popBackStack()
        }

        binding.btnContinue.setOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.resetQuiz()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(result: QuizResult) =
            QuizResultFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_RESULT, result)
                }
            }
    }
}