package ro.chiralinteriordesign.cull.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.FragmentQuizQuestionListBinding
import ro.chiralinteriordesign.cull.databinding.ItemQuizTabBinding
import ro.chiralinteriordesign.cull.model.quiz.QuizQuestion

class QuizQuestionListFragment : Fragment() {

    private val viewModel: QuizViewModel by activityViewModels()
    private var binding: FragmentQuizQuestionListBinding? = null
    private val adapter = QuizTabAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizQuestionListBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.tabs.adapter = adapter


        viewModel.questions.observe(viewLifecycleOwner) { qs ->
            val questions = qs ?: run {
                parentFragmentManager.popBackStack()
                return@observe
            }
            adapter.itemsCount = questions.size
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
                parentFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(
                        R.anim.push_enter,
                        R.anim.push_exit,
                        R.anim.pop_enter,
                        R.anim.pop_exit
                    )
                    .replace(R.id.childContainer, fr, fr.javaClass.name)
                    .commit()
            }
        }
    }

    private fun setCurrentQuestion(question: QuizQuestion, index: Int) {
        adapter.selection = index
        childFragmentManager
            .beginTransaction()
            .replace(R.id.questionContainer, QuizQuestionFragment.newInstance(question))
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

private class QuizTabViewHolder(private val binding: ItemQuizTabBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun setData(position: Int, checked: Boolean) {
        binding.textView.text =
            binding.textView.resources.getString(R.string.quiz_step_format, position + 1)
        binding.textView.isChecked = checked
    }
}

private class QuizTabAdapter : RecyclerView.Adapter<QuizTabViewHolder>() {

    var itemsCount: Int = 0
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    var selection: Int = 0
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }


    override fun getItemCount(): Int = itemsCount

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizTabViewHolder {
        return QuizTabViewHolder(
            ItemQuizTabBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: QuizTabViewHolder, position: Int) {
        holder.setData(position, position == selection)
    }

}