package cs3500.model;

import java.util.List;

/**
 * Handles battle phase logic including combo steps.
 */
public interface BattleHandler {
  /**
   * Processes initial battle phase for newly placed card.
   *
   * @param playedPosition position of newly played card
   * @return list of positions where cards were flipped
   * @throws IllegalArgumentException if position is null or invalid
   */
  List<Coordinate> runBattle(Coordinate playedPosition);

  /**
   * Processes combo step battles for newly flipped cards.
   *
   * @param newlyFlippedPositions positions of newly flipped cards
   * @return list of additional positions where cards were flipped
   * @throws IllegalArgumentException if positions list is null
   */
  List<Coordinate> runComboStep(List<Coordinate> newlyFlippedPositions);

  /**
   * Determines if cards should flip in battle.
   *
   * @param attackingCard attacking card
   * @param defendingCard defending card
   * @param attackPos attacker position
   * @param defendPos defender position
   * @return true if defender should flip, false otherwise
   */
  boolean shouldFlip(Card attackingCard, Card defendingCard, Coordinate attackPos,
                     Coordinate defendPos);
}