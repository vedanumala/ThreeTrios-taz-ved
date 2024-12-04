package cs3500.controller;

import cs3500.model.Card;
import cs3500.model.Coordinate;
import cs3500.model.PlayerColor;

/**
 * Represents the actions a player can take during the game.
 * This interface defines the contract for handling player interactions
 * such as selecting cards and choosing grid positions.
 */
public interface Features {
  /**
   * Called when a player selects a card from their hand.
   *
   * @param player the player making the selection
   * @param card the selected card
   * @throws IllegalArgumentException if player or card is null
   */
  void handleCardSelect(PlayerColor player, Card card);

  /**
   * Called when a player selects a position on the grid.
   *
   * @param player the player making the selection
   * @param position the selected grid position
   * @throws IllegalArgumentException if player or position is null
   */
  void handleGridSelect(PlayerColor player, Coordinate position);

  /**
   * Called when a player wants to confirm their move with their
   * currently selected card and position.
   *
   * @param player the player confirming the move
   * @throws IllegalArgumentException if player is null
   * @throws IllegalStateException if no card or position is selected
   */
  void handleConfirmMove(PlayerColor player);

  /**
   * Cancels the current selection (both card and position).
   *
   * @param player the player canceling their selection
   * @throws IllegalArgumentException if player is null
   */
  void handleCancelSelection(PlayerColor player);
}