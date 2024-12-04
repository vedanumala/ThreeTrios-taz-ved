package cs3500.model;

/**
 * Implementation of Three Trios for human vs human gameplay.
 */
public class BasicThreeTriosGame extends AbstractThreeTriosGame {

  /**
   * Constructs a new BasicThreeTriosGame.
   */
  public BasicThreeTriosGame() {
    super();
    players.put(PlayerColor.RED, new GamePlayer(PlayerColor.RED));
    players.put(PlayerColor.BLUE, new GamePlayer(PlayerColor.BLUE));
  }

  @Override
  public void startGame() {
    if (gameState != GameState.INITIALIZATION) {
      throw new IllegalStateException("Game cannot start in current state");
    }
    currentPlayer = PlayerColor.RED;
    gameState = GameState.WAITING_FOR_MOVE;
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

    board.placeCard(card, position);
    getCurrentPlayer().removeCardFromHand(card);

    handleBattles(position);

    if (board.isFull()) {
      gameState = GameState.GAME_OVER;
    } else {
      currentPlayer = currentPlayer.getOpponentColor();
      gameState = GameState.WAITING_FOR_MOVE;
    }
  }
}