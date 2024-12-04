package cs3500.strategy;

import cs3500.model.PlayerColor;
import cs3500.model.ReadOnlyThreeTriosModel;

/**
 * Interface for a game playing strategy.
 */
public interface Strategy {
  /**
   * Chooses the best move for the given player.
   *
   * @return Move to make, or null if no valid moves.
   */
  Move chooseMove(ReadOnlyThreeTriosModel model, PlayerColor player);
}