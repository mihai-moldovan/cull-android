package ro.chiralinteriordesign.cull.ui.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.FragmentQuizIntroBinding
import ro.chiralinteriordesign.cull.utils.pushFragment


class QuizIntroFragment : Fragment() {

    private var binding: FragmentQuizIntroBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizIntroBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding ?: return
        binding.btnStart.setOnClickListener {
            parentFragmentManager.pushFragment(QuizQuestionListFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}