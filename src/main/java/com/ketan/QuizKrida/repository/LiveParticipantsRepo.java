package com.ketan.QuizKrida.repository;

import com.ketan.QuizKrida.models.LiveParticipant;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiveParticipantsRepo extends JpaRepository<LiveParticipant, Integer> {
    @Nullable List<LiveParticipant> findAllByQuizId(int quizId);

}
