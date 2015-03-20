/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author EliFriedman
 */
public class Game {

    private final int INIT_SIZE = 12;

    // Each card is a 4 digit String where each digit is from 0 - 2 
    // and corresponds to a feature of the Set card
    // "Shape Color Number Fill"
    private HashMap<Integer, String> deck;

    // Each player is represented by the cards they won.
    private HashMap<Integer, ArrayList<String>> players;
    private HashMap<String, Object> outCards;

    public Game(int player_id) {
        this();
        players = new HashMap<>();
        players.put(player_id, new ArrayList<String>());
    }

    public Game() {
        deck = new HashMap<>(81);
        outCards = new HashMap<>(21);

        for (int i = 0; i < 81; i++) {
            String res = getCardString(i);
            deck.put(i, res);
        }
        initializeOutCards();
    }

    /**
     * Initializes the cards that are out, making sure that there is at least
     * one set on the board
     */
    private void initializeOutCards() {
        for (int i = 0; i < INIT_SIZE / 3.0; i++) {
            add3Cards();
        }
        if (!outContainsSet()) {
        }
    }

    private boolean outContainsSet() {
        String[] cards = new String[outCards.keySet().size()];
        outCards.keySet().toArray(cards);
        for (int i = 0; i < outCards.size(); i++) {
            for (int j = 0; j < outCards.size(); j++) {
                if (i != j) {
                    String setCard = getSetCard(cards[i], cards[j]);
                    if (outCards.containsKey(setCard)) {
//                        System.out.println("Set: " + cards[i] + " " + cards[j] + " " + setCard);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * @param playerID the ID of a player
     * @param card1
     * @param card2
     * @param card3
     * @return
     */
    public int checkSet(int playerID, String card1, String card2, String card3) {
        if (players.containsKey(playerID)) {
            ArrayList<String> ownedcards = players.get(playerID);
            if (checkValidSet(card1, card2, card3)) {
                ownedcards.add(card1);
                ownedcards.add(card2);
                ownedcards.add(card3);
                players.put(playerID, ownedcards);
                
                outCards.remove(card1);
                outCards.remove(card2);
                outCards.remove(card3);
                add3Cards();
                return ownedcards.size() + 1;
            }
            return ownedcards.size();
        }
        return -9999;
    }

     public String[] getOutCards() {
         String[] cards = new String[outCards.keySet().size()];
         outCards.keySet().toArray(cards);
         return cards;
     }

    /**
     *
     * @param card1
     * @param card2
     * @param card3
     * @return given three cards, check to see if they're on the table and they
     * form a set
     */
    public boolean checkValidSet(String card1, String card2, String card3) {
        return outCards.containsKey(card1)
                && outCards.containsKey(card2)
                && outCards.containsKey(card3)
                && card3.equals(getSetCard(card1, card2));
    }

    private void add3Cards() {
        if (outCards.size() >= INIT_SIZE) {
            return;
        }
        Random rnd = new Random();

        for (int i = 0; i < 3; i++) {
            int index = rnd.nextInt(deck.size());
            while (!deck.containsKey(index % 81)) {
                index++;
            }
            String card = deck.remove(index);
            outCards.put(card, 0);
        }
    }

    /**
     * Given any two cards returns the unique third card that forms a set with
     * the first two.
     *
     * @param card1
     * @param card2
     * @return
     */
    private String getSetCard(String card1, String card2) {
        String card3 = "";
        for (int i = 0; i < 4; i++) {
            if (card1.charAt(i) == card2.charAt(i)) {
                card3 += card1.charAt(i);
            } else {
                // gets the third value and converts to char
                card3 += (char) '0' + '3' - (card1.charAt(i) + card2.charAt(i));
            }
        }
        return card3;
    }

    /**
     * Returns the string form of the card given a number from 0 to 80
     *
     * @param index
     * @return
     */
    private String getCardString(int index) {
        String res = "";
        for (int threeNum = index; threeNum > 0; threeNum /= 3) {
            res = (threeNum % 3) + res;
        }
        if (res.length() < 4) {
            res = "0000" + res;
            res = res.substring(res.length() - 4);
        }
        return res;
    }

    private int getCardIndex(String card) {
        return Integer.valueOf(card, 3);
    }

    public Integer[] getPlayers() {
        Integer[] p = new Integer[players.keySet().size()];
        players.keySet().toArray(p);
        return p;
    }
}