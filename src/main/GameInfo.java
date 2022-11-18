package main;

import fileio.*;

import java.util.ArrayList;

public class GameInfo {
    private int gameNumber = 0;

    private int turn;
    private Hero playerOneHero, playerTwoHero;
    private Deck playerOneDeck, playerTwoDeck;
    private int playerOneTotalMana = 1, PlayerTwoTotalMana = 1;
    private int startPlayer;

    private int round = 1;

    private ArrayList<ArrayList<Card>> board;

    public ArrayList<Card> playerOneHand, playerTwoHand;

    public int getGameNumber() {
        return gameNumber;
    }

    public int getTurn() {
        return turn;
    }

    public Hero getPlayerOneHero() {
        return playerOneHero;
    }

    public Hero getPlayerTwoHero() {
        return playerTwoHero;
    }

    public Deck getPlayerOneDeck() {
        return playerOneDeck;
    }

    public Deck getPlayerTwoDeck() {
        return playerTwoDeck;
    }

    public ArrayList<ArrayList<Card>> getBoard() {
        return board;
    }

    public ArrayList<Card> getPlayerOneHand() {
        return playerOneHand;
    }

    public ArrayList<Card> getPlayerTwoHand() {
        return playerTwoHand;
    }

    public void setGameNumber(int gameNumber) {
        this.gameNumber = gameNumber;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setPlayerOneHero(Hero playerOneHero) {
        this.playerOneHero = playerOneHero;
    }

    public void setPlayerTwoHero(Hero playerTwoHero) {
        this.playerTwoHero = playerTwoHero;
    }

    public void setPlayerOneDeck(Deck playerOneDeck) {
        this.playerOneDeck = playerOneDeck;
    }

    public void setPlayerTwoDeck(Deck playerTwoDeck) {
        this.playerTwoDeck = playerTwoDeck;
    }

    public void setBoard(ArrayList<ArrayList<Card>> board) {
        this.board = board;
    }

    public void setPlayerOneHand(ArrayList<Card> playerOneHand) {
        this.playerOneHand = playerOneHand;
    }

    public void setPlayerTwoHand(ArrayList<Card> playerTwoHand) {
        this.playerTwoHand = playerTwoHand;
    }
    public int getPlayerOneTotalMana() {
        return playerOneTotalMana;
    }

    public void setPlayerOneTotalMana(int playerOneTotalMana) {
        this.playerOneTotalMana = playerOneTotalMana;
    }

    public int getPlayerTwoTotalMana() {
        return PlayerTwoTotalMana;
    }

    public void setPlayerTwoTotalMana(int playerTwoTotalMana) {
        PlayerTwoTotalMana = playerTwoTotalMana;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getStartPlayer() {
        return startPlayer;
    }

    public void setStartPlayer(int startPlayer) {
        this.startPlayer = startPlayer;
    }

    void setupGame(Input inputData) {
        StartGameInput startInput = inputData.getGames().get(gameNumber).getStartGame();
        playerOneHero = new Hero(
                startInput.getPlayerOneHero().getMana(),
                startInput.getPlayerOneHero().getDescription(),
                startInput.getPlayerOneHero().getColors(),
                startInput.getPlayerOneHero().getName());
        playerTwoHero = new Hero(
                startInput.getPlayerTwoHero().getMana(),
                startInput.getPlayerTwoHero().getDescription(),
                startInput.getPlayerTwoHero().getColors(),
                startInput.getPlayerTwoHero().getName());

        setTurn(inputData.getGames().get(gameNumber).getStartGame().getStartingPlayer());
        setStartPlayer(getTurn());

        int playerOneDeckIdx = startInput.getPlayerOneDeckIdx();
        int seed = startInput.getShuffleSeed();
        ArrayList<CardInput> playerOneDeckInput = inputData.getPlayerOneDecks().getDecks().get(playerOneDeckIdx);

        playerOneDeck = new Deck();
        int nrCardsInDeck = inputData.getPlayerOneDecks().getNrCardsInDeck();
        playerOneDeck.setupDeck(playerOneDeckInput, seed, nrCardsInDeck);

        playerOneHand = new ArrayList<Card>();
        playerOneHand.add(playerOneDeck.getCards().get(0));
        playerOneDeck.getCards().remove(0);
        playerOneDeck.setNrCardsInDeck(playerOneDeck.getNrCardsInDeck() - 1);

        int playerTwoDeckIdx = startInput.getPlayerTwoDeckIdx();
        ArrayList<CardInput> playerTwoDeckInput = inputData.getPlayerTwoDecks().getDecks().get(playerTwoDeckIdx);

        playerTwoDeck = new Deck();
        playerTwoDeck.setupDeck(playerTwoDeckInput, seed, nrCardsInDeck);

        playerTwoHand = new ArrayList<Card>();
        playerTwoHand.add(playerTwoDeck.getCards().get(0));
        playerTwoDeck.getCards().remove(0);
        playerTwoDeck.setNrCardsInDeck(playerTwoDeck.getNrCardsInDeck() - 1);

        board = new ArrayList<ArrayList<Card>>(4);
        board.add(new ArrayList<Card>());
        board.add(new ArrayList<Card>());
        board.add(new ArrayList<Card>());
        board.add(new ArrayList<Card>());
    }
}
