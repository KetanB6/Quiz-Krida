package com.ketan.QuizKrida.repository;

import com.ketan.QuizKrida.models.ParticipantScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScoreRepo extends JpaRepository<ParticipantScore, Integer> {

    List<ParticipantScore> findByQuizId(int quizId);
}
