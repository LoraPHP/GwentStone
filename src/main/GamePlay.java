package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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
    ArrayNode executeActions(ArrayList<ActionsInput> actions, GameInfo gameInfo) {
        ObjectMapper objectMapper = new ObjectMapper();
        //ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();

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
                    if (gameInfo.getTurn() == 1)
                        placeCard(gameInfo.getPlayerOneHand().get(act.getHandIdx()),
                                gameInfo, out, 1, act.getHandIdx());
                    else
                        placeCard(gameInfo.getPlayerTwoHand().get(act.getHandIdx()),
                                gameInfo, out, 2, act.getHandIdx());
                    break;
                case "endPlayerTurn":
                    //reset stats frozen si hasAttacked
                    if (gameInfo.getTurn() == 1) {
                        gameInfo.setTurn(2);
                    } else {
                        gameInfo.setTurn(1);
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
                    System.out.println("STOP");

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


            }
            if (!out.isEmpty())
                list.add(out);
        }
        return list;

    }
}
