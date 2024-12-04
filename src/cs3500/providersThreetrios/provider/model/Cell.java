package cs3500.providersThreetrios.provider.model;

import java.util.Optional;

/**
 * Cell in a ThreeTriosModel's grid.
 */
public interface Cell {

  /**
   * Whether the {@link Cell} contains a card.
   * @return  whether the {@link Cell} contains a card
   */
  boolean isEmpty();

  /**
   * Places the card in the {@link Cell}. Returns false if the Cell is a Hole.
   * @param card  the {@link TTCard} to place in the {@link Cell}
   * @return  whether the place action was successful
   * @throws IllegalArgumentException if the given card is null
   */
  boolean placeCard(TTCard card);

  /**
   * Returns the {@link Cell}'s {@link TTCard} if it has one.
   * @return  the {@link Cell}'s {@link TTCard} if it has one
   */
  Optional<Player> getOwner();

  /**
   * Performs everything necessary for one {@link Cell}'s {@link TTCard} to battle another
   * {@link Cell}'s {@link TTCard} and returns whether this {@link Cell} wins.
   *
   * @param other             the {@link TTCard} this battles against
   * @param directionToOther  the {@link Direction} this battles in
   * @return                  if this wins
   */
  boolean battle(Cell other, Direction directionToOther);

  /**
   * Returns a copy of the {@link Cell}'s {@link TTCard}.
   * @return  a copy of the {@link Cell}'s {@link TTCard}
   * @throws IllegalStateException if the {@link Cell} does not have a {@link TTCard}
   */
  TTCard getCard();

  /**
   * Creates a copy of the {@link Cell} with immutable fields.
   * @return a copy of the {@link Cell}.
   */
  Cell copy();
}