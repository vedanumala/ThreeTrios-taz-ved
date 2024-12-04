package cs3500.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements battle phase logic for the Three Trios game.
 * Handles both initial battles and combo chains.
 */
public class GameBattleHandler implements BattleHandler {
  private final Board board;

  /**
   * Constructor for the GameBattleHandler.
   *
   * @param board the game board to handle battles for
   * @throws IllegalArgumentException if board is null
   */
  public GameBattleHandler(Board board) {
    if (board == null) {
      throw new IllegalArgumentException("Board cannot be null");
    }
    this.board = board;
  }

  @Override
  public List<Coordinate> runBattle(Coordinate playedPosition) {
    if (playedPosition == null || !board.getGrid().isValidPosition(playedPosition)) {
      throw new IllegalArgumentException("Invalid played position");
    }

    List<Coordinate> flippedPositions = new ArrayList<>();
    Card playedCard = board.getCardAt(playedPosition);

    if (playedCard == null) {
      throw new IllegalStateException("No card at played position");
    }

    List<Coordinate> adjacentPositions = board.getGrid().getAdjacentPositions(playedPosition);

    // For each adjacent position, check if there's an opponent's card to battle
    for (Coordinate adjPos : adjacentPositions) {
      // Skip if position is invalid or has no card
      if (!board.getGrid().isValidPosition(adjPos)
              || board.getGrid().getCellState(adjPos) != CellState.OCCUPIED) {
        continue;
      }

      Card adjacentCard = board.getCardAt(adjPos);
      // Only battle opponent's cards
      if (adjacentCard != null
              && adjacentCard.getOwner().getColor() != playedCard.getOwner().getColor()) {
        // Get direction from played card to adjacent card
        Direction battleDir = ((GameBoard) board).getDirection(playedPosition, adjPos);

        // The played card attacks in battleDir, the adjacent card defends with opposite direction
        if (board.isCardWinningBattle(playedCard, adjacentCard, battleDir)) {
          board.flipCard(adjPos, playedCard.getOwner());
          flippedPositions.add(adjPos);
        }
      }
    }

    return flippedPositions;
  }

  @Override
  public List<Coordinate> runComboStep(List<Coordinate> newlyFlippedPositions) {
    if (newlyFlippedPositions == null) {
      throw new IllegalArgumentException("Newly flipped positions cannot be null");
    }

    List<Coordinate> additionalFlips = new ArrayList<>();

    // Process each newly flipped card for potential combos
    for (Coordinate flippedPos : newlyFlippedPositions) {
      Card flippedCard = board.getCardAt(flippedPos);
      if (flippedCard == null) {
        continue;
      }

      List<Card> adjacentCards = board.getAdjacentCards(flippedPos);
      List<Coordinate> adjacentPositions = board.getGrid().getAdjacentPositions(flippedPos);

      // Check each adjacent position for potential flips
      for (int i = 0; i < adjacentPositions.size(); i++) {
        Coordinate adjPos = adjacentPositions.get(i);
        if (!board.getGrid().isValidPosition(adjPos)
                || board.getGrid().getCellState(adjPos) != CellState.OCCUPIED) {
          continue;
        }

        Card adjacentCard = board.getCardAt(adjPos);
        if (adjacentCard != null
                && adjacentCard.getOwner().getColor() != flippedCard.getOwner().getColor()) {
          Direction direction = ((GameBoard) board).getDirection(flippedPos, adjPos);
          if (board.isCardWinningBattle(flippedCard, adjacentCard, direction)) {
            board.flipCard(adjPos, flippedCard.getOwner());
            additionalFlips.add(adjPos);
          }
        }
      }
    }

    return additionalFlips;
  }

  @Override
  public boolean shouldFlip(Card attackingCard, Card defendingCard,
                            Coordinate attackPos, Coordinate defendPos) {
    if (attackingCard == null || defendingCard == null
            || attackPos == null || defendPos == null) {
      throw new IllegalArgumentException("Arguments cannot be null");
    }

    Direction direction = ((GameBoard) board).getDirection(attackPos, defendPos);
    return board.isCardWinningBattle(attackingCard, defendingCard, direction);
  }
}