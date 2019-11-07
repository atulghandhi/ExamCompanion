package com.ec16358.examcompanion.flashcards;

/*
*
* Deck object is used to contain information about flash card decks for each module.
*
* Instances of this class are created when new decks are added via alert dialog in FlashCardDecks.java
* and added to firebase database. These instances are then deserialised and read (also by FlashCardDecks.java)
* to populate a ListView of decks.
*
* */

public class DeckObject {

    private String id;
    private String name;
    private String module;
    private int cards;
    private int cardsDue;


    public DeckObject() {
    }


    public DeckObject(String id, String name, String module, int cards, int cardsDue) {
        this.id = id;
        this.name = name;
        this.module = module;
        this.cards = cards;
        this.cardsDue = cardsDue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public int getCards() {
        return cards;
    }

    public void setCards(int cards) {
        this.cards = cards;
    }

    public int getCardsDue() {
        return cardsDue;
    }

    public void setCardsDue(int cardsDue) {
        this.cardsDue = cardsDue;
    }

}
