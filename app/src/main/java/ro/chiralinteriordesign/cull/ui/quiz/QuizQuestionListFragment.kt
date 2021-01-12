package ro.chiralinteriordesign.cull.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.QuizQuestionListFragmentBinding
import ro.chiralinteriordesign.cull.model.quiz.QuizQuestion
import ro.chiralinteriordesign.cull.ui.BackButtonListener
import ro.chiralinteriordesign.cull.utils.pushFragment

class QuizQuestionListFragment : Fragment(), BackButtonListener {

    private val viewModel: QuizViewModel by activityViewModels()
    private var binding: QuizQuestionListFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = QuizQuestionListFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.questions.observe(viewLifecycleOwner) { qs ->
            val questions = qs ?: run {
                parentFragmentManager.popBackStack()
                return@observe
            }
            binding?.tabs?.let { tabLayout ->
                tabLayout.removeAllTabs()
                for (i in questions.indices) {
                    val tab = tabLayout
                        .newTab()
                        .setText(getString(R.string.quiz_step_format, i + 1))
                    tabLayout.addTab(tab)
                }
            }
            val index = viewModel.currentQuestion.value ?: 0
            setCurrentQuestion(questions[index], index)
        }

        viewModel.currentQuestion.observe(viewLifecycleOwner) {
            val questions = viewModel.questions.value ?: return@observe
            setCurrentQuestion(questions[it], it)
        }

        viewModel.result.observe(viewLifecycleOwner) {
            if (it != null) {
                val fr = QuizResultFragment.newInstance(it)
                parentFragmentManager.pushFragment(fr)
            }
        }
    }

    private fun setCurrentQuestion(question: QuizQuestion, index: Int) {
        binding?.tabs?.let { it.selectTab(it.getTabAt(index)) }
        childFragmentManager
            .beginTransaction()
            .replace(R.id.questionContainer, QuizQuestionFragment.newInstance(question))
            .commit()
    }

    override fun onBackPressed(): Boolean {
        return viewModel.goBackOneQuestion()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}