package view;

/**
 * Interface for a Three Trios view. Can only be shown/hidden and repainted.
 */
public interface TTGUIView {

  void startTurn();

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