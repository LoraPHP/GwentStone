package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;

import java.util.ArrayList;


public class GamePlay {

    /**
     * This method makes preparations for the JSON output by converting a Card to ObjectNode.
     *
     * @param card the input card
     * @return the object node containing the card info
     */
    ObjectNode cardToJSON(final Card card) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode cardOut = objectMapper.createObjectNode();
        ArrayNode colors = objectMapper.createArrayNode();

        cardOut.put("mana", card.getMana());
        if (!card.getName().equals("Firestorm")
                && !card.getName().equals("Winterfell")
                && !card.getName().equals("Heart Hound")
                && !card.getName().equals("Lord Royce")
                && !card.getName().equals("Empress Thorina")
                && !card.getName().equals("King Mudface")
                && !card.getName().equals("General Kocioraw")) {
            cardOut.put("attackDamage", ((Minion) card).getAttackDamage());
            cardOut.put("health", ((Minion) card).getHealth());
        }

        cardOut.put("description", card.getDescription());
        for (String color:card.getColors()) {
            colors.add(color);
        }
        cardOut.set("colors", colors);
        cardOut.put("name", card.getName());

        if (card.getName().equals("Lord Royce")
                || card.getName().equals("Empress Thorina")
                || card.getName().equals("King Mudface")
                || card.getName().equals("General Kocioraw")) {
            cardOut.put("health", ((Hero) card).getHealth());
        }
        return cardOut;
    }

    /**
     * This method makes preparations for the JSON output by converting a set of cards to an
     * ArrayNode which is useful for displaying a player's deck or the cards in his hand.
     *
     * @param cards the input set of cards
     * @return the array node containing the info of each card
     */
    ArrayNode deckToJSON(final ArrayList<Card> cards) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode deckOut = objectMapper.createObjectNode();
        ArrayNode deckOutList = objectMapper.createArrayNode();
        for (int i = 0; i < cards.size(); i++) {
            deckOutList.add(cardToJSON(cards.get(i)));
        }
        deckOut.set("output", deckOutList);
        return deckOutList;
    }

    /**
     * This method makes preparations for the JSON output by converting an ArrayList of ArrayLists
     * of cards to an ArrayNode which is useful for displaying the cards placed on the table.
     *
     * @param board the cards on the table
     * @return the array node containing the info of each card
     */
    ArrayNode boardToJSON(final ArrayList<ArrayList<Card>> board) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode node = objectMapper.createArrayNode();

        for (int i = 0; i < 4; i++) {
            if (deckToJSON(board.get(i)) != null) {
                node.add(deckToJSON(board.get(i)));
            }
        }
        return node;
    }

    /**
     * This method takes the first card from the player's deck and puts it in his hand.
     *
     * @param deck the deck of the player
     * @param hand the cards in the hand of the player
     */
    void drawCard(final Deck deck, final ArrayList<Card> hand) {
        if (deck.getNrCardsInDeck() > 0) {
            hand.add(deck.getCards().get(0));
            deck.getCards().remove(0);
            deck.setNrCardsInDeck(deck.getNrCardsInDeck() - 1);
        }
    }

    /**
     * This method checks if the current card is of type Environment.
     *
     * @param card the given card
     * @return true if the card is of type environment, false otherwise
     */
    boolean checkEnvironment(final Card card) {
        if (card.getName().equals("Firestorm")
                || card.getName().equals("Winterfell")
                || card.getName().equals("Heart Hound")) {
            return true;
        }
        return false;
    }

    /**
     * This method goes through the cards played on the table and gathers the frozen ones in
     * an ArrayNode which is used to provide the JSON output.
     *
     * @param out used for JSON output
     * @param board the cards played on the table by both players
     */
    void frozenToJSON(final ObjectNode out, final  ArrayList<ArrayList<Card>> board) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode frozenCards = objectMapper.createArrayNode();

        for (int i = 0; i < 4; i++) {
            for (Card card : board.get(i)) {
                if (((Minion) card).isFrozen()) {
                    frozenCards.add(cardToJSON(card));
                }
            }
        }
        out.set("output", frozenCards);
    }

    /**
     * This method resets the frozen and hasAttacked status of the minions.
     *
     * @param gameInfo holds the information about the current game
     * @param playerRow1 first row of minions that needs to be reset
     * @param playerRow2 second row of minions that needs to be reset
     */
    void resetFrozenAndAttacked(final GameInfo gameInfo, final int playerRow1,
                                final int playerRow2) {
        ArrayList<Card> reset1 = gameInfo.getBoard().get(playerRow1);
        ArrayList<Card> reset2 = gameInfo.getBoard().get(playerRow2);
        for (Card card : reset1) {
            ((Minion) card).setFrozen(false);
            ((Minion) card).setHasAttacked(false);
        }
        for (Card card : reset2) {
            ((Minion) card).setFrozen(false);
            ((Minion) card).setHasAttacked(false);
        }
    }

    /**
     * This method performs the necessary checks on the given card and if the card passes then it
     * is placed on the table.
     *
     * @param card the card the current player wants to place
     * @param gameInfo holds the information about the current game
     * @param out used for JSON output
     * @param handIdx the index of the card, used mainly in error outputs
     */
    void placeCard(final Card card, final GameInfo gameInfo, final ObjectNode out,
                   final int handIdx) {
        // if the card is of type Environment it cannot be placed
        if (checkEnvironment(card)) {
            out.put("command", "placeCard");
            out.put("handIdx", handIdx);
            out.put("error", "Cannot place environment card on table.");
            return;
        }
        int player = gameInfo.getTurn();
        if (player == 1) {
            // checks that the player has enough mana to play the card
            if (gameInfo.getPlayerOneTotalMana() < card.getMana()) {
                out.put("command", "placeCard");
                out.put("handIdx", handIdx);
                out.put("error", "Not enough mana to place card on table.");
                return;
            }
            // checks that there is enough space on the table to place the card
            if (card.getName().equals("The Ripper")
                    || card.getName().equals("Miraj")
                    || card.getName().equals("Goliath")
                    || card.getName().equals("Warden")) {
                if (gameInfo.getBoard().get(2).size() == 5) {
                    out.put("command", "placeCard");
                    out.put("handIdx", handIdx);
                    out.put("error", "Cannot place card on table since row is full.");
                } else {
                    // places the card, removes it from the player's hand and decreases the mana
                    gameInfo.getBoard().get(2).add(card);
                    gameInfo.getPlayerOneHand().remove(card);
                    gameInfo.setPlayerOneTotalMana(gameInfo.getPlayerOneTotalMana()
                            - card.getMana());
                }
            } else {
                // checks that there is enough space on the table to place the card
                if (gameInfo.getBoard().get(3).size() == 5) {
                    out.put("command", "placeCard");
                    out.put("handIdx", handIdx);
                    out.put("error", "Cannot place card on table since row is full.");
                } else {
                    // places the card, removes it from the player's hand and decreases the mana
                    gameInfo.getBoard().get(3).add(card);
                    gameInfo.getPlayerOneHand().remove(card);
                    gameInfo.setPlayerOneTotalMana(gameInfo.getPlayerOneTotalMana()
                            - card.getMana());
                }
            }
        } else {
            // checks that there is enough space on the table to place the card
            if (card.getName().equals("The Ripper")
                    || card.getName().equals("Miraj")
                    || card.getName().equals("Goliath")
                    || card.getName().equals("Warden")) {
                if (gameInfo.getBoard().get(1).size() == 5) {
                    out.put("command", "placeCard");
                    out.put("handIdx", handIdx);
                    out.put("error", "Cannot place card on table since row is full.");
                } else {
                    // places the card, removes it from the player's hand and decreases the mana
                    gameInfo.getBoard().get(1).add(card);
                    gameInfo.getPlayerTwoHand().remove(card);
                    gameInfo.setPlayerTwoTotalMana(gameInfo.getPlayerTwoTotalMana()
                            - card.getMana());
                }
            } else {
                // checks that there is enough space on the table to place the card
                if (gameInfo.getBoard().get(0).size() == 5) {
                    out.put("command", "placeCard");
                    out.put("handIdx", handIdx);
                    out.put("error", "Cannot place card on table since row is full.");
                } else {
                    // places the card, removes it from the player's hand and decreases the mana
                    gameInfo.getBoard().get(0).add(card);
                    gameInfo.getPlayerTwoHand().remove(card);
                    gameInfo.setPlayerTwoTotalMana(gameInfo.getPlayerTwoTotalMana()
                            - card.getMana());
                }
            }
        }
    }

    /**
     * This method performs the necessary checks on the given card and if it passes then the
     * Environment card will be used.
     *
     * @param out used for JSON output
     * @param gameInfo holds the information about the current game
     * @param player the current player
     * @param handIdx the index of the card, used mainly in error outputs
     * @param affectedRow the row on the table which the card's effect will be applied to
     */
    void useEnvironment(final ObjectNode out, final GameInfo gameInfo, final int player,
                        final int handIdx, final int affectedRow) {
        if (player == 1) {
            Card card = gameInfo.getPlayerOneHand().get(handIdx);
            if (!checkEnvironment(card)) {
                out.put("command", "useEnvironmentCard");
                out.put("handIdx", handIdx);
                out.put("affectedRow", affectedRow);
                out.put("error", "Chosen card is not of type environment.");
                return;
            }
            // checks that the player has enough mana to play the card
            if (card.getMana() > gameInfo.getPlayerOneTotalMana()) {
                out.put("command", "useEnvironmentCard");
                out.put("handIdx", handIdx);
                out.put("affectedRow", affectedRow);
                out.put("error", "Not enough mana to use environment card.");
                return;
            }
            if (affectedRow > 1) {
                out.put("command", "useEnvironmentCard");
                out.put("handIdx", handIdx);
                out.put("affectedRow", affectedRow);
                out.put("error", "Chosen row does not belong to the enemy.");
                return;
            }
            // checks that there is enough space to steal a card
            if (card.getName().equals("Heart Hound")
                    && ((affectedRow == 0 && gameInfo.getBoard().get(3).size() == 5)
                    || (affectedRow == 1 && gameInfo.getBoard().get(2).size() == 5))) {
                out.put("command", "useEnvironmentCard");
                out.put("handIdx", handIdx);
                out.put("affectedRow", affectedRow);
                out.put("error", "Cannot steal enemy card since the player's row is full.");
                return;
            }
            // plays the environment card, decreases mana and removes the card from hand
            ((Environment) card).effect(gameInfo.getBoard(), affectedRow);
            gameInfo.setPlayerOneTotalMana(gameInfo.getPlayerOneTotalMana() - card.getMana());
            gameInfo.getPlayerOneHand().remove(handIdx);

        } else {
            Card card = gameInfo.getPlayerTwoHand().get(handIdx);
            if (!checkEnvironment(card)) {
                out.put("command", "useEnvironmentCard");
                out.put("handIdx", handIdx);
                out.put("affectedRow", affectedRow);
                out.put("error", "Chosen card is not of type environment.");
                return;
            }
            // checks that the player has enough mana to play the card
            if (card.getMana() > gameInfo.getPlayerTwoTotalMana()) {
                out.put("command", "useEnvironmentCard");
                out.put("handIdx", handIdx);
                out.put("affectedRow", affectedRow);
                out.put("error", "Not enough mana to use environment card.");
                return;
            }
            if (affectedRow < 2) {
                out.put("command", "useEnvironmentCard");
                out.put("handIdx", handIdx);
                out.put("affectedRow", affectedRow);
                out.put("error", "Chosen row does not belong to the enemy.");
                return;
            }
            // checks that there is enough space to steal a card
            if (card.getName().equals("Heart Hound")
                    && ((affectedRow == 2 && gameInfo.getBoard().get(1).size() == 5)
                    || (affectedRow == 3 && gameInfo.getBoard().get(0).size() == 5))) {
                out.put("command", "useEnvironmentCard");
                out.put("handIdx", handIdx);
                out.put("affectedRow", affectedRow);
                out.put("error", "Cannot steal enemy card since the player's row is full.");
                return;
            }
            // plays the environment card, decreases mana and removes the card from hand
            ((Environment) card).effect(gameInfo.getBoard(), affectedRow);
            gameInfo.setPlayerTwoTotalMana(gameInfo.getPlayerTwoTotalMana() - card.getMana());
            gameInfo.getPlayerTwoHand().remove(handIdx);
        }
    }
    /**
     * This method iterates through the cards in the current player's hand and gathers a list of
     * the Environment cards.
     *
     * @param out used for JSON output
     * @param hand the cards in the hand of the current player
     */
    void getEnvCards(final ObjectNode out, final ArrayList<Card> hand) {
        ArrayList<Card> envCards = new ArrayList<Card>();
        for (Card card : hand) {
            if (checkEnvironment(card)) {
                envCards.add(card);
            }
        }
        out.set("output", deckToJSON(envCards));
    }

    /**
     * This method calculates and distributes the correct number of mana points for each player.
     *
     * @param gameInfo holds the information about the current game
     */
    void giveMana(final GameInfo gameInfo) {
        int manaOne = gameInfo.getPlayerOneTotalMana();
        int manaTwo = gameInfo.getPlayerTwoTotalMana();
        // if the game is past round 10 each player receives 10 mana
        if (gameInfo.getRound() >= 10) {
            gameInfo.setPlayerOneTotalMana(manaOne + 10);
            gameInfo.setPlayerTwoTotalMana(manaTwo + 10);
        } else {
            gameInfo.setPlayerOneTotalMana(manaOne + gameInfo.getRound());
            gameInfo.setPlayerTwoTotalMana(manaTwo + gameInfo.getRound());
        }
    }

    /**
     * This checks if there are any tank minions on a players row.
     *
     * @param gameInfo holds the information about the current game
     * @param player the given player
     * @return true if any tank minion is found, false otherwise
     */
    boolean checkTankOnRow(final GameInfo gameInfo, final int player) {
        ArrayList<Card> cardRow;
        if (player == 1) {
            cardRow = gameInfo.getBoard().get(1);
        } else {
            cardRow = gameInfo.getBoard().get(2);
        }
        for (Card card : cardRow) {
            if (((Minion) card).isTank()) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method implements the attack mechanic between cards. If any parameter is incorrect
     * there will be an error message and the attack will fail.
     *
     * @param out used for JSON output
     * @param gameInfo holds the information about the current game
     * @param act holds information about the attacker and attacked
     */
    void cardAttack(final ObjectNode out, final GameInfo gameInfo, final ActionsInput act) {
        int player = gameInfo.getTurn();
        // a player's minion cannot attack an allied minion
        if ((player == 1 && act.getCardAttacked().getX() > 1)
                || (player == 2 && act.getCardAttacked().getX() < 2)) {
            out.put("command", act.getCommand());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode aux = objectMapper.createObjectNode();

            aux.put("x", act.getCardAttacker().getX());
            aux.put("y", act.getCardAttacker().getY());
            out.set("cardAttacker", aux);
            aux = objectMapper.createObjectNode();
            aux.put("x", act.getCardAttacked().getX());
            aux.put("y", act.getCardAttacked().getY());
            out.set("cardAttacked", aux);

            out.put("error", "Attacked card does not belong to the enemy.");
            return;
        }

        Minion minion = (Minion) (gameInfo.getBoard().get(act.getCardAttacker().getX())
                .get(act.getCardAttacker().getY()));
        // a minion cannot attack if it's frozen or if it has already attacked this turn
        if (minion.hasAttacked() || minion.isFrozen()) {
            out.put("command", act.getCommand());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode aux = objectMapper.createObjectNode();

            aux.put("x", act.getCardAttacker().getX());
            aux.put("y", act.getCardAttacker().getY());
            out.set("cardAttacker", aux);
            aux = objectMapper.createObjectNode();
            aux.put("x", act.getCardAttacked().getX());
            aux.put("y", act.getCardAttacked().getY());
            out.set("cardAttacked", aux);
            if (minion.hasAttacked()) {
                out.put("error", "Attacker card has already attacked this turn.");
            }
            if (minion.isFrozen()) {
                out.put("error", "Attacker card is frozen.");
            }
            return;
        }
        Minion attacked = (Minion) gameInfo.getBoard().get(act.getCardAttacked().getX())
                .get(act.getCardAttacked().getY());
        // if there is an enemy tank, the minion has to target it
        if (!attacked.isTank() && checkTankOnRow(gameInfo, gameInfo.getTurn())) {
            out.put("command", act.getCommand());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode aux = objectMapper.createObjectNode();

            aux.put("x", act.getCardAttacker().getX());
            aux.put("y", act.getCardAttacker().getY());
            out.set("cardAttacker", aux);
            aux = objectMapper.createObjectNode();
            aux.put("x", act.getCardAttacked().getX());
            aux.put("y", act.getCardAttacked().getY());
            out.set("cardAttacked", aux);
            out.put("error", "Attacked card is not of type 'Tank'.");
            return;
        }
        // the minion attacks and if the target dies, it is removed from the game
        minion.setHasAttacked(true);
        attacked.setHealth(attacked.getHealth() - minion.getAttackDamage());
        if (attacked.getHealth() < 1) {
            gameInfo.getBoard().get(act.getCardAttacked().getX()).remove(attacked);
        }
    }

    /**
     * This method uses a card's ability.
     *
     * @param out used for JSON output
     * @param gameInfo holds the information about the current game
     * @param act holds information about the attacker and attacked
     */
    void cardAbility(final ObjectNode out, final GameInfo gameInfo, final ActionsInput act) {
        Minion attacker = (Minion) gameInfo.getBoard().get(act.getCardAttacker().getX())
                .get(act.getCardAttacker().getY());
        // a minion cannot attack if it's frozen or if it has already attacked this turn
        if (attacker.isFrozen() || attacker.hasAttacked()) {
            out.put("command", act.getCommand());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode aux = objectMapper.createObjectNode();

            aux.put("x", act.getCardAttacker().getX());
            aux.put("y", act.getCardAttacker().getY());
            out.set("cardAttacker", aux);
            aux = objectMapper.createObjectNode();
            aux.put("x", act.getCardAttacked().getX());
            aux.put("y", act.getCardAttacked().getY());
            out.set("cardAttacked", aux);
            if (attacker.isFrozen()) {
                out.put("error", "Attacker card is frozen.");
            }
            if (attacker.hasAttacked()) {
                out.put("error", "Attacker card has already attacked this turn.");
            }
            return;
        }
        int player = gameInfo.getTurn();
        // a Disciple can only target allied minions
        if (attacker.getName().equals("Disciple")) {
            if ((player == 1 && act.getCardAttacked().getX() < 2)
                    || (player == 2 && act.getCardAttacked().getX() > 1)) {
                out.put("command", act.getCommand());
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode aux = objectMapper.createObjectNode();

                aux.put("x", act.getCardAttacker().getX());
                aux.put("y", act.getCardAttacker().getY());
                out.set("cardAttacker", aux);
                aux = objectMapper.createObjectNode();
                aux.put("x", act.getCardAttacked().getX());
                aux.put("y", act.getCardAttacked().getY());
                out.set("cardAttacked", aux);
                out.put("error", "Attacked card does not belong to the current player.");
                return;
            }
            // use minion ability and mark that it has attacked
            ((SpecialMinion) attacker).ability(gameInfo, act);
            attacker.setHasAttacked(true);
        } else {
            if ((player == 1 && act.getCardAttacked().getX() > 1)
                    || (player == 2 && act.getCardAttacked().getX() < 2)) {
                out.put("command", act.getCommand());
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode aux = objectMapper.createObjectNode();

                aux.put("x", act.getCardAttacker().getX());
                aux.put("y", act.getCardAttacker().getY());
                out.set("cardAttacker", aux);
                aux = objectMapper.createObjectNode();
                aux.put("x", act.getCardAttacked().getX());
                aux.put("y", act.getCardAttacked().getY());
                out.set("cardAttacked", aux);
                out.put("error", "Attacked card does not belong to the enemy.");
                return;
            }
            Minion attacked = (Minion) gameInfo.getBoard().get(act.getCardAttacked().getX())
                    .get(act.getCardAttacked().getY());
            // if there is a tank on the enemy row, the minion must target it
            if (!attacked.isTank() && checkTankOnRow(gameInfo, gameInfo.getTurn())) {
                out.put("command", act.getCommand());
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode aux = objectMapper.createObjectNode();

                aux.put("x", act.getCardAttacker().getX());
                aux.put("y", act.getCardAttacker().getY());
                out.set("cardAttacker", aux);
                aux = objectMapper.createObjectNode();
                aux.put("x", act.getCardAttacked().getX());
                aux.put("y", act.getCardAttacked().getY());
                out.set("cardAttacked", aux);
                out.put("error", "Attacked card is not of type 'Tank'.");
                return;
            }
            // use minion ability and mark that it has attacked
            ((SpecialMinion) attacker).ability(gameInfo, act);
            attacker.setHasAttacked(true);
        }
    }
    /**
     * This method implements the minion attack on a hero. If the hero's health reaches 0
     * that player loses and the game ends.
     *
     * @param out used for JSON output
     * @param gameInfo holds the information about the current game
     * @param act holds information about the attacker and attacked
     * @param solver used to update the number of wins
     */
    void attackHero(final ObjectNode out, final GameInfo gameInfo, final ActionsInput act,
                    final Solver solver) {
        Minion attacker = (Minion) gameInfo.getBoard().get(act.getCardAttacker().getX())
                .get(act.getCardAttacker().getY());
        // a minion cannot attack if it's frozen or if it has already attacked this turn
        if (attacker.isFrozen() || attacker.hasAttacked()) {
            out.put("command", act.getCommand());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode aux = objectMapper.createObjectNode();

            aux.put("x", act.getCardAttacker().getX());
            aux.put("y", act.getCardAttacker().getY());
            out.set("cardAttacker", aux);

            if (attacker.isFrozen()) {
                out.put("error", "Attacker card is frozen.");
            }
            if (attacker.hasAttacked()) {
                out.put("error", "Attacker card has already attacked this turn.");
            }
            return;
        }
        // the hero cannot be attacked if he has a tank minion on the table
        if (checkTankOnRow(gameInfo, gameInfo.getTurn())) {
            out.put("command", act.getCommand());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode aux = objectMapper.createObjectNode();

            aux.put("x", act.getCardAttacker().getX());
            aux.put("y", act.getCardAttacker().getY());
            out.set("cardAttacker", aux);

            out.put("error", "Attacked card is not of type 'Tank'.");
            return;
        }
        Hero hero;
        if (gameInfo.getTurn() == 1) {
            hero = gameInfo.getPlayerTwoHero();
            hero.setHealth(hero.getHealth() - attacker.getAttackDamage());
            attacker.setHasAttacked(true);
            // if the hero's health reaches 0 the game ends and the other player gets a win
            if (hero.getHealth() < 1) {
                solver.setPlayerOneWins(solver.getPlayerOneWins() + 1);
                out.put("gameEnded", "Player one killed the enemy hero.");
            }

        } else {
            hero = gameInfo.getPlayerOneHero();
            hero.setHealth(hero.getHealth() - attacker.getAttackDamage());
            attacker.setHasAttacked(true);
            // if the hero's health reaches 0 the game ends and the other player gets a win
            if (hero.getHealth() < 1) {
                solver.setPlayerTwoWins(solver.getPlayerTwoWins() + 1);
                out.put("gameEnded", "Player two killed the enemy hero.");
            }

        }
    }

    /**
     * This method implements the hero's ability. Depending on the hero, it can affect both
     * allied and enemy minions.
     *
     * @param out used for JSON output
     * @param gameInfo holds the information about the current game
     * @param act holds information about the attacker and attacked
     */
    void heroAbility(final ObjectNode out, final GameInfo gameInfo, final ActionsInput act) {
        Hero hero;
        if (gameInfo.getTurn() == 1) {
            hero = gameInfo.getPlayerOneHero();
            // the hero needs enough mana to use his ability
            if (hero.getMana() > gameInfo.getPlayerOneTotalMana()) {
                out.put("command", act.getCommand());
                out.put("affectedRow", act.getAffectedRow());
                out.put("error", "Not enough mana to use hero's ability.");
                return;
            }
            // the hero cannot use his ability multiple times in a turn
            if (hero.isHasAttacked()) {
                out.put("command", act.getCommand());
                out.put("affectedRow", act.getAffectedRow());
                out.put("error", "Hero has already attacked this turn.");
                return;
            }
            // the hero must use his ability on a valid row
            if ((hero.getName().equals("Lord Royce") || hero.getName().equals("Empress Thorina"))
                    && act.getAffectedRow() > 1) {
                out.put("command", act.getCommand());
                out.put("affectedRow", act.getAffectedRow());
                out.put("error", "Selected row does not belong to the enemy.");
                return;
            }
            if ((hero.getName().equals("General Kocioraw") || hero.getName().equals("King Mudface"))
                    && act.getAffectedRow() < 2) {
                out.put("command", act.getCommand());
                out.put("affectedRow", act.getAffectedRow());
                out.put("error", "Selected row does not belong to the current player.");
                return;
            }
            // the player's mana decreases
            gameInfo.setPlayerOneTotalMana(gameInfo.getPlayerOneTotalMana() - hero.getMana());
        } else {
            hero = gameInfo.getPlayerTwoHero();
            // the hero needs enough mana to use his ability
            if (hero.getMana() > gameInfo.getPlayerTwoTotalMana()) {
                out.put("command", act.getCommand());
                out.put("affectedRow", act.getAffectedRow());
                out.put("error", "Not enough mana to use hero's ability.");
                return;
            }
            // the hero cannot use his ability multiple times in a turn
            if (hero.isHasAttacked()) {
                out.put("command", act.getCommand());
                out.put("affectedRow", act.getAffectedRow());
                out.put("error", "Hero has already attacked this turn.");
                return;
            }
            // the hero must use his ability on a valid row
            if ((hero.getName().equals("Lord Royce") || hero.getName().equals("Empress Thorina"))
                    && act.getAffectedRow() < 2) {
                out.put("command", act.getCommand());
                out.put("affectedRow", act.getAffectedRow());
                out.put("error", "Selected row does not belong to the enemy.");
                return;
            }
            if ((hero.getName().equals("General Kocioraw") || hero.getName().equals("King Mudface"))
                    && act.getAffectedRow() > 1) {
                out.put("command", act.getCommand());
                out.put("affectedRow", act.getAffectedRow());
                out.put("error", "Selected row does not belong to the current player.");
                return;
            }
            // the player's mana decreases
            gameInfo.setPlayerTwoTotalMana(gameInfo.getPlayerTwoTotalMana() - hero.getMana());
        }
        // the hero uses his ability and it's marked that he cannot attack anymore this turn
        hero.ability(gameInfo.getBoard().get(act.getAffectedRow()));
        hero.setHasAttacked(true);
    }
    /**
     * This method is responsible to call the correct method to execute the given action.
     *
     * @param actions a list of the given commands that contains info about each of them
     * @param gameInfo holds the information about the current game
     * @param games the number of games played
     * @param solver holds info about the number of wins
     * @param output used for JSON output
     * @return an ArrayNode containing the outputs of all the commands given during the games
     */
    ArrayNode executeActions(final ArrayList<ActionsInput> actions, final GameInfo gameInfo,
                             final int games, final Solver solver, final ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        // iterates through the list of commands
        for (ActionsInput act : actions) {
            ObjectNode out = objectMapper.createObjectNode();
            switch (act.getCommand()) {
                case "getPlayerDeck":
                    out.put("command", act.getCommand());
                    out.put("playerIdx", act.getPlayerIdx());
                    if (act.getPlayerIdx() == 1) {
                        out.set("output", deckToJSON(gameInfo.getPlayerOneDeck().getCards()));
                    } else {
                        out.set("output", deckToJSON(gameInfo.getPlayerTwoDeck().getCards()));
                    }
                    break;
                case "getPlayerTurn":
                    out.put("command", act.getCommand());
                    out.put("output", gameInfo.getTurn());
                    break;

                case "getPlayerHero":
                    out.put("command", act.getCommand());
                    out.put("playerIdx", act.getPlayerIdx());
                    if (act.getPlayerIdx() == 1) {
                        out.set("output", cardToJSON(gameInfo.getPlayerOneHero()));
                    } else {
                        out.set("output", cardToJSON(gameInfo.getPlayerTwoHero()));
                    }

                    break;
                case "placeCard":
                    if (gameInfo.getTurn() == 1) {
                        placeCard(gameInfo.getPlayerOneHand().get(act.getHandIdx()),
                                gameInfo, out, act.getHandIdx());
                    } else {
                        placeCard(gameInfo.getPlayerTwoHand().get(act.getHandIdx()),
                                gameInfo, out, act.getHandIdx());
                    }
                    break;
                case "endPlayerTurn":
                    if (gameInfo.getTurn() == 1) {
                        gameInfo.setTurn(2);
                        resetFrozenAndAttacked(gameInfo, 2, 3);
                        gameInfo.getPlayerOneHero().setHasAttacked(false);
                    } else {
                        gameInfo.setTurn(1);
                        resetFrozenAndAttacked(gameInfo, 0, 1);
                        gameInfo.getPlayerTwoHero().setHasAttacked(false);
                    }
                    if (gameInfo.getStartPlayer() == gameInfo.getTurn()) {
                        gameInfo.setRound(gameInfo.getRound() + 1);
                        giveMana(gameInfo);
                        drawCard(gameInfo.getPlayerTwoDeck(), gameInfo.getPlayerTwoHand());
                        drawCard(gameInfo.getPlayerOneDeck(), gameInfo.getPlayerOneHand());
                    }
                    break;
                case "getCardsInHand":
                    out.put("command", act.getCommand());
                    out.put("playerIdx", act.getPlayerIdx());
                    if (act.getPlayerIdx() == 1) {
                        out.set("output", deckToJSON(gameInfo.getPlayerOneHand()));
                    } else {
                        out.set("output", deckToJSON(gameInfo.getPlayerTwoHand()));
                    }

                    break;
                case "getPlayerMana":
                    out.put("command", act.getCommand());
                    out.put("playerIdx", act.getPlayerIdx());
                    if (act.getPlayerIdx() == 1) {
                        out.put("output", gameInfo.getPlayerOneTotalMana());
                    } else {
                        out.put("output", gameInfo.getPlayerTwoTotalMana());
                    }
                    break;
                case "getCardsOnTable":
                    out.put("command", act.getCommand());
                    out.set("output", boardToJSON(gameInfo.getBoard()));
                    break;
                case "useEnvironmentCard":
                    useEnvironment(out, gameInfo, gameInfo.getTurn(), act.getHandIdx(),
                            act.getAffectedRow());
                    break;
                case "getCardAtPosition":
                    out.put("command", act.getCommand());
                    out.put("x", act.getX());
                    out.put("y", act.getY());
                    if (act.getX() > gameInfo.getBoard().size()) {
                        out.put("output", "No card available at that position.");
                    }  else if (gameInfo.getBoard().get(act.getX()).size() < act.getY()) {
                        out.put("output", "No card available at that position.");
                    } else {
                        out.set("output", cardToJSON(gameInfo.getBoard().get(act.getX())
                                .get(act.getY())));
                    }
                    break;
                case "getEnvironmentCardsInHand":
                    out.put("command", act.getCommand());
                    out.put("playerIdx", act.getPlayerIdx());
                    if (act.getPlayerIdx() == 1) {
                        getEnvCards(out, gameInfo.getPlayerOneHand());
                    } else {
                        getEnvCards(out, gameInfo.getPlayerTwoHand());
                    }
                    break;
                case "getFrozenCardsOnTable":
                    out.put("command", act.getCommand());
                    frozenToJSON(out, gameInfo.getBoard());
                    break;
                case "cardUsesAttack":
                    cardAttack(out, gameInfo, act);
                    break;
                case "cardUsesAbility":
                    cardAbility(out, gameInfo, act);
                    break;
                case "useAttackHero":
                    attackHero(out, gameInfo, act, solver);
                    break;
                case "useHeroAbility":
                    heroAbility(out, gameInfo, act);
                    break;
                case "getTotalGamesPlayed":
                    out.put("command", act.getCommand());
                    out.put("output", games);
                    break;
                case "getPlayerOneWins":
                    out.put("command", act.getCommand());
                    out.put("output", solver.getPlayerOneWins());
                    break;
                case "getPlayerTwoWins":
                    out.put("command", act.getCommand());
                    out.put("output", solver.getPlayerTwoWins());
                    break;
                default:
                    System.out.println("Warning: Invalid command.");
            }
            if (!out.isEmpty()) {
                output.add(out);
            }
        }
        return output;
    }
}
