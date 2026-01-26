package com.ketan.QuizKrida.repository;

import com.ketan.QuizKrida.models.Quizzes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface QuizzesRepo extends JpaRepository<Quizzes, Integer> {
    List<Quizzes> findByCreatedBy(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Quizzes q SET q.status = CASE WHEN q.status = true THEN false ELSE true END WHERE q.id = :quizId")
    int toggleQuizStatus(@Param("quizId") int quizId);
}
