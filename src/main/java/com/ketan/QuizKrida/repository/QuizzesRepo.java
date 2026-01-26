package com.ketan.QuizKrida.repository;

import com.ketan.QuizKrida.models.Quizzes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizzesRepo extends JpaRepository<Quizzes, Integer> {
    List<Quizzes> findByCreatedBy(String email);
}
