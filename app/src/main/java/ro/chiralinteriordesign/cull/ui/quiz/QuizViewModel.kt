package ro.chiralinteriordesign.cull.ui.quiz

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.model.quiz.QuizAnswer
import ro.chiralinteriordesign.cull.model.quiz.QuizQuestion
import ro.chiralinteriordesign.cull.model.quiz.QuizResult
import ro.chiralinteriordesign.cull.services.ResultWrapper
import kotlin.collections.set

/**
 * Created by Mihai Moldovan on 24/12/2020.
 */
class QuizViewModel : ViewModel() {

    val currentQuestion = MutableLiveData(0)

    private val quizRepo = App.instance.dataRepository.quizRepository
    private val userRepo = App.instance.dataRepository.userRepository

    private val quiz = liveData(Dispatchers.IO) {
        when (val result = quizRepo.getQuiz()) {
            is ResultWrapper.Success -> emit(result.value)
            else -> emit(null)
        }
    }

    val questions = MediatorLiveData<List<QuizQuestion>?>().apply {
        addSource(quiz) { postValue(it?.questions?.sortedBy { it.position }) }
    }

    private val answers = hashMapOf<Int, QuizAnswer>()
    val result = MutableLiveData<QuizResult?>()

    fun saveAnswer(answer: QuizAnswer, question: QuizQuestion) {
        answers[question.id] = answer
        questions.value?.let { questions ->
            for ((i, q) in questions.withIndex()) {
                if (answers[q.id] == null) {
                    currentQuestion.postValue(i)
                    return
                }
            }
            //all answers have results, post results
            computeResult()
        }
    }

    private fun computeResult() {
        val results = hashMapOf<Int, Int>()
        for (answer in answers.values) {
            results[answer.attributedResultId] = (results[answer.attributedResultId] ?: 0) + 1
        }
        results.maxByOrNull { it.value }?.value?.let { resultId ->
            quiz.value?.let { quiz ->
                quiz.results.firstOrNull { it.id == resultId }?.let {
                    result.postValue(it)
                    userRepo.currentUser = userRepo.currentUser.copy(quizResultId = it.id)
                }
            }
        }
    }

    fun resetQuiz() {
        answers.clear()
        result.value = null
    }
}