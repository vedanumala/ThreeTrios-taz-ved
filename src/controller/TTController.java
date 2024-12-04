package controller;

import model.Move;

/**
 * Represents the controller for a Three Trios game.
 */
public interface TTController {

  /**
   * Takes actions needed at the start of the player's turn.
   */
  void startTurn();

  /**
   * Plays a card from the current player's hand to the grid.
   *
   * @param move  the move to make
   * @throws IllegalArgumentException if move is null
   */
  void playCard(Move move);
}