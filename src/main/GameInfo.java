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

    public void setGameNumber(final int gameNumber) {
        this.gameNumber = gameNumber;
    }

    public void setTurn(final int turn) {
        this.turn = turn;
    }

    public void setPlayerOneHero(final Hero playerOneHero) {
        this.playerOneHero = playerOneHero;
    }

    public void setPlayerTwoHero(final Hero playerTwoHero) {
        this.playerTwoHero = playerTwoHero;
    }

    public void setPlayerOneDeck(final Deck playerOneDeck) {
        this.playerOneDeck = playerOneDeck;
    }

    public void setPlayerTwoDeck(final Deck playerTwoDeck) {
        this.playerTwoDeck = playerTwoDeck;
    }

    public void setBoard(final ArrayList<ArrayList<Card>> board) {
        this.board = board;
    }

    public void setPlayerOneHand(final ArrayList<Card> playerOneHand) {
        this.playerOneHand = playerOneHand;
    }

    public void setPlayerTwoHand(final ArrayList<Card> playerTwoHand) {
        this.playerTwoHand = playerTwoHand;
    }
    public int getPlayerOneTotalMana() {
        return playerOneTotalMana;
    }

    public void setPlayerOneTotalMana(final int playerOneTotalMana) {
        this.playerOneTotalMana = playerOneTotalMana;
    }

    public int getPlayerTwoTotalMana() {
        return playerTwoTotalMana;
    }

    public void setPlayerTwoTotalMana(final int playerTwoTotalMana) {
        this.playerTwoTotalMana = playerTwoTotalMana;
    }

    public int getRound() {
        return round;
    }

    public void setRound(final int round) {
        this.round = round;
    }

    public int getStartPlayer() {
        return startPlayer;
    }

    public void setStartPlayer(final int startPlayer) {
        this.startPlayer = startPlayer;
    }

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
        }
        return hero;
    }

    void setupGame(final Input inputData, final int gameNumber) {
        StartGameInput startInput = inputData.getGames().get(gameNumber).getStartGame();

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

        setTurn(inputData.getGames().get(gameNumber).getStartGame().getStartingPlayer());
        setStartPlayer(getTurn());

        int playerOneDeckIdx = startInput.getPlayerOneDeckIdx();
        int seed = startInput.getShuffleSeed();
        ArrayList<CardInput> playerOneDeckInput = inputData.getPlayerOneDecks().getDecks()
                .get(playerOneDeckIdx);

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

        board = new ArrayList<ArrayList<Card>>(4);
        board.add(new ArrayList<Card>());
        board.add(new ArrayList<Card>());
        board.add(new ArrayList<Card>());
        board.add(new ArrayList<Card>());
    }
}
