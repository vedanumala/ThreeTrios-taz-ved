package cs3500.threetrios.provider.model;

/**
 * Represents a Player in Three Trios by its color.
 */
public enum Player {
  RED,
  BLUE;

  @Override
  public String toString() {
    switch (this) {
      case RED:
        return "Red";
      case BLUE:
        return "Blue";
      default:
        throw new IllegalArgumentException("Invalid player");
    }
  }
}