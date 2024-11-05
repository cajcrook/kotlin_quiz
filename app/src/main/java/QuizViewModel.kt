package com.example.quiz

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlin.random.Random


class QuizViewModel : ViewModel() {
    data class Question(
        val questionResId: Int,
        val answerResId: Int
    )

    private val _questions = mutableStateListOf(
        Question(R.string.question1, R.string.answer1),
        Question(R.string.question2, R.string.answer2),
        Question(R.string.question3, R.string.answer3),
        Question(R.string.question4, R.string.answer4),
        Question(R.string.question5, R.string.answer5)
    )

    val questions: List<Question> get() = _questions
    var currentIndex = mutableIntStateOf(0) // Track the index of the current question
    var correctCount = mutableIntStateOf(0) // Track the number of correct answers

    fun initializeQuiz() {
        _questions.shuffle() // Shuffle questions for a new session
        currentIndex.value = Random.nextInt(0, _questions.size) // Set currentIndex to a random index
    }

    fun incrementCorrectCount() {
        correctCount.intValue++
    }

    fun resetQuiz() {
        currentIndex.intValue = 0
        correctCount.intValue = 0
        _questions.shuffle() // Reshuffle questions for a new session
    }

    fun nextQuestion() {
        if (currentIndex.intValue < _questions.size - 1) {
            currentIndex.intValue++
        }
    }
}
