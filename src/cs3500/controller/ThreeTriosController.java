package cs3500.controller;

import cs3500.model.PlayerColor;

/**
 * Interface for a game controller in Three Trios.
 * Defines the contract for controlling a player's interactions with the game,
 * whether they are human or machine players.
 */
public interface ThreeTriosController {

  /**
   * Starts controlling the game for this player.
   * This includes setting up listeners and initializing the view.
   */
  void start();

  /**
   * Gets the color of the player this controller is managing.
   *
   * @return the player color
   */
  PlayerColor getPlayer();

  /**
   * Checks if this controller is managing an AI player.
   *
   * @return true if this is an AI player, false if human
   */
  boolean isAIPlayer();

  /**
   * Updates the display to reflect the current game state.
   * This should be called whenever the game state changes.
   */
  void refresh();
}