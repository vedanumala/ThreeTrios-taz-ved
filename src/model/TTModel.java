package model;

import view.TTGUIView;

import java.util.Scanner;

/**
 * An interface for the model of a Three Trios Game.
 * A model implements a grid of {@link Cell} that are either {@link CardCell} or {@link Hole}.
 * There are two competing {@link Player}s who each have a hand and will try to win over the board.
 */
public interface TTModel extends ReadOnlyTTModel {

  /**
   * Starts a game with the given parameters.
   *
   * @param gridData the {@link Scanner} to read grid data from
   * @param cardData the {@link Scanner} to read card data from
   * @throws IllegalStateException    if the game has already started
   * @throws IllegalArgumentException if either parameter is null
   */
  void startGame(Scanner gridData, Scanner cardData, TTGUIView viewRed, TTGUIView viewBlue);

  /**
   * Plays the card from the current player's hand at the given card index to the given
   * position in the grid.
   *
   * @param move  the move to be played
   * @throws IllegalArgumentException if the move is invalid
   * @throws IllegalStateException    if game has ended or has not started
   */
  void playCard(Move move);

  /**
   * Converts the neighbors of the card at (row, col) to the same owner as the
   * card if it wins in a battle. Any defeated cards will also have
   * battleNeighbors called on them.
   *
   * @param row the row of the card battling its neighbors
   * @param col the column of the card battling its neighbors
   * @throws IllegalArgumentException if the row or column value isn't valid
   * @throws IllegalArgumentException if the position doesn't hold a card
   */
  void battleNeighbors(int row, int col);

  /**
   * Switches which Player's turn it is and updates the views.
   */
  void toggleTurn();

  /**
   * Determines how many cards would be flipped if a player places a given card
   * down at the given coords.
   * @param move the move to be tested
   * @return the number of cards that would be flipped
   * @throws IllegalArgumentException if the move isn't valid
   */
  int numFlippedAtGivenCoords(Move move);

  /**
   * Creates a copy of the {@link TTModel} with identical fields.
   * @return a copy of the {@link TTModel}.
   */
  TTModel copy();
}