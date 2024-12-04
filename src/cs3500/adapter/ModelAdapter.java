package cs3500.adapter;

import cs3500.model.Board;
import cs3500.model.Card;
import cs3500.model.CellState;
import cs3500.model.Coordinate;
import cs3500.model.GameCoordinate;
import cs3500.model.GameState;
import cs3500.model.Grid;
import cs3500.model.PlayerColor;
import cs3500.model.ReadOnlyThreeTriosModel;
import cs3500.providerstrios.provider.controller.model.Cell;
import cs3500.providerstrios.provider.controller.model.Direction;
import cs3500.providerstrios.provider.controller.model.Move;
import cs3500.providerstrios.provider.controller.model.Player;
import cs3500.providerstrios.provider.controller.model.TTCard;
import cs3500.providerstrios.provider.controller.model.TTModel;
import cs3500.providerstrios.provider.controller.view.TTGUIView;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Adapts our ThreeTriosModel to work with provider's model interfaces.
 * Handles conversion between the two different implementations' data structures and behaviors.
 */
public class ModelAdapter implements TTModel {
  private final ReadOnlyThreeTriosModel model;

  /**
   * Constructs a new ModelAdapter.
   *
   * @param model the model to adapt
   * @throws IllegalArgumentException if model is null
   */
  public ModelAdapter(ReadOnlyThreeTriosModel model) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    this.model = model;
  }

  @Override
  public void startGame(Scanner gridData, Scanner cardData, TTGUIView viewRed, TTGUIView viewBlue) {
    // No implementation needed - game is initialized elsewhere
  }

  @Override
  public boolean isGameOver() {
    return model.getGameState() == GameState.GAME_OVER;
  }

  @Override
  public Optional<Player> getWinner() {
    PlayerColor winner = model.getWinner();
    if (winner == null) {
      return Optional.empty();
    }
    return Optional.of(convertToProviderPlayer(winner));
  }

  @Override
  public ArrayList<TTCard> getHand(Player player) {
    List<Card> hand = model.getPlayerHand(convertToPlayerColor(player));
    ArrayList<TTCard> convertedHand = new ArrayList<>();
    for (Card card : hand) {
      if (card != null && card.getOwner() != null) {
        convertedHand.add(convertToProviderCard(card));
      }
    }
    return convertedHand;
  }

  @Override
  public ArrayList<ArrayList<Cell>> getGrid() {
    ArrayList<ArrayList<Cell>> grid = new ArrayList<>();
    Board board = model.getBoard();
    Grid ourGrid = board.getGrid();

    for (int i = 0; i < ourGrid.getTotalRows(); i++) {
      ArrayList<Cell> row = new ArrayList<>();
      for (int j = 0; j < ourGrid.getTotalColumns(); j++) {
        Coordinate pos = new GameCoordinate(i, j);
        Cell cell = createCell(board, pos);
        // Verify consistency before adding
        if (!cell.isEmpty() && cell.getCard() == null) {
          throw new IllegalStateException(
                  String.format("Inconsistent cell state at %d,%d: not empty but no card", i, j));
        }
        row.add(cell);
      }
      grid.add(row);
    }
    return grid;
  }

  @Override
  public Player getPlayerTurn() {
    return convertToProviderPlayer(model.getCurrentPlayerColor());
  }

  @Override
  public void playCard(Move move) {
    if (move == null) {
      throw new IllegalArgumentException("Move cannot be null");
    }

    PlayerColor currentPlayer = model.getCurrentPlayerColor();
    List<Card> hand = model.getPlayerHand(currentPlayer);

    if (move.getHandIdx() >= 0 && move.getHandIdx() < hand.size()) {
      Card cardToPlay = hand.get(move.getHandIdx());
      Coordinate pos = new GameCoordinate(move.getRow(), move.getCol());

      if (model instanceof cs3500.model.ThreeTriosModel) {
        ((cs3500.model.ThreeTriosModel) model).playCard(cardToPlay, pos);
      }
    }
  }

  @Override
  public void battleNeighbors(int row, int col) {
    // Battle logic handled by our model during playCard
  }

  @Override
  public void toggleTurn() {
    // Turn toggling handled by our model during playCard
  }

  @Override
  public int numFlippedAtGivenCoords(Move move) {
    if (move == null) {
      throw new IllegalArgumentException("Move cannot be null");
    }

    PlayerColor currentPlayer = model.getCurrentPlayerColor();
    List<Card> hand = model.getPlayerHand(currentPlayer);

    if (move.getHandIdx() >= 0 && move.getHandIdx() < hand.size()) {
      Card cardToPlay = hand.get(move.getHandIdx());
      Coordinate pos = new GameCoordinate(move.getRow(), move.getCol());
      return model.getPotentialFlips(cardToPlay, pos);
    }
    return 0;
  }

  @Override
  public TTModel copy() {
    return this;
  }

  private Cell createCell(Board board, Coordinate pos) {
    return new Cell() {
      @Override
      public boolean isEmpty() {
        try {
          CellState state = board.getGrid().getCellState(pos);
          if (state == CellState.HOLE) {
            return false; // Holes should not be considered empty
          }
          return state == CellState.AVAILABLE;
        } catch (IllegalStateException e) {
          return true; // If we can't get state, consider it empty
        }
      }

      @Override
      public boolean placeCard(TTCard card) {
        return false; // Handled by model
      }

      @Override
      public Optional<Player> getOwner() {
        try {
          // First check if it's a valid cell that could have an owner
          CellState state = board.getGrid().getCellState(pos);
          if (state != CellState.OCCUPIED) {
            return Optional.empty();
          }

          // Then get the card and check its owner
          Card card = board.getCardAt(pos);
          if (card != null && card.getOwner() != null) {
            return Optional.of(convertToProviderPlayer(card.getOwner().getColor()));
          }
        } catch (IllegalStateException e) {
          // Fall through to empty
        }
        return Optional.empty();
      }

      @Override
      public boolean battle(Cell other, Direction direction) {
        return false; // Handled by model
      }

      @Override
      public TTCard getCard() {
        try {
          // First verify cell state
          CellState state = board.getGrid().getCellState(pos);
          if (state != CellState.OCCUPIED) {
            return null;
          }

          // Get the card and ensure it has an owner
          Card card = board.getCardAt(pos);
          if (card != null && card.getOwner() != null) {
            return convertToProviderCard(card);
          }
        } catch (IllegalStateException e) {
          // Fall through to null
        }
        return null;
      }

      @Override
      public Cell copy() {
        return this;
      }
    };
  }

  private TTCard convertToProviderCard(Card card) {
    if (card == null || card.getOwner() == null) {
      return null;
    }

    return new TTCard() {
      @Override
      public Player getOwner() {
        return convertToProviderPlayer(card.getOwner().getColor());
      }

      @Override
      public void setOwner(Player owner) {
        // Not needed for view
      }

      @Override
      public boolean battle(TTCard other, Direction direction) {
        return false; // Handled by model
      }

      @Override
      public TTCard.AttackValue getAttack(Direction direction) {
        return convertToAttackValue(card.getValue(convertToOurDirection(direction)));
      }

      @Override
      public TTCard copy() {
        return this;
      }
    };
  }

  private TTCard.AttackValue convertToAttackValue(int value) {
    switch (value) {
      case 1: return TTCard.AttackValue.ONE;
      case 2: return TTCard.AttackValue.TWO;
      case 3: return TTCard.AttackValue.THREE;
      case 4: return TTCard.AttackValue.FOUR;
      case 5: return TTCard.AttackValue.FIVE;
      case 6: return TTCard.AttackValue.SIX;
      case 7: return TTCard.AttackValue.SEVEN;
      case 8: return TTCard.AttackValue.EIGHT;
      case 9: return TTCard.AttackValue.NINE;
      case 10: return TTCard.AttackValue.TEN;
      default: throw new IllegalArgumentException("Invalid value: " + value);
    }
  }

  private cs3500.model.Direction convertToOurDirection(Direction dir) {
    switch (dir) {
      case NORTH: return cs3500.model.Direction.NORTH;
      case SOUTH: return cs3500.model.Direction.SOUTH;
      case EAST: return cs3500.model.Direction.EAST;
      case WEST: return cs3500.model.Direction.WEST;
      default: throw new IllegalArgumentException("Invalid direction");
    }
  }

  private Player convertToProviderPlayer(PlayerColor color) {
    if (color == null) {
      throw new IllegalStateException("Cannot convert null PlayerColor");
    }
    return color == PlayerColor.RED ? Player.RED : Player.BLUE;
  }

  private PlayerColor convertToPlayerColor(Player player) {
    if (player == null) {
      throw new IllegalStateException("Cannot convert null Player");
    }
    return player == Player.RED ? PlayerColor.RED : PlayerColor.BLUE;
  }
}