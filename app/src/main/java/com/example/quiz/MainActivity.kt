package com.example.quiz

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quiz.ui.theme.QuizTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) {
                    App(viewModel()) // Pass the ViewModel to App()
                }
            }
        }
    }
}

@Composable
fun App(quizViewModel: QuizViewModel = viewModel()) {
    val questions = quizViewModel.questions // Access questions from the ViewModel
    val currentQuestion = stringResource(questions[quizViewModel.currentIndex.intValue].questionResId)
    val currentAnswer = stringResource(questions[quizViewModel.currentIndex.intValue].answerResId)

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable(route = "home") {
            HomeScreen(onNextScreen = {
                quizViewModel.initializeQuiz()
                navController.navigate("question")
            })
        }

        composable(route = "question") {
            QuestionScreen(
                questionText = currentQuestion,
                correctAnswer = currentAnswer,
                onSubmitAnswer = { isCorrect ->
                    if (isCorrect) quizViewModel.incrementCorrectCount() // Increment correct count

                    // Move to the next question or show the summary if at the end
                  if (quizViewModel.currentIndex.intValue < questions.size - 1) {
                        quizViewModel.nextQuestion() // Move to the next question
                        navController.navigate("question") { popUpTo("question") { inclusive = true } }
                    } else {
                        navController.navigate("summary") // Show summary if all questions are answered
                    }
                }
            )
        }
        composable(route = "summary") {
            SummaryScreen(correctCount = quizViewModel.correctCount.intValue, totalQuestions = questions.size) {
                quizViewModel.resetQuiz() // Reset quiz for a new session
                navController.navigate("home") {
                    popUpTo("summary") { inclusive = true } // Clear navigation stack
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onNextScreen: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.welcome),
            textAlign = TextAlign.Center,
            )
        Button(
            onClick = onNextScreen
        ) {
            Text(
                text = stringResource(R.string.start_quiz_button)
            )
        }
    }
}
    }

@Composable
fun QuestionScreen(
    questionText: String,
    correctAnswer: String,
    onSubmitAnswer: (Boolean) -> Unit
) {
    var answer by remember { mutableStateOf("") } // Mutable state to hold the user's answer input

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = questionText,
            textAlign = TextAlign.Center,
            )

        TextField(
            value = answer,
            onValueChange = { answer = it },
            label = { Text(text = stringResource(R.string.answer_field_label)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(onClick = { onSubmitAnswer(answer == correctAnswer) }) {
            Text(text = stringResource(R.string.submit_answer_button))        }
    }
}

@Composable
fun SummaryScreen(correctCount: Int, totalQuestions: Int, onRestartQuiz: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.quiz_summary),
            textAlign = TextAlign.Center,
        )

        Text(
//            text = "You answered $correctCount out of 3 question(s) correctly.",
            text = "You answered $correctCount out of $totalQuestions question(s) correctly.",
            textAlign = TextAlign.Center,
            )

        Button(onClick = onRestartQuiz) {
            Text(text = stringResource(R.string.restart_quiz_button))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun QuizPreview() {
    QuizTheme {
        App()
    }
}
