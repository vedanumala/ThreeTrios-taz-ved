package cs3500.strategy;

import cs3500.model.Card;
import cs3500.model.Coordinate;
import cs3500.model.PlayerColor;
import cs3500.model.ReadOnlyThreeTriosModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Base class for implementing game strategies.
 * Provides common functionality for evaluating and selecting moves.
 */
public abstract class AbstractStrategy implements Strategy {

  @Override
  public Move chooseMove(ReadOnlyThreeTriosModel model, PlayerColor player) {
    List<Move> possibleMoves = generateAllPossibleMoves(model, player);

    if (possibleMoves.isEmpty()) {
      return generateDefaultMove(model, player);
    }

    // Sort moves by:
    // 1. Strategy-specific value (highest first)
    // 2. Row position (uppermost first)
    // 3. Column position (leftmost first)
    // 4. Card index in hand (lowest first)
    return possibleMoves.stream()
          .max(Comparator
                  .<Move>comparingInt(Move::getValue)
                  .thenComparing((m1, m2) -> Integer.compare(
                        m2.getPosition().getRow(), m1.getPosition().getRow()))
                  .thenComparing((m1, m2) -> Integer.compare(
                        m2.getPosition().getCol(), m1.getPosition().getCol()))
                  .thenComparing((m1, m2) -> Integer.compare(
                        getCardIndex(model, player, m1.getCard()),
                        getCardIndex(model, player, m2.getCard()))))
            .orElse(null);
  }

  /**
   * Generates all possible moves for the given player.
   * Each move is evaluated according to the strategy's criteria.
   */
  protected List<Move> generateAllPossibleMoves(ReadOnlyThreeTriosModel model, PlayerColor player) {
    List<Move> moves = new ArrayList<>();
    List<Card> hand = model.getPlayerHand(player);
    List<Coordinate> emptyCells = model.getBoard().getEmptyCardCells();

    for (Coordinate pos : emptyCells) {
      for (Card card : hand) {
        int value = evaluateMove(model, player, card, pos);
        moves.add(new Move(card, pos, value));
      }
    }

    return moves;
  }

  /**
   * Generates a default move when no valid moves are found.
   * Uses uppermost-leftmost position and first card in hand.
   */
  protected Move generateDefaultMove(ReadOnlyThreeTriosModel model, PlayerColor player) {
    List<Coordinate> emptyCells = model.getBoard().getEmptyCardCells();
    if (emptyCells.isEmpty() || model.getPlayerHand(player).isEmpty()) {
      return null;
    }

    // Get uppermost-leftmost position
    Coordinate defaultPos = emptyCells.stream().min(Comparator.comparingInt(Coordinate::getRow)
                    .thenComparingInt(Coordinate::getCol)).orElse(null);

    // Get first card in hand
    Card defaultCard = model.getPlayerHand(player).get(0);

    return new Move(defaultCard, defaultPos, 0);
  }

  /**
   * Gets the index of a card in the player's hand.
   */
  protected int getCardIndex(ReadOnlyThreeTriosModel model, PlayerColor player, Card card) {
    List<Card> hand = model.getPlayerHand(player);
    return hand.indexOf(card);
  }

  /**
   * Evaluates the value of playing a specific card at a specific position.
   * Must be implemented by concrete strategy classes.
   */
  protected abstract int evaluateMove(ReadOnlyThreeTriosModel model,
                                      PlayerColor player, Card card, Coordinate position);
}