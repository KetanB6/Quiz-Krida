package com.ketan.QuizKrida.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Entity
public class ParticipantScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int srNo;
    private int quizId;
    private String participantName;
    private int score;
    private int outOf;
    @JsonIgnore
    private Instant submitTime;

    public int getSrNo() {
        return srNo;
    }

    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getOutOf() {
        return outOf;
    }

    public void setOutOf(int outOf) {
        this.outOf = outOf;
    }

    public Instant getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Instant submitTime) {
        this.submitTime = submitTime;
    }
}
