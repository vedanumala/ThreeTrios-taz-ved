package cs3500.view;

import cs3500.controller.Features;

/**
 * Interface for the graphical view of the Three Trios game.
 * Provides methods for displaying the game state and handling user interaction.
 */
public interface ThreeTriosView {
  /**
   * Makes the view visible or invisible.
   *
   * @param visible true to make visible, false to hide
   */
  void setVisible(boolean visible);

  /**
   * Refreshes the view to reflect any changes in the model.
   */
  void refresh();

  /**
   * Adds a features listener to handle player actions.
   *
   * @param features the features listener to add
   * @throws IllegalArgumentException if features is null
   */
  void addFeatures(Features features);

  /**
   * Displays an error message to the user.
   *
   * @param message the error message to display
   * @throws IllegalArgumentException if message is null
   */
  void showError(String message);

  /**
   * Updates the game status message.
   *
   * @param message the status message to display
   * @throws IllegalArgumentException if message is null
   */
  void updateStatus(String message);
}