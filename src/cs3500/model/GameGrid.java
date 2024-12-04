package cs3500.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the game grid structure and layout.
 * Handles structural concerns like dimensions, holes, and valid positions.
 */
public class GameGrid implements Grid {
  private final Cell[][] grid;
  private final int rows;
  private final int cols;
  private int cardCellCount;


  /**
   * Constructor for the GameGrid class.
   *
   * @param rows the number of rows in the grid
   * @param cols the number of columns in the grid
   */
  public GameGrid(int rows, int cols) {
    if (rows <= 0 || cols <= 0) {
      throw new IllegalArgumentException("Grid dimensions must be positive");
    }
    if (rows % 2 == 0 || cols % 2 == 0) {
      throw new IllegalArgumentException("Grid dimensions must be odd");
    }

    this.rows = rows;
    this.cols = cols;
    this.grid = new Cell[rows][cols];
    this.cardCellCount = (rows * cols);

    // Initialize the grid with available cells
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        this.grid[i][j] = new GameCell(CellState.AVAILABLE, new GameCoordinate(i, j));
      }
    }
  }

  @Override
  public int getTotalRows() {
    return this.rows;
  }

  @Override
  public int getTotalColumns() {
    return this.cols;
  }

  @Override
  public CellState getCellState(Coordinate coordinate) {
    return grid[coordinate.getRow()][coordinate.getCol()].getState();
  }

  @Override
  public void setCellState(Coordinate coordinate, CellState state) {
    if (!isValidPosition(coordinate)) {
      throw new IllegalArgumentException("Invalid position");
    }
    CellState currentState = getCellState(coordinate);
    if (currentState != state) {
      // Update the count of card cells
      if (currentState == CellState.AVAILABLE && state == CellState.HOLE) {
        cardCellCount--;
      }

      // Update the existing cell's state
      (grid[coordinate.getRow()][coordinate.getCol()]).setState(state);
    }


  }

  @Override
  public boolean isValidPosition(Coordinate position) {
    return position.getRow() >= 0 && position.getRow() < rows
        && position.getCol() >= 0 && position.getCol() < cols;
  }

  @Override
  public List<Coordinate> getAdjacentPositions(Coordinate position) {

    List<Coordinate> adjacents = new ArrayList<>();
    int row = position.getRow();
    int col = position.getCol();

    Coordinate[] directions = {
        new GameCoordinate(row - 1, col),
        new GameCoordinate(row + 1, col),
        new GameCoordinate(row, col - 1),
        new GameCoordinate(row, col + 1)
    };

    for (Coordinate direction : directions) {
      if (isValidPosition(direction)) {
        adjacents.add(direction);
      }
    }
    return adjacents;
  }

  @Override
  public int getCardCellCount() {
    return this.cardCellCount;
  }

  @Override
  public Grid copy() {
    GameGrid copy = new GameGrid(this.rows, this.cols);
    copy.cardCellCount = this.cardCellCount;

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        copy.grid[i][j] = this.grid[i][j].copy();
      }
    }
    return copy;
  }
}
