package com.ketan.QuizKrida.models;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EditQuizDTO {
    private Quiz quiz;
    private List<Integer> questionNos;

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public List<Integer> getQuestionNos() {
        return questionNos;
    }

    public void setQuestionNos(List<Integer> questionNos) {
        this.questionNos = questionNos;
    }
}
