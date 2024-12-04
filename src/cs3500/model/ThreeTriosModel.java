package cs3500.model;

import java.util.List;

/**
 * Mutable interface for the Three Trios game model.
 * Extends read-only interface and adds methods for modifying game state.
 */
public interface ThreeTriosModel extends ReadOnlyThreeTriosModel {
  /**
   * Creates a new uninitialized Three Trios game model.
   *
   * @return new instance of model.ThreeTriosModel
   */
  static ThreeTriosModel create() {
    // This will be implemented by the concrete class
    throw new UnsupportedOperationException("Not implemented");
  }

  /**
   * Initializes a new game with the specified grid and cards from configuration files.
   *
   * @param gridConfig path to grid configuration file
   * @param cardsConfig path to cards configuration file
   * @throws IllegalArgumentException if either file path is null or invalid
   * @throws IllegalStateException if configurations are invalid or game already initialized
   */
  void initializeGameFromFiles(String gridConfig, String cardsConfig);

  /**
   * Initializes a new game with the specified grid and cards.
   *
   * @param grid the game grid to use
   * @param cards the list of cards to play with
   * @throws IllegalArgumentException if grid or cards are null or invalid
   * @throws IllegalStateException if game already initialized
   */
  void initializeGame(Grid grid, List<Card> cards);


  /**
   * Starts the game by selecting a random player to go first.
   *
   * @throws IllegalStateException if game is not initialized or already started
   */
  void startGame();

  /**
   * Plays the specified card at the given position for the current player.
   *
   * @param card card to play from current player's hand
   * @param position position to play card at
   * @throws IllegalArgumentException if card or position is null or invalid
   * @throws IllegalStateException if move is illegal or not current player's turn
   */
  void playCard(Card card, Coordinate position);
}