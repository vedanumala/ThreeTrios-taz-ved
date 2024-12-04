package cs3500.model;

/**
 * Represents directions of a card.
 */
public enum Direction {
  NORTH,
  SOUTH,
  EAST,
  WEST;

  /**
 * Gets the opposite direction.
 *
 * @return opposite direction
 * @throws IllegalStateException if the direction is invalid
 */
  public Direction getOpposite() {
    switch (this) {
      case NORTH:
        return SOUTH;
      case SOUTH:
        return NORTH;
      case EAST:
        return WEST;
      case WEST:
        return EAST;
      default:
        throw new IllegalStateException("Unknown direction: " + this);
    }
  }
}