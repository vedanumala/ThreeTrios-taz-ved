package cs3500.model;

import cs3500.strategy.CornerStrategy;
import cs3500.strategy.MaxFlipsStrategy;

/**
 * Implementation of Three Trios that supports AI players.
 */
public class AIThreeTriosGame extends AbstractThreeTriosGame {

  /**
   * Constructs a new AI Three Trios game.
   */
  public AIThreeTriosGame() {
    super();
  }

  /**
   * Initializes the game with specific player types.
   */
  public void initializePlayersWithTypes(String redPlayerType, String bluePlayerType) {
    players.put(PlayerColor.RED, createPlayer(PlayerColor.RED, redPlayerType));
    players.put(PlayerColor.BLUE, createPlayer(PlayerColor.BLUE, bluePlayerType));
  }

  private Player createPlayer(PlayerColor color, String playerType) {
    if (playerType == null) {
      throw new IllegalArgumentException("Player type cannot be null");
    }

    switch (playerType.toLowerCase()) {
      case "human":
        return new GamePlayer(color);
      case "maxflips":
        return new AIPlayer(color, new MaxFlipsStrategy(), this);
      case "corner":
        return new AIPlayer(color, new CornerStrategy(), this);
      default:
        throw new IllegalArgumentException(
                "Invalid player type: " + playerType + ". Use 'human', 'maxflips', or 'corner'");
    }
  }

  @Override
  public void startGame() {
    if (gameState != GameState.INITIALIZATION) {
      throw new IllegalStateException("Game cannot start in current state");
    }
    currentPlayer = PlayerColor.RED;
    gameState = GameState.WAITING_FOR_MOVE;

    // If first player is AI, make their move
    checkAndExecuteAIMove();
  }

  @Override
  public void playCard(Card card, Coordinate position) {
    if (card == null || position == null) {
      throw new IllegalArgumentException("Card and position cannot be null");
    }
    if (gameState == GameState.GAME_OVER) {
      throw new IllegalStateException("Game is over");
    }
    if (gameState == GameState.INITIALIZATION) {
      throw new IllegalStateException("Game has not started yet");
    }
    if (!getCurrentPlayerHand().contains(card)) {
      throw new IllegalStateException("Next player's turn.");
    }
    if (!board.canPlaceCard(position)) {
      throw new IllegalStateException("Cannot place card at position");
    }

    // Execute the move
    board.placeCard(card, position);
    getCurrentPlayer().removeCardFromHand(card);
    handleBattles(position);

    // Check game end
    if (board.isFull()) {
      gameState = GameState.GAME_OVER;
      System.out.println("Game Over!");
      System.out.println("Final Scores - Red: " + getScore(PlayerColor.RED)
              + ", Blue: " + getScore(PlayerColor.BLUE));
      System.out.println("Winner: " + getWinner());
      return;
    }

    // Switch turns
    currentPlayer = currentPlayer.getOpponentColor();
    gameState = GameState.WAITING_FOR_MOVE;

    // If next player is AI, make their move
    checkAndExecuteAIMove();
  }

  private void checkAndExecuteAIMove() {
    Player currentPlayerObj = players.get(currentPlayer);
    if (currentPlayerObj instanceof AIPlayer && gameState == GameState.WAITING_FOR_MOVE) {
      // Add small delay to make AI moves visible
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      if (!((AIPlayer) currentPlayerObj).makeMove()) {
        System.err.println("AI failed to make a move");
      }
    }
  }
}