package com.example.alphabaticalquiz

data class Question(
    val questionText: String,
    val capitalLetter: Char,
    val simpleLetter: Char,
    val isCapitalQuestion: Boolean // Add this property
)
