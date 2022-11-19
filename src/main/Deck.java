package main;

import fileio.CardInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck {
    private int nrCardsInDeck;
    private ArrayList<Card> cards = new ArrayList<Card>();

    public int getNrCardsInDeck() {
        return nrCardsInDeck;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setNrCardsInDeck(int nrCardsInDeck) {
        this.nrCardsInDeck = nrCardsInDeck;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    void setupDeck(ArrayList<CardInput> inputDeck, int seed, int nrCardsInDeck) {
        for (int i = 0; i < nrCardsInDeck; i++) {
            this.setNrCardsInDeck(nrCardsInDeck);
            CardInput inputCard = inputDeck.get(i);
            switch (inputCard.getName()) {
                case "Firestorm":
                    cards.add(new Firestorm(inputCard.getMana(), inputCard.getDescription(),
                            inputCard.getColors(), inputCard.getName()));
                    break;
                case "Winterfell":
                    cards.add(new Winterfell(inputCard.getMana(), inputCard.getDescription(),
                            inputCard.getColors(), inputCard.getName()));
                    break;
                case "Heart Hound":
                    cards.add(new HeartHound(inputCard.getMana(), inputCard.getDescription(),
                            inputCard.getColors(), inputCard.getName()));
                    break;
                case "Sentinel":
                    cards.add(new StandardMinion(inputCard.getMana(),
                            inputCard.getDescription(), inputCard.getColors(), inputCard.getName(),
                            inputCard.getAttackDamage(), inputCard.getHealth()));
                    ((Minion)cards.get(cards.size() - 1)).setTank(false);
                    break;
                case "Berserker":
                    cards.add(new StandardMinion(inputCard.getMana(),
                            inputCard.getDescription(), inputCard.getColors(), inputCard.getName(),
                            inputCard.getAttackDamage(), inputCard.getHealth()));
                    ((Minion)cards.get(cards.size() - 1)).setTank(false);
                    break;
                case "Goliath":
                    cards.add(new StandardMinion(inputCard.getMana(),
                            inputCard.getDescription(), inputCard.getColors(), inputCard.getName(),
                            inputCard.getAttackDamage(), inputCard.getHealth()));
                    ((Minion)cards.get(cards.size() - 1)).setTank(true);
                    break;
                case "Warden":
                    cards.add(new StandardMinion(inputCard.getMana(),
                            inputCard.getDescription(), inputCard.getColors(), inputCard.getName(),
                            inputCard.getAttackDamage(), inputCard.getHealth()));
                    ((Minion)cards.get(cards.size() - 1)).setTank(true);
                    break;
                case "The Ripper":
                    cards.add(new TheRipper(inputCard.getMana(),
                            inputCard.getDescription(), inputCard.getColors(), inputCard.getName(),
                            inputCard.getAttackDamage(), inputCard.getHealth()));
                    break;
                case "Miraj":
                    cards.add(new Miraj(inputCard.getMana(),
                            inputCard.getDescription(), inputCard.getColors(), inputCard.getName(),
                            inputCard.getAttackDamage(), inputCard.getHealth()));
                    break;

                case "The Cursed One":
                    cards.add(new TheCursedOne(inputCard.getMana(),
                            inputCard.getDescription(), inputCard.getColors(), inputCard.getName(),
                            inputCard.getAttackDamage(), inputCard.getHealth()));
                    break;
                case "Disciple":
                    cards.add(new Disciple(inputCard.getMana(),
                            inputCard.getDescription(), inputCard.getColors(), inputCard.getName(),
                            inputCard.getAttackDamage(), inputCard.getHealth()));
                    break;
            }
        }
        Collections.shuffle(cards, new Random(seed));
    }
}
