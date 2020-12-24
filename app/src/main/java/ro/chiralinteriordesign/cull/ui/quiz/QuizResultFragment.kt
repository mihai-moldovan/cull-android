package ro.chiralinteriordesign.cull.ui.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.model.quiz.QuizResult

private const val ARG_RESULT = "result"

class QuizResultFragment : Fragment() {
    private lateinit var result: QuizResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            result = it.getSerializable(ARG_RESULT) as QuizResult
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz_result, container, false)
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