package model;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Interface for a read-only model of Three Trios.
 */
public interface ReadOnlyTTModel {

  /**
   * Determines if the game has ended.
   *
   * @return if the game is over
   * @throws IllegalStateException if the game hasn't started yet
   */
  boolean isGameOver();

  /**
   * Determines which player won the game.
   *
   * @return the {@link Player} with the most captured cells on the grid
   * @throws IllegalStateException if the game hasn't started yet or is not yet over
   */
  Optional<Player> getWinner();

  /**
   * Retrieves the cards in the given player's hand.
   *
   * @return the given player's hand
   */
  ArrayList<TTCard> getHand(Player player);

  /**
   * Retrieves the grid in its current game state.
   *
   * @return the 2D ArrayList with the current layout of the grid
   */
  ArrayList<ArrayList<Cell>> getGrid();

  /**
   * Retrieves the Player who is playing the current turn.
   *
   * @return the player for this turn
   */
  Player getPlayerTurn();
}