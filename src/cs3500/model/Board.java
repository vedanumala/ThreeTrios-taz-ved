package cs3500.model;

import java.util.List;
import java.util.Map;

/**
 * Manages the game board state and card interactions.
 * Delegates structural concerns to the model.Grid interface.
 */
public interface Board {
  /**
   * Gets the underlying grid structure.
   *
   * @return the grid
   */
  Grid getGrid();

  /**
   * Checks if a card can be placed at the given position.
   *
   * @param position position to check
   * @return true if cell state is AVAILABLE, false otherwise
   * @throws IllegalArgumentException if position is null
   */
  boolean canPlaceCard(Coordinate position);

  /**
   * Places a card at the specified position.
   *
   * @param card card to place
   * @param position position to place card
   * @throws IllegalArgumentException if card or position is null or if position is invalid
   * @throws IllegalStateException if position is OCCUPIED or a HOLE
   */
  void placeCard(Card card, Coordinate position);

  /**
   * Gets the card at the specified position.
   *
   * @param position position to check
   * @return card at position, or null if AVAILABLE
   * @throws IllegalArgumentException if position is null or invalid
   * @throws IllegalStateException if position is HOLE
   */
  Card getCardAt(Coordinate position);

  /**
   * Gets all cards adjacent to the specified position.
   *
   * @param position position to check
   * @return list of adjacent cards, in order of N, S, E, W
   * @throws IllegalArgumentException if position is null or invalid
     */
  List<Card> getAdjacentCards(Coordinate position);

  /**
   * Gets adjacent cards belonging to the opponent.
   *
   * @param position position to check
   * @param currentPlayer current player's color
   * @return list of adjacent opponent cards, in order of N, S, E, W
   * @throws IllegalArgumentException if position is null or invalid
   */
  List<Card> getAdjacentOpponentCards(Coordinate position, PlayerColor currentPlayer);

  /**
   * Changes ownership of card at specified position.
   *
   * @param position position of card to flip
   * @param newOwner new owner's color
   * @throws IllegalArgumentException if position is null or invalid
   * @throws IllegalStateException if position has no card
   */
  void flipCard(Coordinate position, Player newOwner);

  /**
   * Checks if board has no empty card cells.
   *
   * @return true if board is full, false otherwise
   */
  boolean isFull();

  /**
   * Gets total number of cards owned by specified player.
   *
   * @param player player color to count cards for
   * @return number of cards owned by player
   */
  int getCardCount(PlayerColor player);

  /**
   * Gets all empty card cell positions.
   *
   * @return list of empty positions
   */
  List<Coordinate> getEmptyCardCells();

  /**
   * Gets all cards and their positions on the board.
   *
   * @return map of positions to cards
   */
  Map<Coordinate, Card> getAllCards();

  /**
   * Determines if attacker wins against defender in given direction.
   *
   * @param attacker attacking card
   * @param defender defending card
   * @param direction direction of attack
   * @return true if attacker wins, false otherwise
   */
  boolean isCardWinningBattle(Card attacker, Card defender, Direction direction);

  /**
   * Removes all cards from the board.
   */
  void clear();

  /**
   * Creates a deep copy of the current board state.
   *
   * @return copy of the board
   */
  Board copy();
}