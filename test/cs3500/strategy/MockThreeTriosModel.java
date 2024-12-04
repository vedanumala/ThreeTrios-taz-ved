package cs3500.strategy;

import cs3500.model.Board;
import cs3500.model.Card;
import cs3500.model.CellState;
import cs3500.model.Coordinate;
import cs3500.model.Direction;
import cs3500.model.GameCoordinate;
import cs3500.model.GameState;
import cs3500.model.Grid;
import cs3500.model.Player;
import cs3500.model.PlayerColor;
import cs3500.model.ReadOnlyThreeTriosModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock model for testing strategies with transcript recording.
 */
public class MockThreeTriosModel implements ReadOnlyThreeTriosModel {
  private final List<Coordinate> inspectedPositions;
  private final StringBuilder transcript;
  private final Map<Coordinate, Integer> flipsMap;
  private final Map<PlayerColor, List<Card>> playerHands;
  private final Board board;
  private GameState gameState = GameState.WAITING_FOR_MOVE;
  private PlayerColor currentPlayer;

  /**
   * Constructs a new mock model for testing.
   */
  public MockThreeTriosModel() {
    this.inspectedPositions = new ArrayList<>();
    this.transcript = new StringBuilder();
    this.flipsMap = new HashMap<>();
    this.playerHands = new HashMap<>();
    playerHands.put(PlayerColor.RED, new ArrayList<>());
    playerHands.put(PlayerColor.BLUE, new ArrayList<>());
    this.board = new MockBoard();
    this.gameState = GameState.WAITING_FOR_MOVE;
    this.currentPlayer = PlayerColor.RED;
  }


  /**
   * Helper method to simulate game state changes.
   */
  public void setGameState(GameState state) {
    this.gameState = state;
  }

  /**
   * Helper method to simulate turn changes.
   */
  public void setCurrentPlayer(PlayerColor player) {
    this.currentPlayer = player;
    transcript.append("Current player changed to: ").append(player).append("\n");
  }

  @Override
  public Board getBoard() {
    transcript.append("Checking board state\n");
    return board;
  }

  @Override
  public List<Card> getPlayerHand(PlayerColor player) {
    transcript.append("Getting hand for player: ").append(player).append("\n");
    return new ArrayList<>(playerHands.get(player)); // Return copy of list
  }

  @Override
  public PlayerColor getCurrentPlayerColor() {
    return PlayerColor.RED;
  }

  @Override
  public int getPotentialFlips(Card card, Coordinate position) {
    transcript.append("Checking flips for ")
            .append(card.getIdentifier())
            .append(" at position ")
            .append(position)
            .append("\n");
    return flipsMap.getOrDefault(position, 0);
  }

  @Override
  public GameState getGameState() {
    return gameState;
  }

  @Override
  public int getScore(PlayerColor player) {
    return 0;
  }

  @Override
  public PlayerColor getWinner() {
    return null;
  }

  /**
   * Gets the list of positions that were inspected by the strategy.
   *
   * @return list of inspected positions
   */
  public List<Coordinate> getInspectedPositions() {
    return new ArrayList<>(inspectedPositions);
  }

  /**
   * Gets the recorded transcript of operations.
   *
   * @return string containing operation transcript
   */
  public String getTranscript() {
    return transcript.toString();
  }

  /**
   * Sets the number of flips that would result from a move at the given position.
   *
   * @param pos position to set flips for
   * @param flips number of flips to set
   */
  public void setFlipsForPosition(Coordinate pos, int flips) {
    flipsMap.put(pos, flips);
  }

  /**
   * Adds a card to the specified player's hand.
   *
   * @param player player to add card to
   * @param card card to add
   */
  public void addCardToHand(PlayerColor player, Card card) {
    playerHands.get(player).add(card);
  }

  /**
   * Mock implementation of the Board interface for testing.
   */
  private class MockBoard implements Board {
    private final MockGrid grid;

    private MockBoard() {
      this.grid = new MockGrid();
    }

    @Override
    public Grid getGrid() {
      return grid;
    }

    @Override
    public boolean canPlaceCard(Coordinate position) {
      return position != null
              && grid.isValidPosition(position)
              && grid.getCellState(position) == CellState.AVAILABLE;
    }

    @Override
    public void placeCard(Card card, Coordinate position) {
      // Not needed for mock
    }

    @Override
    public Card getCardAt(Coordinate position) {
      return null; // Empty board for testing
    }

    @Override
    public List<Card> getAdjacentCards(Coordinate position) {
      return new ArrayList<>();
      // Empty for testing
    }

    @Override
    public List<Card> getAdjacentOpponentCards(Coordinate position, PlayerColor currentPlayer) {
      return new ArrayList<>(); // Empty for testing
    }

    @Override
    public void flipCard(Coordinate position, Player newOwner) {
      // Not needed for mock
    }

    @Override
    public boolean isFull() {
      return false;
    }

    @Override
    public int getCardCount(PlayerColor player) {
      return 0;
    }

    @Override
    public List<Coordinate> getEmptyCardCells() {
      List<Coordinate> emptyCells = new ArrayList<>();
      for (int row = 0; row < grid.getTotalRows(); row++) {
        for (int col = 0; col < grid.getTotalColumns(); col++) {
          emptyCells.add(new GameCoordinate(row, col));
        }
      }
      return emptyCells;
    }

    @Override
    public Map<Coordinate, Card> getAllCards() {
      return new HashMap<>();
      // Empty board for testing
    }

    @Override
    public boolean isCardWinningBattle(Card attacker, Card defender, Direction direction) {
      return false; // Not needed for testing
    }

    @Override
    public void clear() {
      // Not needed for mock
    }

    @Override
    public Board copy() {
      return this; // Return self for testing
    }
  }

  /**
   * Mock implementation of the Grid interface for testing.
   */
  private class MockGrid implements Grid {
    @Override
    public int getTotalRows() {
      return 3;
    }

    @Override
    public int getTotalColumns() {
      return 3;
    }

    @Override
    public CellState getCellState(Coordinate position) {
      return CellState.AVAILABLE;
    }

    @Override
    public void setCellState(Coordinate position, CellState state) {
      // Not needed for mock
    }

    @Override
    public boolean isValidPosition(Coordinate position) {
      return position != null
              && position.getRow() >= 0
              && position.getRow() < getTotalRows()
              && position.getCol() >= 0
              && position.getCol() < getTotalColumns();
    }

    @Override
    public List<Coordinate> getAdjacentPositions(Coordinate position) {
      List<Coordinate> adjacent = new ArrayList<>();
      if (isValidPosition(position)) {
        int row = position.getRow();
        int col = position.getCol();

        // Add all possible adjacent positions
        if (row > 0) {
          adjacent.add(new GameCoordinate(row - 1, col));
        }
        if (row < getTotalRows() - 1) {
          adjacent.add(new GameCoordinate(row + 1, col));
        }
        if (col > 0) {
          adjacent.add(new GameCoordinate(row, col - 1));
        }
        if (col < getTotalColumns() - 1) {
          adjacent.add(new GameCoordinate(row, col + 1));
        }
      }
      return adjacent;
    }

    @Override
    public int getCardCellCount() {
      return getTotalRows() * getTotalColumns();
      // All cells available for testing
    }

    @Override
    public Grid copy() {
      return this; // Return self for testing
    }
  }


}