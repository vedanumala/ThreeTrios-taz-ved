package cs3500.model;

/**
 * Defines the colors of the players.
 */
public enum PlayerColor {
  RED,
  BLUE;

  /**
   * Gets the opponent's color.
   *
   * @return the opponent's color
   */
  public PlayerColor getOpponentColor() {
    if (this == RED) {
      return BLUE;
    } else {
      return RED;
    }
  }


}
