package cs3500.adapter;

import cs3500.model.PlayerColor;
import cs3500.providersThreetrios.provider.model.Player;

/**
 * Utility class for converting between our PlayerColor and the provider's Player types.
 */
public final class PlayerTypeAdapter {
  private PlayerTypeAdapter() {
    // Private constructor to prevent instantiation
  }

  /**
   * Converts provider's Player type to our PlayerColor.
   *
   * @param player the provider's Player type
   * @return corresponding PlayerColor
   * @throws IllegalArgumentException if player is null
   */
  public static PlayerColor toPlayerColor(Player player) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    return player == Player.RED ? PlayerColor.RED : PlayerColor.BLUE;
  }

  /**
   * Converts our PlayerColor to provider's Player type.
   *
   * @param color our PlayerColor type
   * @return corresponding Player
   * @throws IllegalArgumentException if color is null
   */
  public static Player toPlayer(PlayerColor color) {
    if (color == null) {
      throw new IllegalArgumentException("Color cannot be null");
    }
    return color == PlayerColor.RED ? Player.RED : Player.BLUE;
  }
}