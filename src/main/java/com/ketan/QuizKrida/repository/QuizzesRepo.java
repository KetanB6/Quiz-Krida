package com.ketan.QuizKrida.repository;

import com.ketan.QuizKrida.models.Quizzes;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Repository
public interface QuizzesRepo extends JpaRepository<Quizzes, Integer> {
    List<Quizzes> findByCreatedBy(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Quizzes q SET q.status = CASE WHEN q.status = true THEN false ELSE true END WHERE q.id = :quizId")
    int toggleQuizStatus(@Param("quizId") int quizId);

    @Modifying
    @Transactional
    @Query("Update Quizzes q SET q.expiryTime = :expiryTime WHERE q.id = :quizId")
    int setExpiryTime(@Param("expiryTime") Instant expiryTime, @Param("quizId") int quizId);

    @Modifying
    @Transactional
    @Query("Update Quizzes q Set q.duration = :size where q.quizId = :quizId")
    int setDuration(@Param("quizId") int qid, @Param("size") int size);

    @Nullable List<Quizzes> findAllByIsPrivateFalse();
}
