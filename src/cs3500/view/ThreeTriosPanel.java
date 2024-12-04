package cs3500.view;

/**
 * Interface for a panel in the Three Trios game view.
 */
public interface ThreeTriosPanel {

  /**
   * Refreshes the panel.
   */
  void refresh();

  /**
   * Sets the selected card index.
   */
  void setSelectedCard(int index);
}