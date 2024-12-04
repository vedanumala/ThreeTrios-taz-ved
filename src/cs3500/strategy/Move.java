package cs3500.strategy;

import cs3500.model.Card;
import cs3500.model.Coordinate;

/**
 * Represents a move in the Three Trios game.
 */
public class Move {
  private final Card card;
  private final Coordinate position;
  private final int value; // For comparing moves

  /**
   * Constructor for the Move class.
   *
   * @param card the card to play
   * @param position the position to play the card
   * @param value the value of the move
   */
  public Move(Card card, Coordinate position, int value) {
    this.card = card;
    this.position = position;
    this.value = value;

    if (card == null || position == null) {
      throw new IllegalArgumentException("Card and position must be non-null");
    }

    if (value < 0) {
      throw new IllegalArgumentException("Value must be non-negative");
    }
  }

  /**
   * Gets the card of the move.
   *
   * @return the card
   */
  public Card getCard() {
    return card;
  }

  /**
   * Gets the position of the move.
   *
   * @return the position
   */
  public Coordinate getPosition() {
    return position;
  }

  /**
   * Gets the value of the move.
   *
   * @return the value
   */
  public int getValue() {
    return value;
  }
}