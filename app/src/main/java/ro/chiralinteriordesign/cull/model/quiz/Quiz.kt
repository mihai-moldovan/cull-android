package ro.chiralinteriordesign.cull.model.quiz

import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.R
import java.io.Serializable

/**
 * Created by Mihai Moldovan on 21/12/2020.
 */
data class Quiz(
    val questions: List<QuizQuestion>,
    val results: List<QuizResult>,
) : Serializable


data class QuizQuestion(
    val id: Int,
    val question: String,
    val weight: Float,
    val position: Int,
    val answers: List<QuizAnswer>
) : Serializable

data class QuizAnswer(
    val id: Int,
    val photo: String,
    val result: String
) : Serializable {

    val photoAbsoluteURL: String
        get() = if (photo.startsWith("http")) photo else "${App.instance.resources.getString(R.string.api_url)}$photo"

}

data class QuizResult(
    val key: String,
    val title: String,
    val text: String,
    val photo: String,
) : Serializable {
    val photoAbsoluteURL: String
        get() = if (photo.startsWith("http")) photo else "${App.instance.resources.getString(R.string.api_url)}$photo"
}