package cs3500.threetrios;

import cs3500.controller.ThreeTriosController;
import cs3500.model.PlayerColor;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry to manage and coordinate game controllers.
 * This allows controllers to find each other for AI coordination.
 */
public class GameControllerRegistry {
  private static final Map<PlayerColor, ThreeTriosController> controllers = new HashMap<>();
  private static boolean isInitialized = false;

  /**
   * Private constructor to prevent instantiation.
   */
  private GameControllerRegistry() {
  }

  /**
   * Registers a controller for a specific player color.
   *
   * @param color      the player color
   * @param controller the controller to register
   * @throws IllegalArgumentException if color or controller is null
   * @throws IllegalStateException    if a controller is already registered for this color
   */
  public static void register(PlayerColor color, ThreeTriosController controller) {
    if (!isInitialized) {
      throw new IllegalStateException("Registry must be initialized before use");
    }
    if (color == null || controller == null) {
      throw new IllegalArgumentException("Color and controller cannot be null");
    }
    if (controllers.containsKey(color)) {
      throw new IllegalStateException("Controller already registered for " + color);
    }
    controllers.put(color, controller);
  }

  /**
   * Gets the controller for a specific player color.
   *
   * @param color the player color
   * @return the controller for that color, or null if not found
   * @throws IllegalArgumentException if color is null
   */
  public static ThreeTriosController getController(PlayerColor color) {
    if (!isInitialized) {
      throw new IllegalStateException("Registry must be initialized before use");
    }
    if (color == null) {
      throw new IllegalArgumentException("Color cannot be null");
    }
    return controllers.get(color);
  }


  /**
   * Initializes the registry for a new game.
   */
  public static void initialize() {
    controllers.clear();
    isInitialized = true;
  }

  /**
   * Checks if the registry has been initialized.
   *
   * @return true if initialized, false otherwise
   */
  public static boolean isInitialized() {
    return isInitialized;
  }
}