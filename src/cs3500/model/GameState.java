package cs3500.model;

/**
 * Different states of the game during a match.
 */
public enum GameState {

  /**
   * The game is in the initialization phase, getting ready to start.
   */
  INITIALIZATION,

  /**
   * The game is waiting for the player to make a move.
   */
  WAITING_FOR_MOVE,

  /**
   * The game is in the card placement phase.
   */
  PLACING_PHASE,

  /**
   * The game is in the battle phase.
   */
  BATTLE_PHASE,

  /**
   * The game is in the combo phase.
   */
  COMBO_PHASE,

  /**
   * The game is over.
   */
  GAME_OVER;

}