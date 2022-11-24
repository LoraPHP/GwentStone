package main;

import fileio.*;

import java.util.ArrayList;

public class GameInfo {
    private int gameNumber = 0;

    private int turn;
    private Hero playerOneHero, playerTwoHero;
    private Deck playerOneDeck, playerTwoDeck;
    private int playerOneTotalMana = 1, playerTwoTotalMana = 1;
    private int startPlayer;

    private int round = 1;

    private ArrayList<ArrayList<Card>> board;

    private ArrayList<Card> playerOneHand, playerTwoHand;

    public final int getGameNumber() {
        return gameNumber;
    }

    public final int getTurn() {
        return turn;
    }

    public final Hero getPlayerOneHero() {
        return playerOneHero;
    }

    public final Hero getPlayerTwoHero() {
        return playerTwoHero;
    }

    public final Deck getPlayerOneDeck() {
        return playerOneDeck;
    }

    public final Deck getPlayerTwoDeck() {
        return playerTwoDeck;
    }

    public final ArrayList<ArrayList<Card>> getBoard() {
        return board;
    }

    public final ArrayList<Card> getPlayerOneHand() {
        return playerOneHand;
    }

    public final ArrayList<Card> getPlayerTwoHand() {
        return playerTwoHand;
    }

    public final void setGameNumber(final int gameNumber) {
        this.gameNumber = gameNumber;
    }

    public final void setTurn(final int turn) {
        this.turn = turn;
    }

    public final void setPlayerOneHero(final Hero playerOneHero) {
        this.playerOneHero = playerOneHero;
    }

    public final void setPlayerTwoHero(final Hero playerTwoHero) {
        this.playerTwoHero = playerTwoHero;
    }

    public final void setPlayerOneDeck(final Deck playerOneDeck) {
        this.playerOneDeck = playerOneDeck;
    }

    public final void setPlayerTwoDeck(final Deck playerTwoDeck) {
        this.playerTwoDeck = playerTwoDeck;
    }

    public final void setBoard(final ArrayList<ArrayList<Card>> board) {
        this.board = board;
    }

    public final void setPlayerOneHand(final ArrayList<Card> playerOneHand) {
        this.playerOneHand = playerOneHand;
    }

    public final void setPlayerTwoHand(final ArrayList<Card> playerTwoHand) {
        this.playerTwoHand = playerTwoHand;
    }
    public final int getPlayerOneTotalMana() {
        return playerOneTotalMana;
    }

    public final void setPlayerOneTotalMana(final int playerOneTotalMana) {
        this.playerOneTotalMana = playerOneTotalMana;
    }

    public final int getPlayerTwoTotalMana() {
        return playerTwoTotalMana;
    }

    public final void setPlayerTwoTotalMana(final int playerTwoTotalMana) {
        this.playerTwoTotalMana = playerTwoTotalMana;
    }

    public final int getRound() {
        return round;
    }

    public final void setRound(final int round) {
        this.round = round;
    }

    public final int getStartPlayer() {
        return startPlayer;
    }

    public final void setStartPlayer(final int startPlayer) {
        this.startPlayer = startPlayer;
    }

    /**
     * This method creates a Hero with the following characteristics:
     *
     * @param mana the hero's ability mana cost
     * @param description a short description of the hero
     * @param colors the colors used in the drawing of the hero on the card
     * @param name the hero's name
     * @return a Hero containing all the necessary information
     */
    Hero setHero(final int mana, final String description, final ArrayList<String> colors,
                 final String name) {
        Hero hero = null;
        switch (name) {
            case "Lord Royce":
                hero = new LordRoyce(mana, description, colors, name);
                break;
            case "Empress Thorina":
                hero = new EmpressThorina(mana, description, colors, name);
                break;
            case "King Mudface":
                hero = new KingMudface(mana, description, colors, name);
                break;
            case "General Kocioraw":
                hero = new GeneralKocioraw(mana, description, colors, name);
                break;
            default:
                System.out.println("Warning: Invalid case reached.");
        }
        return hero;
    }
    /**
     * This method is responsible for the setup of the player's heroes, decks, cards in hand and
     * the setup of the playing board.
     *
     * @param inputData holds all the information about the heroes and decks
     * @param gameNumber indicates the number of the current game
     */
    void setupGame(final Input inputData, final int gameNumber) {
        StartGameInput startInput = inputData.getGames().get(gameNumber).getStartGame();
        // set up the player's heroes
        playerOneHero = setHero(
                startInput.getPlayerOneHero().getMana(),
                startInput.getPlayerOneHero().getDescription(),
                startInput.getPlayerOneHero().getColors(),
                startInput.getPlayerOneHero().getName());
        playerTwoHero = setHero(
                startInput.getPlayerTwoHero().getMana(),
                startInput.getPlayerTwoHero().getDescription(),
                startInput.getPlayerTwoHero().getColors(),
                startInput.getPlayerTwoHero().getName());
        // sets the starting player
        setTurn(inputData.getGames().get(gameNumber).getStartGame().getStartingPlayer());
        setStartPlayer(getTurn());

        int playerOneDeckIdx = startInput.getPlayerOneDeckIdx();
        int seed = startInput.getShuffleSeed();
        ArrayList<CardInput> playerOneDeckInput = inputData.getPlayerOneDecks().getDecks()
                .get(playerOneDeckIdx);

        // sets up the decks and each player draws the first card
        playerOneDeck = new Deck();
        int nrCardsInDeck = inputData.getPlayerOneDecks().getNrCardsInDeck();
        playerOneDeck.setupDeck(playerOneDeckInput, seed, nrCardsInDeck);

        playerOneHand = new ArrayList<Card>();
        playerOneHand.add(playerOneDeck.getCards().get(0));
        playerOneDeck.getCards().remove(0);
        playerOneDeck.setNrCardsInDeck(playerOneDeck.getNrCardsInDeck() - 1);

        int playerTwoDeckIdx = startInput.getPlayerTwoDeckIdx();
        ArrayList<CardInput> playerTwoDeckInput = inputData.getPlayerTwoDecks().getDecks()
                .get(playerTwoDeckIdx);

        playerTwoDeck = new Deck();
        playerTwoDeck.setupDeck(playerTwoDeckInput, seed, nrCardsInDeck);

        playerTwoHand = new ArrayList<Card>();
        playerTwoHand.add(playerTwoDeck.getCards().get(0));
        playerTwoDeck.getCards().remove(0);
        playerTwoDeck.setNrCardsInDeck(playerTwoDeck.getNrCardsInDeck() - 1);

        // forms the playing field
        board = new ArrayList<ArrayList<Card>>(4);
        board.add(new ArrayList<Card>());
        board.add(new ArrayList<Card>());
        board.add(new ArrayList<Card>());
        board.add(new ArrayList<Card>());
    }
}
