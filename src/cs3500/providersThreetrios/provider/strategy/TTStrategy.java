package cs3500.threetrios.provider.strategy;

import cs3500.threetrios.provider.model.Move;
import cs3500.threetrios.provider.model.TTModel;

/**
 * A strategy interface that chooses a move for a player based on a certain technique.
 */
public interface TTStrategy {

  /**
   * Finds the best move for the current player in the model based on a specific strategy.
   * @param model the current game being played.
   * @return the {@link Move} that executes the strategy.
   */
  Move findMove(TTModel model);

  /**
   * Generates a transcript of every coordinate on a {@link TTModel}'s grid.
   * @return a string representation of visited coordinates.
   */
  String generateTranscript();
}