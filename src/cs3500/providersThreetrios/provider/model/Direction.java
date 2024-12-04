package cs3500.threetrios.provider.model;

/**
 * A cardinal direction.
 */
public enum Direction {
  NORTH,
  SOUTH,
  EAST,
  WEST;

  /**
   * Flips a direction.
   * @return  the flipped direction
   */
  public Direction flip() {
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
        throw new IllegalArgumentException("Invalid direction");
    }
  }
}