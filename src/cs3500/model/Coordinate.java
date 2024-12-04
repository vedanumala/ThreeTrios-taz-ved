package cs3500.model;

/**
 * Represents a position on the game grid with row and column coordinates.
 */
public interface Coordinate {
  /**
   * Gets the row of this coordinate.
   *
   * @return the row of this coordinate (0-based index)
   */
  int getRow();

  /**
   * Gets the column of this coordinate.
   *
   * @return the column of this coordinate (0-based index)
   */
  int getCol();
}
