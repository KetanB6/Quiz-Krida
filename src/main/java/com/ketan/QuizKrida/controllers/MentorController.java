package com.ketan.QuizKrida.controllers;

import com.ketan.QuizKrida.models.*;
import com.ketan.QuizKrida.services.PlayQuizService;
import com.ketan.QuizKrida.services.QuizCreateServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin("*")
public class MentorController {

    @Autowired
    public QuizCreateServices service;

    @Autowired
    public PlayQuizService playQuizService;

    @GetMapping("/Health")
    public ResponseEntity<String> health(){
        return ResponseEntity.ok("Active");
    }

    @PostMapping("/Create")
    public ResponseEntity<Integer> createQuiz(@RequestBody Quizzes quiz){
        int newId = service.generateUniqueQuizId();
        quiz.setQuizId(newId);
        service.createQuiz(quiz);
        return ResponseEntity.ok(newId); //return this quiz id with questions
    }

    @PostMapping("/Questions")
    public ResponseEntity<String> addQuestions(@RequestBody List<Question> questions){
            service.saveQuestion(questions);
            return ResponseEntity.ok().build();
    }

    //send list of quizzes(all the quizzes that he created earlier) when user login.
    //http://localhost:8080/Quiz/Logged?email=ketan@gmail.com
    @GetMapping("/Logged")
    public ResponseEntity<List<Quizzes>> createdQuizzes(@RequestParam String email) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.createdQuizzes(email));
    }

    //When user opts for Preview Quiz/ Edit Quiz
    @GetMapping("/Logged/Preview/{quizId}")
    public ResponseEntity<Quiz> previewQuiz(@PathVariable int quizId) {
        Quiz quiz = service.loadQuiz(quizId);
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(quiz);
    }

    @GetMapping("/Logged/SwitchStatus/{quizId}")
    public ResponseEntity<Void> activeQuiz(@PathVariable int quizId) {
        service.switchQuizStatus(quizId);
        return ResponseEntity.ok().build();
    }

    //When user submit edited Quiz
    @PutMapping("/Logged/Edit")
    public ResponseEntity<Void> editQuiz(@RequestBody EditQuizDTO quiz) {
        service.updateQuiz(quiz);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //Delete Quiz
    @DeleteMapping("/Logged/Delete/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable int quizId) {
        service.deleteQuiz(quizId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //Return result list
    @GetMapping("Logged/Result/{quizId}")
    public ResponseEntity<List<ResultDTO>> result(@PathVariable int quizId) {
        return ResponseEntity.ok(service.getResult(quizId));
    }

}

