package com.ketan.QuizKrida.services;

import com.ketan.QuizKrida.exceptionsHandler.BadRequestException;
import com.ketan.QuizKrida.exceptionsHandler.ResourceNotFoundException;
import com.ketan.QuizKrida.models.*;
import com.ketan.QuizKrida.repository.LiveParticipantsRepo;
import com.ketan.QuizKrida.repository.QuestionsRepo;
import com.ketan.QuizKrida.repository.QuizzesRepo;
import com.ketan.QuizKrida.repository.ScoreRepo;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class QuizCreateServices {

//    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private static final Logger log = LoggerFactory.getLogger(QuizCreateServices.class);

    private static int extraMinutes = 2;

    private final QuestionsRepo qRepo;
    private final QuizzesRepo qzRepo;
    private final ScoreRepo scoreRepo;
    private final LiveParticipantsRepo liveParticipants;

    @Autowired
    public QuizCreateServices (QuestionsRepo qRepo, QuizzesRepo qzRepo, ScoreRepo scoreRepo, LiveParticipantsRepo liveParticipants) {
        this.qRepo = qRepo;
        this.qzRepo = qzRepo;
        this.scoreRepo = scoreRepo;
        this.liveParticipants = liveParticipants;
    }


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
            log.error("Quiz not exist!");
            throw new ResourceNotFoundException("Quiz not exist! ");
        }
        qRepo.saveAll(questions);

        Quizzes qz = qzRepo.findById(qid).get();
        if(qz.isTimer()) {
            qz.setDuration(questions.size() + extraMinutes);
        } else {
            qz.setDuration(1440);
        }

        log.info("Saved questions");
    }

    //3. Return quizzes list after Mentor log-in
    @Transactional
    public List<Quizzes> createdQuizzes(String email) {
        List<Quizzes> quizzes= qzRepo.findByCreatedBy(email);
        for(Quizzes q: quizzes) {
            if(q.isStatus() && q.getExpiryTime() != null) {
                if(!q.isPrivate()) {
                    q.setExpiryTime(null);
                    continue;
                }
                if (q.getExpiryTime().isBefore(Instant.now())) {
                    q.setStatus(false);
                    q.setExpiryTime(null);
                    liveParticipants.deleteAll();
                    log.info("Status of expired quizzes updated!");
                }
            }
        }

        log.info("Returning quizzes list");
        return quizzes;
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
        if(qzRepo.findById(quizId).get().isStatus()) {
            throw new BadRequestException("Active Quizzes cannot be edited!");
        }
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

        Quizzes qz = dto.getQuiz().getQuiz();

        if(qz.isTimer()) {
            qz.setDuration(qRepo.countByQuizId(quizId)+2);
        } else {
            qz.setDuration(1440);
        }

        qzRepo.save(dto.getQuiz().getQuiz());

        List<Question> questions = dto.getQuiz().getQuestions();

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
    public int switchQuizStatus(int quizId) {
        int updatedRows1 = qzRepo.toggleQuizStatus(quizId);

        if(updatedRows1 == 0) {
            log.error("Failed to toggle");
            throw new ResourceNotFoundException("Quiz not exist to toggle!");
        }

        Quizzes quiz = qzRepo.findById(quizId).get();
        if(!quiz.isPrivate()) {
            log.info("Quiz is public!");
            return -1; //if front-end receive -1 the display message : quiz will not expire
        }

        if(!quiz.isStatus()) {
            log.info("Quiz is switched off!");
            return 0;
        }

        int updatedRows2 = 0;
        int exMin = 1440; //change to 1440 if timer is off

        if(quiz.isStatus() && quiz.isTimer()) {
            exMin = qRepo.countByQuizId(quizId) + extraMinutes;
            updatedRows2 = qzRepo.setExpiryTime(Instant.now().plus(exMin, ChronoUnit.MINUTES), quizId);
            log.info("Quiz will expire after {} minutes.", exMin);
        } else {
            updatedRows2 = qzRepo.setExpiryTime(null, quizId);
            log.info("Quiz will expire after 1440 minutes.");
            liveParticipants.deleteAll();//status is false
        }

        if(updatedRows2 == 0) {
            log.error("Failed to set expiry time!");
            throw new ResourceNotFoundException("Failed to set expiry time!");
        }

        log.info("Successfully toggled status and updated expiry time!");
        return exMin;

    }

    public @Nullable List<ResultDTO> getResult(int quizId) {
        if(!qzRepo.existsById(quizId)) {
            log.error("Quiz not exist to generate result");
            throw new ResourceNotFoundException("Quiz not exist!");
        }
        List<ResultDTO> result = new ArrayList<>();
        List<ParticipantScore> scores = scoreRepo.findByQuizIdOrderByScoreDescSubmitTimeAsc(quizId);
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

    public @Nullable List<LiveParticipant> getLiveParticipants(int quizId) {
        return liveParticipants.findAllByQuizId(quizId);
    }

    public @Nullable List<Quizzes> getPublicQuizzes() {
        return qzRepo.findAllByIsPrivateFalseAndStatusTrue();
    }
}
