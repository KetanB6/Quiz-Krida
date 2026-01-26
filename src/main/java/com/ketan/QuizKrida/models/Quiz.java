package com.ketan.QuizKrida.models;

import org.springframework.context.annotation.Scope;

import java.util.List;

@Scope("prototype")
public class Quiz {
    private Quizzes quiz;
    private List<Question> questions;

    public Quiz(Quizzes quiz, List<Question> questions) {
        this.quiz = quiz;
        this.questions = questions;
    }

    public Quizzes getQuiz() {
        return quiz;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
