package com.ketan.QuizKrida.controllers;

import com.ketan.QuizKrida.models.ParticipantScore;
import com.ketan.QuizKrida.models.Quiz;
import com.ketan.QuizKrida.services.PlayQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "https://myquizapp-psi.vercel.app")
public class PlayQuizController {
    @Autowired
    public PlayQuizService playQuizService;


    @GetMapping("/Play/{quizId}")
    public ResponseEntity<Quiz> loadQuiz(@PathVariable int quizId){
        Quiz quiz = playQuizService.loadQuiz(quizId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(quiz);
    }


    @PostMapping("/Play/Submit")
    public ResponseEntity<Void> submitQuiz(@RequestBody ParticipantScore participant) {
        playQuizService.saveParticipant(participant);
        return ResponseEntity.ok().build();
    }
}
