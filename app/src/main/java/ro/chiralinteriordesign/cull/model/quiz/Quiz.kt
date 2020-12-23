package ro.chiralinteriordesign.cull.model.quiz

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

/**
 * Created by Mihai Moldovan on 21/12/2020.
 */
data class Quiz(
    @SerializedName("id") val id: Int,
    @SerializedName("create_at") val createdAt: Date,
    @SerializedName("name") val name: String,
    @SerializedName("questions") val questions: List<QuizQuestion>,
    @SerializedName("results") val results: List<QuizResult>,
) : Serializable


data class QuizQuestion(
    @SerializedName("id") val id: Int,
    @SerializedName("question") val question: String,
    @SerializedName("position") val position: Int,
    @SerializedName("answers") val answers: List<QuizAnswer>
) : Serializable

data class QuizAnswer(
    @SerializedName("id") val id: Int,
    @SerializedName("photo") val photo: String,
    @SerializedName("attributedResultId") val attributedResultId: Int
) : Serializable

data class QuizResult(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("text") val text: String,
    @SerializedName("photo") val photo: String,
) : Serializable