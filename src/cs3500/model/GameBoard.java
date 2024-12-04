package cs3500.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the game board state and card interactions.
 * Delegates structural concerns to the model.Grid interface.
 * Represents the game board.
 * Handles card placement, flipping, and battle resolution.
 *
 */
public class GameBoard implements Board {
  private final Grid grid;
  private final Map<Coordinate, Card> cardPositions;

  /**
   * Constructor for the GameBoard class.
   *
   * @param grid the grid structure to use
   */
  public GameBoard(Grid grid) {
    if (grid == null) {
      throw new IllegalArgumentException("Grid cannot be null");
    }

    this.grid = grid;
    this.cardPositions = new HashMap<>();
  }

  @Override
  public Grid getGrid() {
    return this.grid;
  }

  @Override
  public boolean canPlaceCard(Coordinate position) {
    if (position == null) {
      throw new IllegalArgumentException("Position cannot be null");
    }
    if (!grid.isValidPosition(position)) {
      throw new IllegalArgumentException("Invalid position");
    }
    return grid.getCellState(position) == CellState.AVAILABLE
            && !cardPositions.containsKey(position);
  }

  @Override
  public void placeCard(Card card, Coordinate position) {
    if (card == null || position == null) {
      throw new IllegalArgumentException("Card and position cannot be null");
    }
    if (!grid.isValidPosition(position)) {
      throw new IllegalArgumentException("Invalid position");
    }
    if (grid.getCellState(position) == CellState.HOLE) {
      throw new IllegalStateException("Cannot place card in hole");
    }
    if (grid.getCellState(position) == CellState.OCCUPIED
            || cardPositions.containsKey(position)) {
      throw new IllegalStateException("Position already occupied");
    }

    cardPositions.put(position, card);
    grid.setCellState(position, CellState.OCCUPIED);
  }

  @Override
  public Card getCardAt(Coordinate position) {
    if (position == null) {
      throw new IllegalArgumentException("Position cannot be null");
    }
    if (!grid.isValidPosition(position)) {
      throw new IllegalArgumentException("Invalid position");
    }
    if (grid.getCellState(position) == CellState.HOLE) {
      throw new IllegalStateException("Cannot get card from hole");
    }

    return cardPositions.get(position);
  }

  @Override
  public List<Card> getAdjacentCards(Coordinate position) {
    if (position == null) {
      throw new IllegalArgumentException("Position cannot be null");
    }
    if (!grid.isValidPosition(position)) {
      throw new IllegalArgumentException("Invalid position");
    }

    List<Card> adjacentCards = new ArrayList<>();
    List<Coordinate> adjacentPositions = grid.getAdjacentPositions(position);

    // Return cards in order: North, South, East, West
    // Add null for positions without cards
    Map<Direction, Card> directionMap = new HashMap<>();

    for (Coordinate adjPos : adjacentPositions) {
      Direction direction = getDirection(position, adjPos);
      if (grid.getCellState(adjPos) == CellState.OCCUPIED) {
        directionMap.put(direction, getCardAt(adjPos));
      } else {
        directionMap.put(direction, null);
      }
    }

    // Ensure consistent ordering
    adjacentCards.add(directionMap.get(Direction.NORTH));
    adjacentCards.add(directionMap.get(Direction.SOUTH));
    adjacentCards.add(directionMap.get(Direction.EAST));
    adjacentCards.add(directionMap.get(Direction.WEST));

    return adjacentCards;
  }

  /**
   * Method is only public so it can be used in tests.
   * Helper method to determine direction from source to target coordinate.
   * Returns the direction that the source card is attacking in.
   *
   * @param source the source coordinate.
   * @param target the target coordinate.
   * @return the direction from source to target
   */
  public Direction getDirection(Coordinate source, Coordinate target) {
    int rowDiff = target.getRow() - source.getRow();
    int colDiff = target.getCol() - source.getCol();

    if (rowDiff < 0) {
      return Direction.NORTH;  // Target is above source, attack north
    } else if (rowDiff > 0) {
      return Direction.SOUTH;  // Target is below source, attack south
    } else if (colDiff < 0) {
      return Direction.WEST;   // Target is left of source, attack west
    } else if (colDiff > 0) {
      return Direction.EAST;   // Target is right of source, attack east
    }

    throw new IllegalArgumentException("Coordinates must be adjacent");
  }

