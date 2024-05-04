package com.example.alphabaticalquiz


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Random

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var gamelayout: LinearLayout
    private lateinit var dashbord: LinearLayout
    private lateinit var questionTextView: TextView
    private lateinit var option1Button: Button
    private lateinit var option2Button: Button
    private lateinit var option3Button: Button
    private lateinit var option4Button: Button
    private lateinit var timerTextView: TextView


    private lateinit var questionNumberTextView: Button
    private var currentQuestionNumber: Int = 1

    private val random = Random()
    private var score = 0
    private lateinit var currentQuestion: Question
    private var timer: CountDownTimer? = null

    private lateinit var scoreTextView: TextView
    private lateinit var title: TextView
    private val correctColor: Int by lazy { ContextCompat.getColor(this, R.color.correctColor) }
    private val incorrectColor: Int by lazy { ContextCompat.getColor(this, R.color.incorrectColor) }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var highScoreTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("game_data", Context.MODE_PRIVATE)

        startButton = findViewById(R.id.start_button)
        gamelayout = findViewById(R.id.gamelayout)
        dashbord = findViewById(R.id.dashboard)
        timerTextView = findViewById(R.id.timerTextView)
        scoreTextView = findViewById(R.id.scoreTextView)
        title = findViewById(R.id.title)
        questionNumberTextView = findViewById(R.id.questionNumberTextView)

        gamelayout.visibility = View.GONE
        // Initialize highScoreTextView
        highScoreTextView = findViewById(R.id.highScoreTextView)

        // Update high score display
        updateHighScoreDisplay()

        startButton.setOnClickListener {
            val durationInMillis: Long = 10000
            val intervalInMillis: Long = 100

            object : CountDownTimer(durationInMillis, intervalInMillis) {
                override fun onTick(millisUntilFinished: Long) {

                    val secondsRemaining = (millisUntilFinished / 1000).toInt()
                    timerTextView.text = secondsRemaining.toString()
                }

                override fun onFinish() {
                    timerTextView.text = "0"
                }
            }.start()
        }

        questionTextView = findViewById(R.id.questionTextView)
        option1Button = findViewById(R.id.option1Button)
        option2Button = findViewById(R.id.option2Button)
        option3Button = findViewById(R.id.option3Button)
        option4Button = findViewById(R.id.option4Button)
        timerTextView = findViewById(R.id.timerTextView)


        startButton.setOnClickListener {
            startGame()
        }

        option1Button.setOnClickListener { onOptionSelected(it) }
        option2Button.setOnClickListener { onOptionSelected(it) }
        option3Button.setOnClickListener { onOptionSelected(it) }
        option4Button.setOnClickListener { onOptionSelected(it) }


    }

    private fun startGame() {
        option1Button.setBackgroundResource(R.drawable.rounded_button_background)
        option2Button.setBackgroundResource(R.drawable.rounded_button_background)
        option3Button.setBackgroundResource(R.drawable.rounded_button_background)
        option4Button.setBackgroundResource(R.drawable.rounded_button_background)

        dashbord.visibility = View.GONE
        gamelayout.visibility = View.VISIBLE
        score = 0
        currentQuestionNumber = 1
        showNextQuestion()
        updateScoreDisplay()

        // Retrieve the score from SharedPreferences
        score = sharedPreferences.getInt("player_score", 0)
        updateScoreDisplay()
        // Reset shared preferences // sharedPreferences.edit().clear().apply()
    }


    @SuppressLint("SetTextI18n")
    private fun showNextQuestion() {
        currentQuestion = generateRandomQuestion()
        updateQuestionUI()
        startTimer()
        questionNumberTextView.text = "Question $currentQuestionNumber/50"
        currentQuestionNumber++

        // Ensure questionNumberTextView is visible
        questionNumberTextView.visibility = View.VISIBLE
    }


    private fun startTimer() {

        timer?.cancel()
        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE") var timeLeft = 10000L
        val duration = 10000L // Total duration of the timer in milliseconds
        val interval = 50L // Update interval in milliseconds

        timer = object : CountDownTimer(duration, interval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                timerTextView.text = "${millisUntilFinished / 1000}"
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                timerTextView.text = "Time's up!"
                endGame()
            }
        }.start()
    }


    private fun generateRandomQuestion(): Question {
        val isCapitalToSimple = random.nextBoolean()
        val simpleLetter = ('a'..'z').random()
        val capitalLetter =
            if (isCapitalToSimple) simpleLetter.uppercaseChar() else simpleLetter.lowercaseChar()
        val questionText =
            if (isCapitalToSimple) "What is the simple letter for '$capitalLetter'?" else "What is the capital letter for '$simpleLetter'?"
        return Question(questionText, capitalLetter, simpleLetter, isCapitalToSimple)
    }

    private fun generateRandomOptions(correctOption: Char, question: Question): List<String> {
        val options = mutableListOf<String>()

        // Generate random options
        val randomOptions = mutableListOf<String>()
        while (randomOptions.size < 3) {
            val randomLetter = if (random.nextBoolean()) ('a'..'z').random() else ('A'..'Z').random()

            // Exclude duplicates and the correct answer (both lowercase and uppercase)
            if (!randomOptions.contains(randomLetter.toString()) &&
                randomLetter != correctOption &&
                randomLetter.lowercaseChar() != correctOption &&
                randomLetter.uppercaseChar() != correctOption) {
                randomOptions.add(randomLetter.toString())
            }
        }

        // Add the corresponding letter based on the question type
        val correctSimpleOption = correctOption.lowercaseChar()
        val correctCapitalOption = correctOption.uppercaseChar()

        if (question.isCapitalQuestion) {
            randomOptions.add(correctSimpleOption.toString()) // Add the corresponding simple letter
        } else {
            randomOptions.add(correctCapitalOption.toString()) // Add the corresponding capital letter
        }

        // Ensure options list has exactly four elements
        while (randomOptions.size < 4) {
            val additionalRandomLetter = if (random.nextBoolean()) ('a'..'z').random() else ('A'..'Z').random()
            if (additionalRandomLetter != correctOption &&
                additionalRandomLetter.lowercaseChar() != correctOption &&
                additionalRandomLetter.uppercaseChar() != correctOption) {
                randomOptions.add(additionalRandomLetter.toString())
            }
        }

        // Ensure correct capital or simple option is present
        if (question.isCapitalQuestion && !randomOptions.contains(correctSimpleOption.toString())) {
            randomOptions.add(correctSimpleOption.toString())
        } else if (!question.isCapitalQuestion && !randomOptions.contains(correctCapitalOption.toString())) {
            randomOptions.add(correctCapitalOption.toString())
        }

        options.addAll(randomOptions.shuffled()) // Shuffle random options and add to options list
        return options.distinct() // Remove duplicates
    }


    private fun updateQuestionUI() {
        val questionText = currentQuestion.questionText
        questionTextView.text = questionText

        // Generate options based on the type of question
        val options = generateRandomOptions(
            if (currentQuestion.isCapitalQuestion) currentQuestion.simpleLetter else currentQuestion.capitalLetter,
            currentQuestion
        )

        // Ensure options list contains at least four elements
        if (options.size >= 4) {
            // Set the text of option buttons
            option1Button.text = options[0]
            option2Button.text = options[1]
            option3Button.text = options[2]
            option4Button.text = options[3]
        } else {
            Log.e("MainActivity", "Options list doesn't have enough elements")
        }
    }

    private fun onOptionSelected(view: View) {
        val selectedOption = when (view.id) {
            R.id.option1Button -> option1Button.text.toString()
            R.id.option2Button -> option2Button.text.toString()
            R.id.option3Button -> option3Button.text.toString()
            R.id.option4Button -> option4Button.text.toString()
            else -> ""
        }

        // Check if the selected option matches the correct answer (case-insensitive)
        val correctAnswer = if (currentQuestion.isCapitalQuestion) {
            currentQuestion.capitalLetter.toString()
        } else {
            currentQuestion.simpleLetter.toString()
        }

        if (selectedOption.equals(correctAnswer, ignoreCase = true)) {
            score += 10
            updateScoreDisplay()
            view.setBackgroundColor(correctColor)
        } else {
            view.setBackgroundColor(incorrectColor)
            Toast.makeText(this, "Wrong! Game Over.", Toast.LENGTH_SHORT).show()
            endGame()
            return
        }

        Handler(Looper.getMainLooper()).postDelayed({
            view.setBackgroundResource(R.drawable.rounded_button_background)
            if (currentQuestionNumber <= 50) {
                showNextQuestion()
            } else {
                Toast.makeText(this, "congratulations! Game Over.", Toast.LENGTH_SHORT).show()
                endGame()
            }
        }, 1000)
    }


    @SuppressLint("SetTextI18n")
    private fun endGame() {
        Toast.makeText(this, "Game Over! Your score: $score", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            dashbord.visibility = View.VISIBLE
            gamelayout.visibility = View.GONE
        }, 1000)

        timer?.cancel()
        currentQuestionNumber = 1
        title.text = "  Game Over!  \n\n Score: $score "
        startButton.text = "  Play Again  "

        // Update high score if necessary
        val currentHighScore = sharedPreferences.getInt("high_score", 0)
        if (score > currentHighScore) {
            sharedPreferences.edit().putInt("high_score", score).apply()
            updateHighScoreDisplay() // Update the display with the new high score
        }

    }

    @SuppressLint("SetTextI18n")
    private fun updateScoreDisplay() {
        scoreTextView.text = "Score: $score"

        // Save the score to SharedPreferences
        sharedPreferences.edit().putInt("player_score", score).apply()
    }

    @SuppressLint("SetTextI18n")
    private fun updateHighScoreDisplay() {
        val highScore = sharedPreferences.getInt("high_score", 0)
        highScoreTextView.text = "High Score: $highScore"
    }

}
