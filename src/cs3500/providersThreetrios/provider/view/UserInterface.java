package cs3500.threetrios.provider.view;

/**
 * User interface for a Three Trios game.
 */
public interface UserInterface {

  /**
   * Plays the card from the player's hand at the index to the row and column in the grid.
   * @param cardIdx the index of the card in the player's hand to play
   * @param row     the row to play the card to
   * @param col     the column to play the card to
   * @throws IllegalArgumentException if the move is invalid
   */
  void playCard(int cardIdx, int row, int col);
}