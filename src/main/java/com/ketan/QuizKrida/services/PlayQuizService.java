package com.ketan.QuizKrida.services;

import com.ketan.QuizKrida.exceptionsHandler.BadRequestException;
import com.ketan.QuizKrida.exceptionsHandler.ResourceNotFoundException;
import com.ketan.QuizKrida.models.ParticipantScore;
import com.ketan.QuizKrida.models.Question;
import com.ketan.QuizKrida.models.Quiz;
import com.ketan.QuizKrida.models.Quizzes;
import com.ketan.QuizKrida.repository.QuestionsRepo;
import com.ketan.QuizKrida.repository.QuizzesRepo;
import com.ketan.QuizKrida.repository.ScoreRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayQuizService {

    private static final Logger log = LoggerFactory.getLogger(PlayQuizService.class);

    @Autowired
    private QuestionsRepo qRepo;
    @Autowired
    private QuizzesRepo qzRepo;
    @Autowired
    private ScoreRepo scoreRepo;

    //1. Loads the quiz info and questions and wrap into Quiz object
    public Quiz loadQuiz(int quizId) {
        if(!qzRepo.existsById(quizId)) {
            log.error("Quiz not exist to load");
            throw new ResourceNotFoundException("Quiz not exist!");
        }

        if(!qzRepo.findById(quizId).get().isStatus()) {
            log.error("Quiz not yet started to play!");
            throw new BadRequestException("Quiz not started yet!");
        }
        log.info("Starting Quiz...");
        return new Quiz(loadQuizInfo(quizId), loadQuestions(quizId));
    }

    //a. Load quiz info
    public Quizzes loadQuizInfo(int quizId) {
        return qzRepo.findById(quizId).orElse(new Quizzes());
    }

    //b. Load quiz questions
    public List<Question> loadQuestions(int quizId) {
        return qRepo.findByQuizId(quizId);
    }


    public void saveParticipant(ParticipantScore participant) {
        if(participant == null) {
            log.error("Participant object is empty!");
            throw new BadRequestException("Participant is empty!");
        }
        scoreRepo.save(participant);
        log.info("Participant and score saved!");
    }
}
