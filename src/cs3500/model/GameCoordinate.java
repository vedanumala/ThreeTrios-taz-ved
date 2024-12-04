package cs3500.model;

/**
 * Implementation of the Coordinate Class.
 * Represents a position on the game grid with row and column coordinates.
 */
public class GameCoordinate implements Coordinate {

  private final int row;
  private final int col;

  /**
   * Constructor for the GameCoordinate class.
   *
   * @param row the row of the coordinate
   * @param col the column of the coordinate
   */
  public GameCoordinate(int row, int col) {
    this.row = row;
    this.col = col;
  }

  @Override
  public int getRow() {
    return row;
  }

  @Override
  public int getCol() {
    return col;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    GameCoordinate that = (GameCoordinate) other;

    return this.row == that.row && this.col == that.col;
  }

  @Override
  public int hashCode() {
    return 31 * row + col;
  }

  @Override
  public String toString() {
    return "(" + row + ", " + col + ")";
  }
}
