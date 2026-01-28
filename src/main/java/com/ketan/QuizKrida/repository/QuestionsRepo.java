package com.ketan.QuizKrida.repository;

import com.ketan.QuizKrida.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionsRepo extends JpaRepository<Question, Integer> {
    List<Question> findByQuizId(int quizId);

    void deleteByQuizId(int quizId);

    int countByQuizId(int quizId);
}
