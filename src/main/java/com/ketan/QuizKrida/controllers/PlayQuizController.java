package com.ketan.QuizKrida.controllers;

import com.ketan.QuizKrida.models.ParticipantScore;
import com.ketan.QuizKrida.models.Quiz;
import com.ketan.QuizKrida.services.PlayQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PlayQuizController {

    private final PlayQuizService playQuizService;

    @Autowired
    public PlayQuizController(PlayQuizService playQuizService) {
        this.playQuizService = playQuizService;
    }

    @GetMapping
    public String apiInfo() {
        return "You are on API URL. Please visit official site: https://myquizapp-psi.vercel.app/";
    }

    @GetMapping("/Play/{quizId}/{name}")
    public ResponseEntity<Quiz> loadQuiz(@PathVariable int quizId, @PathVariable String name){
        Quiz quiz = playQuizService.loadQuiz(quizId, name);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(quiz);
    }


    @PostMapping("/Play/Submit")
    public ResponseEntity<Void> submitQuiz(@RequestBody ParticipantScore participant) {
        playQuizService.saveParticipant(participant);
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/decrypt/{code}")
//    public String decrypt(@PathVariable String code) throws Exception{
//        return playQuizService.decrypt(code, "jon-snow-is-here");
//    }
}
