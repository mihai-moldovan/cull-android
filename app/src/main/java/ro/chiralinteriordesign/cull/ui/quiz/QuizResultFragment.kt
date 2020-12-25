package ro.chiralinteriordesign.cull.ui.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.FragmentQuizResultBinding
import ro.chiralinteriordesign.cull.model.quiz.QuizResult

private const val ARG_RESULT = "result"

class QuizResultFragment : Fragment() {
    private lateinit var result: QuizResult

    private var binding: FragmentQuizResultBinding? = null

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
        binding = FragmentQuizResultBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding ?: return
        binding.titleView.text = result.title
        binding.textView.text = result.text
        Glide
            .with(binding.imageView)
            .load(result.photo)
            .into(binding.imageView)
        binding.btnRedo.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnContinue.setOnClickListener {
            requireActivity().finish()
        }
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