package cs3500.model;

/**
 * Represents the possible states of a cell on the grid.
 */
public enum CellState {

  /**
   * Cell is available for card placement.
   */
  AVAILABLE,

  /**
   * Cell is occupied by a card.
   */
  OCCUPIED,

  /**
   * Cell is a hole and cannot hold cards.
   */
  HOLE
}