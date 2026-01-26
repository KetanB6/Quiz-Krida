package com.ketan.QuizKrida.models;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class ResultDTO {
    private String name;
    private int score;
    private int outOf;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
