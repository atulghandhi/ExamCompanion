package com.ec16358.examcompanion;

public class FlashCardObject {

    private String id;
    private String prompt;
    private String answer;

    private String nextReviewDate;

    private int currentLietnerDeck;

    private String module;
    private String deck;

    //constructor that initialises all variables
    public FlashCardObject(String id, String module, String deck, String prompt, String answer, String nextReviewDate, int currentLietnerDeck) {
        this.id = id;
        this.module = module;
        this.deck = deck;
        this.prompt = prompt;
        this.answer = answer;
        this.nextReviewDate = nextReviewDate;
        this.currentLietnerDeck = currentLietnerDeck;
    }

    //default constructor
    public FlashCardObject() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getNextReviewDate() {
        return nextReviewDate;
    }

    public void setNextReviewDate(String nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getDeck() {
        return deck;
    }

    public void setDeck(String deck) {
        this.deck = deck;
    }

    public int getCurrentLietnerDeck() {
        return currentLietnerDeck;
    }

    public void setCurrentLietnerDeck(int currentLietnerDeck) {
        this.currentLietnerDeck = currentLietnerDeck;
    }

}
