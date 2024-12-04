package cs3500.model;

/**
 * Represents a cell on the grid.
 */
public class GameCell implements Cell {
  private final Coordinate coordinate;
  private CellState state;
  private Card card;

  /**
   * Constructor for the GameCell class.
   *
   * @param state the state of the cell
   */
  public GameCell(CellState state, Coordinate coordinate) {
    if (state == null) {
      throw new IllegalArgumentException("State cannot be null");
    }
    this.coordinate = coordinate;
    this.state = state;
    this.card = null;
  }

  @Override
  public CellState getState() {
    return this.state;
  }

  @Override
  public void setState(CellState state) {
    if (state == null) {
      throw new IllegalArgumentException("State cannot be null");
    }
    if (this.state == CellState.OCCUPIED) {
      throw new IllegalStateException("Cannot change state of occupied cell");
    }

    if (this.state == CellState.HOLE && state != CellState.HOLE) {
      throw new IllegalStateException("Cannot change state of hole cell");
    }
    this.state = state;
  }

  @Override
  public Card getCard() {
    return this.card;
  }

  @Override
  public void setCard(Card card) {
    if (card == null) {
      throw new IllegalArgumentException("Card cannot be null");
    }
    if (this.state == CellState.HOLE) {
      throw new IllegalStateException("Cannot place card in hole");
    }
    if (this.state == CellState.OCCUPIED) {
      throw new IllegalStateException("Cell is already occupied");
    }
    this.card = card;
    this.state = CellState.OCCUPIED;
  }

  @Override
  public Coordinate getCoordinate() {
    return this.coordinate;
  }

  @Override
  public GameCell copy() {
    GameCell copy = new GameCell(this.state, this.coordinate);
    if (this.card != null) {
      copy.card = this.card;
    }
    return copy;
  }

  @Override
  public String toString() {
    if (state == CellState.HOLE) {
      return "HOLE";
    } else if (state == CellState.OCCUPIED) {
      return "OCCUPIED (" + card.getIdentifier() + ")";
    } else {
      return "AVAILABLE";
    }
  }
}
