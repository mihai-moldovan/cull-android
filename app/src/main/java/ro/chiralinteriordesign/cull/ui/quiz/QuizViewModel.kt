package ro.chiralinteriordesign.cull.ui.quiz

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    fun goBackOneQuestion(): Boolean {
        return currentQuestion.value?.let {
            if (it > 0) {
                currentQuestion.postValue(it - 1)
                true
            } else {
                false
            }
        } ?: false
    }

    private fun computeResult() {
        val questions = questions.value ?: return
        val results = hashMapOf<String, Float>()
        for ((qId, answer) in answers) {
            results[answer.result] = (results[answer.result]
                ?: 0f) + (questions.firstOrNull { it.id == qId }?.weight ?: 1.0f)
        }
        results.maxByOrNull { it.value }?.key?.let { resultKey ->
            quiz.value?.let { quiz ->
                quiz.results.firstOrNull { it.key == resultKey }?.let {
                    result.postValue(it)
                    userRepo.currentUser = userRepo.currentUser.copy(quizResult = it.key)
                    viewModelScope.launch {
                        userRepo.saveUserData(quizResult = it.key)
                    }
                }
            }
        }
    }

    fun resetQuiz() {
        answers.clear()
        currentQuestion.value = 0
        result.value = null
    }
}