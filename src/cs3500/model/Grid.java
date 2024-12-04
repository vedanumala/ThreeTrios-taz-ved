package cs3500.model;

import java.util.List;

/**
 * Represents the game grid structure and layout.
 * Handles structural concerns like dimensions, holes, and valid positions.
 */
public interface Grid {
  /**
   * Gets the total number of rows in the grid.
   *
   * @return total number of rows
   */
  int getTotalRows();

  /**
   * Gets the total number of columns in the grid.
   *
   * @return total number of columns
   */
  int getTotalColumns();

  /**
   * Gets the state of the cell at the given position.
   *
   * @param position the position to check
   * @return the state of the cell at the position
   * @throws IllegalArgumentException if position is null or invalid
   */
  CellState getCellState(Coordinate position);

  /**
   * Sets the state of the cell at the given position.
   *
   * @param position the position to set
   * @param state the state to set
   * @throws IllegalArgumentException if position is null or invalid
   */
  void setCellState(Coordinate position, CellState state);

  /**
   * Checks if the given position is within grid bounds and valid.
   *
   * @param position the position to validate
   * @return true if the position is valid, false otherwise
   */
  boolean isValidPosition(Coordinate position);

  /**
   * Gets all valid adjacent positions for the given position.
   *
   * @param position the position to find adjacents for
   * @return list of valid adjacent positions
   * @throws IllegalArgumentException if position is null or invalid
   */
  List<Coordinate> getAdjacentPositions(Coordinate position);

  /**
   * Gets the total number of card cells (non-hole cells) in the grid.
   * Must be odd as per game requirements.
   *
   * @return number of card cells
   */
  int getCardCellCount();

  /**
   * Creates a deep copy of this grid.
   *
   * @return a new Grid with the same dimensions and cell states
   */
  Grid copy();
}