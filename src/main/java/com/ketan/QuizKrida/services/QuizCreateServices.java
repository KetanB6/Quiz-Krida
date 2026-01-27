package com.ketan.QuizKrida.services;

import com.ketan.QuizKrida.exceptionsHandler.BadRequestException;
import com.ketan.QuizKrida.exceptionsHandler.ResourceNotFoundException;
import com.ketan.QuizKrida.models.*;
import com.ketan.QuizKrida.repository.QuestionsRepo;
import com.ketan.QuizKrida.repository.QuizzesRepo;
import com.ketan.QuizKrida.repository.ScoreRepo;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class QuizCreateServices {

//    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private static final Logger log = LoggerFactory.getLogger(QuizCreateServices.class);

    @Autowired
    private QuestionsRepo qRepo;
    @Autowired
    private QuizzesRepo qzRepo;
    @Autowired
    private ScoreRepo scoreRepo;


    //1. Creates quiz
    @Transactional
    public void createQuiz(Quizzes quizInfo) {
        if (quizInfo == null) {
            log.error("quizInfo is null!");
            throw new BadRequestException("Quiz data must not be null");
        }

        qzRepo.save(quizInfo);
        log.info(quizInfo.getQuizTitle() + "quiz created.");
    }

    //a. Generate quiz_id
    public int generateUniqueQuizId() {
        Random random = new Random();
        int id;
        do {
            id = 100000 + random.nextInt(900000); // Generates a random number between 100000 and 999999
        } while (qzRepo.existsById(id)); // Checks if ID already exists in DB to avoid duplicates
        return id;
    }

    //2. Save questions
    @Transactional
    public void saveQuestion(List<Question> questions) {
        int qid = questions.getFirst().getQuizId();
        if(!qzRepo.existsById(qid)) {
            log.error("Quiz not exist with this quiz_id " + qid);
            throw new ResourceNotFoundException("Quiz not exist with this quiz_id " + qid);
        }
        qRepo.saveAll(questions);
        log.info("Saved questions");
    }

    //3. Return quizzes list after Mentor log-in
    public List<Quizzes> createdQuizzes(String email) {
        log.info("Returning quizzes list");
        return qzRepo.findByCreatedBy(email);
    }

    //Preview quiz
    public Quiz loadQuiz(int quizId) {
        if(!qzRepo.existsById(quizId)) {
            log.error("Quiz not exist to load!");
            throw new ResourceNotFoundException("Quiz not exist!");
        }
        log.info("Loading Quiz for Preview");
        return new Quiz(loadQuizInfo(quizId), loadQuestions(quizId));
    }

    //a. Load quiz info
    public Quizzes loadQuizInfo(int quizId) {
        return qzRepo.findById(quizId).orElse(new Quizzes());
    }

    //b. Preview Quiz
    public List<Question> loadQuestions(int quizId) {
        return qRepo.findByQuizId(quizId);
    }

    //edit quiz
    @Transactional
    public void updateQuiz(EditQuizDTO dto){
        if(dto==null||dto.getQuiz()==null){
            throw new ResourceNotFoundException("Quiz object is null");
        }
        int quizId=dto.getQuiz().getQuiz().getQuizId();
        if(!qzRepo.existsById(quizId)){
            throw new ResourceNotFoundException("Quiz not found!");
        }
        if(dto.getQuestionNos() !=null && !dto.getQuestionNos().isEmpty()){
            for(int qno:dto.getQuestionNos()){
                if(qRepo.existsById(qno)){
                    qRepo.deleteById(qno);
                }
            }
        }

        qzRepo.save(dto.getQuiz().getQuiz());

        List<Question> questions=dto.getQuiz().getQuestions();

        if(questions!=null && !questions.isEmpty()){
            List<Integer> deleted = dto.getQuestionNos();
            for(Question q : questions){
                if(deleted !=null && deleted.contains(q.getQno())){
                    continue;
                }
                qRepo.save(q);
            }
        }
    }


    //delete entire quiz
    @Transactional
    public void deleteQuiz(int quizId) {
        if (!qzRepo.existsById(quizId)) {
            log.error("Quiz not exist to delete!");
            throw new ResourceNotFoundException("Quiz not found: " + quizId);
        }
        qzRepo.deleteById(quizId);
        log.info("Quiz deleted successfully!");
    }

    @Transactional
    public void switchQuizStatus(int quizId) {
        int updatedRows = qzRepo.toggleQuizStatus(quizId);

        if(updatedRows == 0) {
            log.error("Failed to toggle: Quiz not exist!");
            throw new ResourceNotFoundException("Quiz not exist!");
        }
        log.info("Successfully toggled status!");
    }

    public @Nullable List<ResultDTO> getResult(int quizId) {
        if(!qzRepo.existsById(quizId)) {
            log.error("Quiz not exist to generate result");
            throw new ResourceNotFoundException("Quiz not exist!");
        }
        List<ResultDTO> result = new ArrayList<>();
        List<ParticipantScore> scores = scoreRepo.findByQuizId(quizId);
        for(ParticipantScore participant: scores) {
            ResultDTO rs = new ResultDTO();
            rs.setName(participant.getParticipantName());
            rs.setScore(participant.getScore());
            rs.setOutOf(participant.getOutOf());
            result.add(rs);
        }
        log.info("Result Generated!");
        return result;
    }
}
