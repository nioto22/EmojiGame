package com.example.nioto.emojigame.database;

public class EnigmaPlayed {

    private String enigmaUid;
    private int enigmaIsSolved; // -1 = NO , 1 = YES
    private int enigmaHasHintOne;
    private int enigmaHasHintTwo;
    private String hintTwoPositions;

    public EnigmaPlayed (String enigmaUid){
        this.enigmaUid = enigmaUid;
        this.enigmaIsSolved = -1;
        this.enigmaHasHintOne = -1;
        this.enigmaHasHintTwo = -1;
        this.hintTwoPositions = "";
    }


    // GETTERS
    public String getEnigmaUid() {  return enigmaUid;    }
    public int getEnigmaIsSolved() {  return enigmaIsSolved;    }
    public int getEnigmaHasHintOne() { return enigmaHasHintOne;    }
    public int getEnigmaHasHintTwo() { return enigmaHasHintTwo;    }
    public String getHintTwoPositions() { return hintTwoPositions; }


    // SETTERS
    public void setEnigmaUid(String enigmaUid) { this.enigmaUid = enigmaUid; }
    public void setEnigmaIsSolved(int enigmaIsSolved) {   this.enigmaIsSolved = enigmaIsSolved; }
    public void setEnigmaHasHintOne(int enigmaHasHintOne) {  this.enigmaHasHintOne = enigmaHasHintOne;    }
    public void setEnigmaHasHintTwo(int enigmaHasHintTwo) {  this.enigmaHasHintTwo = enigmaHasHintTwo; }
    public void setHintTwoPositions(String hintTwoPositions) {  this.hintTwoPositions = hintTwoPositions; }
}
