package cs3500.providersThreetrios.provider.view;

/**
 * Interface for a Three Trios view. Can only be shown/hidden and repainted.
 */
public interface TTGUIView {

  /**
   * Takes actions needed at the start of the player's turn.
   */
  void startTurn();

  /**
   * Updates the title of the GUI window.
   */
  void updateTitle();

  /**
   * Displays or hides the GUI window.
   * @param show  whether to display or hide the GUI
   */
  void display(boolean show);

  /**
   * Repaints the view.
   */
  void repaint();
}