package ro.chiralinteriordesign.cull.model.quiz

import java.io.Serializable
import java.util.*

/**
 * Created by Mihai Moldovan on 21/12/2020.
 */
data class Quiz(
    val id: Int,
    val createdAt: Date,
    val name: String,
    val questions: List<QuizQuestion>,
    val results: List<QuizResult>,
) : Serializable


data class QuizQuestion(
    val id: Int,
    val question: String,
    val position: Int,
    val answers: List<QuizAnswer>
) : Serializable

data class QuizAnswer(
    val id: Int,
    val photo: String,
    val attributedResultId: Int
) : Serializable

data class QuizResult(
    val id: Int,
    val title: String,
    val text: String,
    val photo: String,
) : Serializable