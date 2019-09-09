package org.codecyprus.th.model;

import java.util.Random;

public enum QuestionType {

    BOOLEAN, // true, false
    MCQ, // A, B, C, D
    INTEGER, // any int
    NUMERIC, // any float
    TEXT; // any text

    private static Random rand = new Random();

    public static QuestionType random() {
        return values()[rand.nextInt(values().length)];
    }
}