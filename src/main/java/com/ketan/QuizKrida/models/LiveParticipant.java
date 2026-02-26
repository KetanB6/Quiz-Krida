package com.ketan.QuizKrida.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Component
public class LiveParticipant {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int srNo;
    @Id
    private String email;
    private int quizId;
    private String name;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime attendTime;

//    public int getSrNo() {
//        return srNo;
//    }
//
//    public void setSrNo(int srNo) {
//        this.srNo = srNo;
//    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getAttendTime() {
        return attendTime;
    }

    public void setAttendTime(LocalDateTime attendTime) {
        this.attendTime = attendTime;
    }
}
