package cs3500.model;

import java.util.List;

/**
 * Read-only interface for the Three Trios game model.
 * Provides methods for observing game state without modifying it.
 */
public interface ReadOnlyThreeTriosModel {
  /**
   * Gets the game board.
   *
   * @return the current game board
   */
  Board getBoard();

  /**
   * Gets the cards in the specified player's hand.
   *
   * @param player the player whose hand to check
   * @return unmodifiable list of cards in player's hand
   * @throws IllegalArgumentException if player is null
   */
  List<Card> getPlayerHand(PlayerColor player);

  /**
   * Gets the current player's color.
   *
   * @return current player's color
   */
  PlayerColor getCurrentPlayerColor();

  /**
   * Calculates number of cards that would be flipped if the given card
   * was played at the given position by current player.
   *
   * @param card card to simulate playing
   * @param position position to simulate playing at
   * @return number of cards that would be flipped
   * @throws IllegalArgumentException if card or position is null or invalid
   */
  int getPotentialFlips(Card card, Coordinate position);

  /**
   * Gets the current state of the game.
   *
   * @return current game state
   */
  GameState getGameState();

  /**
   * Gets the current score for the specified player.
   * Score is determined by the number of cards owned by the player on the board and in their hand.
   *
   * @param player the player whose score to get
   * @return the player's current score
   * @throws IllegalArgumentException if player is null
   */
  int getScore(PlayerColor player);

  /**
   * Gets the winner of the game.
   *
   * @return the winner of the game, or null if game is not over
   */
  PlayerColor getWinner();
}