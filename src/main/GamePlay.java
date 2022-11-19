package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;

import java.util.ArrayList;


public class GamePlay {

    ObjectNode cardToJSON(Card card) {
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
            cardOut.put("attackDamage", ((Minion)card).getAttackDamage());
            cardOut.put("health", ((Minion)card).getHealth());
        }

        cardOut.put("description", card.getDescription());
        for (String color:card.getColors())
            colors.add(color);
        cardOut.set("colors", colors);
        cardOut.put("name", card.getName());

        if (card.getName().equals("Lord Royce")
                || card.getName().equals("Empress Thorina")
                || card.getName().equals("King Mudface")
                || card.getName().equals("General Kocioraw"))
            cardOut.put("health", ((Hero)card).getHealth());
        return cardOut;
    }
    ArrayNode deckToJSON(ArrayList<Card> cards) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode deckOut = objectMapper.createObjectNode();
        ArrayNode deckOutList = objectMapper.createArrayNode();
        for (int i = 0; i < cards.size(); i++)
            deckOutList.add(cardToJSON(cards.get(i)));
        deckOut.set("output", deckOutList);
        return deckOutList;
    }

    ArrayNode boardToJSON(GameInfo gameInfo) {
        ArrayList<ArrayList<Card>> board = gameInfo.getBoard();
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode node = objectMapper.createArrayNode();

        for (int i = 0; i < 4; i++)
            if (deckToJSON(board.get(i)) != null)
                node.add(deckToJSON(board.get(i)));

        return node;
    }
    void drawCard(Deck deck, ArrayList<Card> hand) {
        if (deck.getNrCardsInDeck() > 0) {
            hand.add(deck.getCards().get(0));
            deck.getCards().remove(0);
            deck.setNrCardsInDeck(deck.getNrCardsInDeck() - 1);
        }
    }

    boolean checkEnvironment(Card card) {
        if (card.getName().equals("Firestorm")
                || card.getName().equals("Winterfell")
                || card.getName().equals("Heart Hound"))
            return true;
        return false;
    }

    void frozenToJSON(ObjectNode out, GameInfo gameInfo) {
        ArrayList<ArrayList<Card>> board = gameInfo.getBoard();
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode frozenCards = objectMapper.createArrayNode();

        for (int i = 0; i < 4; i++)
            for (Card card : board.get(i)) {
                if (((Minion)card).isFrozen())
                    frozenCards.add(cardToJSON(card));
            }
        out.set("output", frozenCards);
    }

    void resetFrozenAndAttacked(GameInfo gameInfo, int playerRow1, int playerRow2) {
        ArrayList<Card> reset1 = gameInfo.getBoard().get(playerRow1);
        ArrayList<Card> reset2 = gameInfo.getBoard().get(playerRow2);
        for (Card card : reset1) {
            ((Minion)card).setFrozen(false);
            ((Minion)card).setHasAttacked(false);
        }
        for (Card card : reset2) {
            ((Minion)card).setFrozen(false);
            ((Minion)card).setHasAttacked(false);
        }
    }

    void placeCard(Card card, GameInfo gameInfo, ObjectNode out, int player, int handIdx) {
        if (checkEnvironment(card)) {
            out.put("command", "placeCard");
            out.put("handIdx", handIdx);
            out.put("error", "Cannot place environment card on table.");
            return ;
        }
        if (player == 1) {
            if (gameInfo.getPlayerOneTotalMana() < card.getMana()) {
                out.put("command", "placeCard");
                out.put("handIdx", handIdx);
                out.put("error", "Not enough mana to place card on table.");
                return ;
            }
            if (card.getName().equals("The Ripper")
                    || card.getName().equals("Miraj")
                    || card.getName().equals("Goliath")
                    ||card.getName().equals("Warden")) {
                if (gameInfo.getBoard().get(2).size() == 5) {
                    out.put("command", "placeCard");
                    out.put("handIdx", handIdx);
                    out.put("error", "Cannot place card on table since row is full.");
                }
                else {
                    gameInfo.getBoard().get(2).add(card);
                    gameInfo.getPlayerOneHand().remove(card);
                    gameInfo.setPlayerOneTotalMana(gameInfo.getPlayerOneTotalMana() - card.getMana());
                }
            } else {
                if (gameInfo.getBoard().get(3).size() == 5) {
                    out.put("command", "placeCard");
                    out.put("handIdx", handIdx);
                    out.put("error", "Cannot place card on table since row is full.");
                }
                else {
                    gameInfo.getBoard().get(3).add(card);
                    gameInfo.getPlayerOneHand().remove(card);
                    gameInfo.setPlayerOneTotalMana(gameInfo.getPlayerOneTotalMana() - card.getMana());
                }
            }
        } else {
            if (card.getName().equals("The Ripper")
                    || card.getName().equals("Miraj")
                    || card.getName().equals("Goliath")
                    ||card.getName().equals("Warden")) {
                if (gameInfo.getBoard().get(1).size() == 5) {
                    out.put("command", "placeCard");
                    out.put("handIdx", handIdx);
                    out.put("error", "Cannot place card on table since row is full.");
                } else {
                    gameInfo.getBoard().get(1).add(card);
                    gameInfo.getPlayerTwoHand().remove(card);
                    gameInfo.setPlayerTwoTotalMana(gameInfo.getPlayerTwoTotalMana() - card.getMana());
                }
            } else {
                if (gameInfo.getBoard().get(0).size() == 5) {
                    out.put("command", "placeCard");
                    out.put("handIdx", handIdx);
                    out.put("error", "Cannot place card on table since row is full.");
                }
                else {
                    gameInfo.getBoard().get(0).add(card);
                    gameInfo.getPlayerTwoHand().remove(card);
                    gameInfo.setPlayerTwoTotalMana(gameInfo.getPlayerTwoTotalMana() - card.getMana());
                }
            }
        }
    }
    void useEnvironment(ObjectNode out, GameInfo gameInfo, int player, int handIdx, int affectedRow) {
        if (player == 1) {
            Card card = gameInfo.getPlayerOneHand().get(handIdx);
            if (!checkEnvironment(card)) {
                out.put("command", "useEnvironmentCard");
                out.put("handIdx", handIdx);
                out.put("affectedRow", affectedRow);
                out.put("error", "Chosen card is not of type environment.");
                return;
            }
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

            if (card.getName().equals("Heart Hound") &&
                    (affectedRow == 0 && gameInfo.getBoard().get(3).size() == 5
                    || affectedRow == 1 && gameInfo.getBoard().get(2).size() == 5)) {
                out.put("command", "useEnvironmentCard");
                out.put("handIdx", handIdx);
                out.put("affectedRow", affectedRow);
                out.put("error", "Cannot steal enemy card since the player's row is full.");
                return;
            }
            ((Environment)card).effect(gameInfo.getBoard(), affectedRow);
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

            if (card.getName().equals("Heart Hound") &&
                    (affectedRow == 2 && gameInfo.getBoard().get(1).size() == 5
                    || affectedRow == 3 && gameInfo.getBoard().get(0).size() == 5)) {
                out.put("command", "useEnvironmentCard");
                out.put("handIdx", handIdx);
                out.put("affectedRow", affectedRow);
                out.put("error", "Cannot steal enemy card since the player's row is full.");
                return;
            }
            ((Environment)card).effect(gameInfo.getBoard(), affectedRow);
            gameInfo.setPlayerTwoTotalMana(gameInfo.getPlayerTwoTotalMana() - card.getMana());
            gameInfo.getPlayerTwoHand().remove(handIdx);
        }
    }

    void getEnvCards(ObjectNode out, ArrayList<Card> hand) {
        ArrayList<Card> envCards = new ArrayList<Card>();
        for (Card card : hand)
            if (checkEnvironment(card))
                envCards.add(card);
        out.set("output", deckToJSON(envCards));
    }
    void giveMana(GameInfo gameInfo) {
        int manaOne = gameInfo.getPlayerOneTotalMana();
        int manaTwo = gameInfo.getPlayerTwoTotalMana();
        if (gameInfo.getRound() >= 10) {
            gameInfo.setPlayerOneTotalMana(manaOne + 10);
            gameInfo.setPlayerTwoTotalMana(manaTwo + 10);
        } else {
            gameInfo.setPlayerOneTotalMana(manaOne + gameInfo.getRound());
            gameInfo.setPlayerTwoTotalMana(manaTwo + gameInfo.getRound());
        }
    }
    boolean checkTankOnRow(GameInfo gameInfo, int player) {
        ArrayList<Card> cardRow;
        if (player == 1)
            cardRow = gameInfo.getBoard().get(1);
        else
            cardRow = gameInfo.getBoard().get(2);
        for (Card card : cardRow) {
            if (((Minion)card).isTank())
                return true;
        }
        return false;
    }
    void cardAttack(ObjectNode out, GameInfo gameInfo, ActionsInput act) {
        int player = gameInfo.getTurn();
        if ((player == 1 && act.getCardAttacked().getX() > 1) || (player == 2 && act.getCardAttacked().getX() < 2)) {
            out.put("command", act.getCommand());
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode coords = objectMapper.createArrayNode();
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

        Minion minion = (Minion)(gameInfo.getBoard().get(act.getCardAttacker().getX()).get(act.getCardAttacker().getY()));
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
            if (minion.hasAttacked())
                out.put("error", "Attacker card has already attacked this turn.");
            if (minion.isFrozen())
                out.put("error", "Attacker card is frozen.");
            return;
        }
        Minion attacked = (Minion) gameInfo.getBoard().get(act.getCardAttacked().getX()).get(act.getCardAttacked().getY());
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
        minion.setHasAttacked(true);
        attacked.setHealth(attacked.getHealth() - minion.getAttackDamage());
        if (attacked.getHealth() < 1)
            gameInfo.getBoard().get(act.getCardAttacked().getX()).remove(attacked);
    }
    ArrayNode executeActions(ArrayList<ActionsInput> actions, GameInfo gameInfo) {
        ObjectMapper objectMapper = new ObjectMapper();

        ArrayNode list = objectMapper.createArrayNode();
        ObjectNode fin = objectMapper.createObjectNode();

        for(ActionsInput act : actions) {
            ObjectNode out = objectMapper.createObjectNode();
            switch (act.getCommand()) {
                case "getPlayerDeck":
                    out.put("command", act.getCommand());
                    out.put("playerIdx", act.getPlayerIdx());
                    if(act.getPlayerIdx() == 1)
                        out.set("output", deckToJSON(gameInfo.getPlayerOneDeck().getCards()));
                    else
                        out.set("output", deckToJSON(gameInfo.getPlayerTwoDeck().getCards()));
                    break;

                case "getPlayerTurn":
                    out.put("command", act.getCommand());
                    out.put("output", gameInfo.getTurn());
                    break;

                case "getPlayerHero":
                    out.put("command", act.getCommand());
                    out.put("playerIdx", act.getPlayerIdx());
                    if(act.getPlayerIdx() == 1)
                        out.set("output", cardToJSON(gameInfo.getPlayerOneHero()));
                    else
                        out.set("output", cardToJSON(gameInfo.getPlayerTwoHero()));

                    break;
                case "placeCard":
                    if (gameInfo.getTurn() == 1) {
                        placeCard(gameInfo.getPlayerOneHand().get(act.getHandIdx()),
                                gameInfo, out, 1, act.getHandIdx());
                    }

                    else
                        placeCard(gameInfo.getPlayerTwoHand().get(act.getHandIdx()),
                                gameInfo, out, 2, act.getHandIdx());
                    break;
                case "endPlayerTurn":
                    if (gameInfo.getTurn() == 1) {
                        gameInfo.setTurn(2);
                        resetFrozenAndAttacked(gameInfo, 2, 3);
                    } else {
                        gameInfo.setTurn(1);
                        resetFrozenAndAttacked(gameInfo, 0, 1);
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
                    if(act.getPlayerIdx() == 1)
                        out.set("output", deckToJSON(gameInfo.getPlayerOneHand()));
                    else
                        out.set("output", deckToJSON(gameInfo.getPlayerTwoHand()));
                    break;
                case "getPlayerMana":
                    out.put("command", act.getCommand());
                    out.put("playerIdx", act.getPlayerIdx());
                    if(act.getPlayerIdx() == 1)
                        out.put("output", gameInfo.getPlayerOneTotalMana());
                    else
                        out.put("output", gameInfo.getPlayerTwoTotalMana());
                    break;
                case "getCardsOnTable":
                    out.put("command", act.getCommand());
                    out.set("output", boardToJSON(gameInfo));
                    break;
                case "useEnvironmentCard":
                    useEnvironment(out, gameInfo, gameInfo.getTurn(), act.getHandIdx(),
                            act.getAffectedRow());
                    break;
                case "getCardAtPosition":
                    out.put("command", act.getCommand());
                    out.put("x", act.getX());
                    out.put("y", act.getY());
                    if (act.getX() > gameInfo.getBoard().size())
                        out.put("output", "No card available at that position.");
                    else if (gameInfo.getBoard().get(act.getX()).size() < act.getY())
                        out.put("output", "No card available at that position.");
                    else
                        out.set("output", cardToJSON(gameInfo.getBoard().get(act.getX()).get(act.getY())));
                    break;
                case "getEnvironmentCardsInHand":
                    out.put("command", act.getCommand());
                    out.put("playerIdx", act.getPlayerIdx());
                    if (act.getPlayerIdx() == 1)
                        getEnvCards(out, gameInfo.getPlayerOneHand());
                    else
                        getEnvCards(out, gameInfo.getPlayerTwoHand());
                    break;
                case "getFrozenCardsOnTable":
                    out.put("command", act.getCommand());
                    frozenToJSON(out, gameInfo);
                    break;
                case "cardUsesAttack":
                    cardAttack(out, gameInfo, act);
                    break;
            }
            if (!out.isEmpty())
                list.add(out);
        }
        return list;

    }
}
