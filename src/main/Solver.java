package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.Input;

class Solver {
    private int game = 1;
    private int playerOneWins = 0;
    private int playerTwoWins = 0;

    public int getGame() {
        return game;
    }

    public void setGame(final int game) {
        this.game = game;
    }

    public int getPlayerOneWins() {
        return playerOneWins;
    }

    public void setPlayerOneWins(final int playerOneWins) {
        this.playerOneWins = playerOneWins;
    }

    public int getPlayerTwoWins() {
        return playerTwoWins;
    }

    public void setPlayerTwoWins(final int playerTwoWins) {
        this.playerTwoWins = playerTwoWins;
    }

    ArrayNode solve(final Input inputData) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode output = objectMapper.createArrayNode();
        if (inputData.getGames().size() > 1) {
            for (int i = 0; i < inputData.getGames().size(); i++) {
                GameInfo gameInfo = new GameInfo();
                GamePlay gamePlay = new GamePlay();
                gameInfo.setupGame(inputData, i);
                gamePlay.executeActions(inputData.getGames().get(i).getActions(),
                        gameInfo, game, this, output);
                game++;
            }
        } else {

            GameInfo gameInfo = new GameInfo();
            GamePlay gamePlay = new GamePlay();
            gameInfo.setupGame(inputData, 0);
            gamePlay.executeActions(inputData.getGames().get(0).getActions(), gameInfo,
                    game, this, output);
        }
        return output;
    }
}
