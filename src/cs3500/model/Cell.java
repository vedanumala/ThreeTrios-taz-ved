package cs3500.model;

/**
 * Represents a single cell on the game grid.
 * Internal implementation detail - not exposed to game logic.
 */
interface Cell {
  /**
   * Gets the current state of this cell.
   *
   * @return the cell's state
   */
  CellState getState();

  /**
   * Sets the state of this cell.
   *
   * @param state the new state to set
   * @throws IllegalArgumentException if state is null
   * @throws IllegalStateException if cell is occupied or a hole
   */
  void setState(CellState state);

  /**
   * Gets the card in this cell, if any.
   *
   * @return the card in this cell, or null if empty or hole
   */
  Card getCard();

  /**
   * Places a card in this cell.
   *
   * @param card the card to place
   * @throws IllegalStateException if cell is a hole or already occupied
   * @throws IllegalArgumentException if card is null
   */
  void setCard(Card card);

  /**
   * Gets the coordinate of this cell.
   *
   * @return the coordinate of this cell
   *
   */
  Coordinate getCoordinate();

  /**
   * Creates a deep copy of this cell.
   *
   * @return a new Cell with the same state and card (if any)
   */
  Cell copy();


}