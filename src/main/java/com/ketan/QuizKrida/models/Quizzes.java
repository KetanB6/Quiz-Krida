package com.ketan.QuizKrida.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.UniqueConstraint;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.security.Timestamp;
import java.time.Instant;

@Component
@Entity
@Scope("prototype")
public class Quizzes {
    @Id
    private int quizId;
    private int duration;
    private String createdBy;//email
    private String quizTitle;
    private boolean status; //true or false

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    //
//    public Instant getQuizTime() {
//        return quizTime;
//    }
//
//    public void setQuizTime(Instant quizTime) {
//        this.quizTime = quizTime;
//    }

}