  @Override
  public List<Card> getAdjacentOpponentCards(Coordinate position, PlayerColor currentPlayerColor) {
    if (position == null) {
      throw new IllegalArgumentException("Position cannot be null");
    }
    if (!grid.isValidPosition(position)) {
      throw new IllegalArgumentException("Invalid position");
    }

    List<Card> adjacentCards = getAdjacentCards(position);
    List<Card> opponentCards = new ArrayList<>();

    for (Card card : adjacentCards) {
      if (card != null && card.getOwner().getColor() == currentPlayerColor.getOpponentColor()) {
        opponentCards.add(card);
      } else {
        opponentCards.add(null);
      }
    }
    return opponentCards;
  }

  @Override
  public void flipCard(Coordinate position, Player newOwner) {
    if (position == null) {
      throw new IllegalArgumentException("Position cannot be null");
    }
    if (!grid.isValidPosition(position)) {
      throw new IllegalArgumentException("Invalid position");
    }
    if (grid.getCellState(position) != CellState.OCCUPIED) {
      throw new IllegalStateException("Position has no card");
    }

    Card card = getCardAt(position);
    if (card == null) {
      throw new IllegalStateException("Card cannot be found at position");
    }

    if (newOwner == card.getOwner()) {
      throw new IllegalStateException("Cannot flip card to same owner");
    } else {
      card.setOwner(newOwner);
    }
  }

  @Override
  public boolean isFull() {
    return cardPositions.size() == grid.getCardCellCount();
  }

  @Override
  public int getCardCount(PlayerColor player) {
    return (int) cardPositions.values().stream()
        .filter(card -> card.getOwner().getColor() == player)
        .count();
  }

  @Override
  public List<Coordinate> getEmptyCardCells() {
    List<Coordinate> emptyCells = new ArrayList<>();
    for (int row = 0; row < grid.getTotalRows(); row++) {
      for (int col = 0; col < grid.getTotalColumns(); col++) {
        Coordinate pos = new GameCoordinate(row, col);
        if (canPlaceCard(pos)) {
          emptyCells.add(pos);
        }
      }
    }
    return emptyCells;
  }

  @Override
  public Map<Coordinate, Card> getAllCards() {
    return new HashMap<>(cardPositions);
  }

  @Override
  public boolean isCardWinningBattle(Card attacker, Card defender, Direction direction) {
    if (attacker == null || defender == null || direction == null) {
      throw new IllegalArgumentException("Arguments cannot be null");
    }

    int attackValue = attacker.getValue(direction);
    int defendValue = defender.getValue(direction.getOpposite());

    return attackValue > defendValue;
  }

  @Override
  public void clear() {
    cardPositions.clear();
    // Reset all occupied cells to available
    for (int row = 0; row < grid.getTotalRows(); row++) {
      for (int col = 0; col < grid.getTotalColumns(); col++) {
        Coordinate pos = new GameCoordinate(row, col);
        if (grid.getCellState(pos) == CellState.OCCUPIED) {
          grid.setCellState(pos, CellState.AVAILABLE);
        }
      }
    }
  }

  @Override
  public Board copy() {
    GameBoard copy = new GameBoard(grid.copy());
    // Deep copy the card positions
    for (Map.Entry<Coordinate, Card> entry : cardPositions.entrySet()) {
      GameCoordinate newCoord = new GameCoordinate(
              entry.getKey().getRow(),
              entry.getKey().getCol());
      copy.cardPositions.put(newCoord, entry.getValue());
      copy.grid.setCellState(newCoord, CellState.OCCUPIED);
    }
    return copy;
  }
}
