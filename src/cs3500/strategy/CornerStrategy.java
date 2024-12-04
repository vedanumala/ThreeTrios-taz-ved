package cs3500.strategy;

import cs3500.model.Card;
import cs3500.model.Coordinate;
import cs3500.model.Direction;
import cs3500.model.PlayerColor;
import cs3500.model.ReadOnlyThreeTriosModel;

/**
 * Strategy that prioritizes placing strong cards in corner positions.
 */
public class CornerStrategy extends AbstractStrategy {

  @Override
  protected int evaluateMove(ReadOnlyThreeTriosModel model,
                             PlayerColor player, Card card, Coordinate position) {
    int rows = model.getBoard().getGrid().getTotalRows();
    int cols = model.getBoard().getGrid().getTotalColumns();

    // Check if position is a corner
    boolean isCorner = (position.getRow() == 0 || position.getRow() == rows - 1)
            && (position.getCol() == 0 || position.getCol() == cols - 1);

    if (!isCorner) {
      return 0;  // Non-corner positions get lowest priority
    }

    // For corners, evaluate based on exposed values
    int score = 0;

    // Add value if edge is exposed (not protected by board edge)
    if (position.getRow() > 0) {
      score += card.getValue(Direction.NORTH);
    }
    if (position.getRow() < rows - 1) {
      score += card.getValue(Direction.SOUTH);
    }
    if (position.getCol() > 0) {
      score += card.getValue(Direction.WEST);
    }
    if (position.getCol() < cols - 1) {
      score += card.getValue(Direction.EAST);
    }

    // Corners are highest priority, so multiply score
    return score * 100;
  }
}