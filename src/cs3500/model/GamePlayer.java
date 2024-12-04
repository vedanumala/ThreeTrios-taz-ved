package cs3500.model;

import java.util.List;

/**
 * Represents a player in the game.
 */
public class GamePlayer implements Player {

  private final PlayerColor color;
  private final List<Card> hand;

  /**
   * Constructor for the GamePlayer class.
   * Also keeps track of the player's hand.
   *
   * @param color the color of the player.
   */
  public GamePlayer(PlayerColor color) {
    this.color = color;
    this.hand = new java.util.ArrayList<>();
  }

  @Override
  public PlayerColor getColor() {
    return this.color;
  }

  @Override
  public List<Card> getHand() {
    return this.hand;
  }

  @Override
  public void addCardToHand(Card card) {
    if (card == null) {
      throw new IllegalArgumentException("Card cannot be null");
    }
    this.hand.add(card);
  }

  @Override
  public void removeCardFromHand(Card card) {
    if (card == null || !this.hand.contains(card)) {
      throw new IllegalArgumentException("Card cannot be null or not in hand");
    }
    this.hand.remove(card);
  }

}
