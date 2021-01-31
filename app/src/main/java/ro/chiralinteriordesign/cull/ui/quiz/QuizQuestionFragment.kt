package ro.chiralinteriordesign.cull.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.QuizItemAnswerBinding
import ro.chiralinteriordesign.cull.databinding.QuizQuestionFragmentBinding
import ro.chiralinteriordesign.cull.model.quiz.QuizAnswer
import ro.chiralinteriordesign.cull.model.quiz.QuizQuestion

private const val ARG_QUESTION = "question"

/**
 * A simple [Fragment] subclass.
 * Use the [QuizQuestionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuizQuestionFragment : Fragment() {
    private lateinit var question: QuizQuestion
    private var binding: QuizQuestionFragmentBinding? = null
    private val viewModel: QuizViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            question = it.getSerializable(ARG_QUESTION) as QuizQuestion
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = QuizQuestionFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding ?: return
        binding.titleView.text = question.question
        val adapter = QuizQuestionAdapter(question.answers) {
            viewModel.saveAnswer(it, question)
        }
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance(question: QuizQuestion) =
            QuizQuestionFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_QUESTION, question)
                }
            }
    }
}

class QuizAnswerViewHolder(val binding: QuizItemAnswerBinding) :
    RecyclerView.ViewHolder(binding.root) {

    var data: QuizAnswer? = null
        set(newValue) {
            field = newValue
            field?.let {
                Glide.with(binding.imageView)
                    .load(it.photoAbsoluteURL)
                    .apply(
                        RequestOptions.bitmapTransform(
                            RoundedCorners(
                                binding.imageView.resources.getDimensionPixelSize(
                                    R.dimen.quiz_answer_rounded_corner
                                )
                            )
                        )
                    )
                    .into(binding.imageView)
            }
        }
}

class QuizQuestionAdapter(
    private val answers: List<QuizAnswer>,
    private val itemSelectedListener: (QuizAnswer) -> Unit
) :
    RecyclerView.Adapter<QuizAnswerViewHolder>() {

    override fun getItemCount(): Int {
        return answers.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizAnswerViewHolder {
        val binding =
            QuizItemAnswerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        val holder = QuizAnswerViewHolder(binding)
        holder.itemView.setOnClickListener {
            holder.data?.let { data ->
                itemSelectedListener.invoke(data)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: QuizAnswerViewHolder, position: Int) {
        holder.data = answers[position]
    }
}