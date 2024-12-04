package cs3500.model;

import java.util.List;

/**
 * Represents a player in the game.
 */
public interface Player {
  /**
   * Gets this player's color.
   *
   * @return player color
   */
  PlayerColor getColor();

  /**
   * Gets cards in player's hand.
   *
   * @return list of cards in hand
   */
  List<Card> getHand();

  /**
   * Adds a card to player's hand.
   *
   * @param card card to add
   * @throws IllegalArgumentException if card is null
   */
  void addCardToHand(Card card);

  /**
   * Removes a card from player's hand.
   *
   * @param card card to remove
   * @throws IllegalArgumentException if card is null or not in hand
   */
  void removeCardFromHand(Card card);
}