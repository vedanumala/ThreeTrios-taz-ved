package model;

/**
 * A move that can be played in a {@link model.TTModel} game, with coordinates on the grid and
 * an index of a card in the current player's hand.
 * Indexing starts at 0.
 */
public class Move {
  private final int row;
  private final int col;
  private final int handIdx;

  /**
   * Initializes a possible move that can be played in a Three Trios game.
   * @param row the row of the move in the grid
   * @param col the column of the move in the grid
   * @param handIdx the index of the hand for the card of the move
   * @throws IllegalArgumentException if any value is negative
   */
  public Move(int row, int col, int handIdx) {
    if (row < 0 || col < 0 || handIdx < 0) {
      throw new IllegalArgumentException("Inputs cannot be negative.");
    }
    this.row = row;
    this.col = col;
    this.handIdx = handIdx;
  }

  /**
   * Retrieves the row coordinate for this move.
   * @return this row
   */
  public int getRow() {
    return this.row;
  }

  /**
   * Retrieves the column coordinate for this move.
   * @return this column
   */
  public int getCol() {
    return this.col;
  }

  /**
   * Retrieves the card index of the hand for this move.
   * @return this card index in the player's hand
   */
  public int getHandIdx() {
    return this.handIdx;
  }
}