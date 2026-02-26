package com.ketan.QuizKrida.services;

import com.ketan.QuizKrida.exceptionsHandler.BadRequestException;
import com.ketan.QuizKrida.exceptionsHandler.ResourceNotFoundException;
import com.ketan.QuizKrida.models.*;
import com.ketan.QuizKrida.repository.LiveParticipantsRepo;
import com.ketan.QuizKrida.repository.QuestionsRepo;
import com.ketan.QuizKrida.repository.QuizzesRepo;
import com.ketan.QuizKrida.repository.ScoreRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class PlayQuizService {

    private static final Logger log = LoggerFactory.getLogger(PlayQuizService.class);

    private final QuestionsRepo qRepo;
    private final QuizzesRepo qzRepo;
    private final ScoreRepo scoreRepo;
    private final LiveParticipantsRepo liveParticipants;

    @Autowired
    public PlayQuizService (QuestionsRepo qRepo, QuizzesRepo qzRepo, ScoreRepo scoreRepo, LiveParticipantsRepo liveParticipants) {
        this.qRepo = qRepo;
        this.qzRepo = qzRepo;
        this.scoreRepo = scoreRepo;
        this.liveParticipants = liveParticipants;
    }

    //1. Loads the quiz info and questions and wrap into Quiz object
    public Quiz loadQuiz(int quizId, String name, String email) {
        if(!qzRepo.existsById(quizId)) {
            log.error("Quiz not exist to load");
            throw new ResourceNotFoundException("Quiz not exist!");
        }

        Quizzes qz = qzRepo.findById(quizId).get();

        if(!qz.isStatus()) {
            log.error("Quiz not yet started to play!");
            throw new BadRequestException("Quiz not started yet!");
        }

        //check if user already attended a quiz
        if(scoreRepo.existsByQuizIdAndEmail(quizId, email)) {
            log.error("Quiz is already submitted by the user: {}", email);
            throw new BadRequestException("It seems you already attempted a quiz!");
        }

        //check if current time is earlier than expiryTime
        if(qz.getExpiryTime() != null) {
            if (qz.getExpiryTime().isBefore(Instant.now()) && qz.isPrivate()) {
                qzRepo.toggleQuizStatus(quizId);
                qzRepo.setExpiryTime(null, quizId);
                log.error("Quiz expired or deactivated!");
                throw new BadRequestException("Quiz Expired!");
            }
        }

        //save player to show mentor live participants
        if(qz.isPrivate()) {
            LiveParticipant liveParticipant = new LiveParticipant();
            liveParticipant.setQuizId(quizId);
            liveParticipant.setName(name);
            liveParticipant.setEmail(email);
            liveParticipant.setAttendTime(LocalDateTime.now().withNano(0));
            liveParticipants.save(liveParticipant);
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
        List<Question> questions = qRepo.findByQuizId(quizId);
        for(Question q: questions) {
            String correctOption = q.getCorrectOpt();
            String encryptedOption = encrypt(correctOption);
            q.setCorrectOpt(encryptedOption);
        }
        return questions;
    }

    public String encrypt(String option) {
        String secret = "jon-snow-is-here";
        try{
            SecretKeySpec spec = new SecretKeySpec(secret.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(cipher.ENCRYPT_MODE, spec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(option.getBytes()));
        } catch (Exception e) {
            throw new BadRequestException("Failed to encrypt correct option");
        }
    }

    public void saveParticipant(ParticipantScore participant) {
        if(participant == null) {
            log.error("Participant object is empty!");
            throw new BadRequestException("Participant is empty!");
        }
        Quizzes qz = qzRepo.findById(participant.getQuizId()).get();

        if(!qz.isPrivate()) return; //if quiz is public, no need to store score

        if(!qz.isStatus()) {
            log.info("Quiz already expired!");
            throw new BadRequestException("Quiz already expired!");
        }
        participant.setSubmitTime(Instant.now());
        try {
            scoreRepo.save(participant);
            log.info("Participant and score saved!");
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate submission attempt blocked for quizId={} email={}",participant.getQuizId(), participant.getEmail());
            throw new BadRequestException("Record already exist!");
        }
    }

}

/*

public String decrypt(String encryptedData, String secretKey) throws Exception {
    // 1. Prepare the key (Must be 16, 24, or 32 chars)
    SecretKeySpec spec = new SecretKeySpec(secretKey.getBytes(), "AES");

    // 2. Initialize the Cipher in DECRYPT_MODE
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, spec);

    // 3. Decode the Base64 string back to bytes
    byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);

    // 4. Decrypt and return as a string
    byte[] originalValue = cipher.doFinal(decodedBytes);
    return new String(originalValue);
}
*/
