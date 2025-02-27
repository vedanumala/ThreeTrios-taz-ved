package cs3500.threetrios;

import cs3500.adapter.ControllerAdapter;
import cs3500.adapter.ModelAdapter;
import cs3500.adapter.ProviderViewAdapter;
import cs3500.controller.GameController;
import cs3500.model.AIThreeTriosGame;
import cs3500.model.BasicThreeTriosGame;
import cs3500.model.PlayerColor;
import cs3500.model.ThreeTriosModel;
import cs3500.providerstrios.provider.controller.model.Player;
import cs3500.providerstrios.provider.controller.view.ThreeTriosGUIView;
import cs3500.strategy.CornerStrategy;
import cs3500.strategy.MaxFlipsStrategy;
import cs3500.strategy.Strategy;
import cs3500.view.SwingThreeTriosView;
import cs3500.view.ThreeTriosView;
import java.io.File;

/**
 * Main class for the Three Trios game.
 */
public class ThreeTrios {

  private static Strategy createStrategy(String playerType) {
    if (playerType == null) {
      throw new IllegalArgumentException("Player type cannot be null");
    }

    switch (playerType.toLowerCase()) {
      case "human":
        return null;
      case "maxflips":
        return new MaxFlipsStrategy();
      case "corner":
        return new CornerStrategy();
      default:
        throw new IllegalArgumentException(
                "Invalid player type: " + playerType
                        + "\nValid types are: human, maxflips, corner");
    }
  }

  private static void printUsage() {
    System.err.println("Usage: java ThreeTrios <boardConfig> "
            + "<cardConfig> <player1Type> <player2Type>");
    System.err.println("Example: java ThreeTrios 3x3BoardNoHoles.txt "
            + "TenCardSetFor3x3Board.txt human maxflips");
    System.err.println("\nAvailable board configurations:");
    System.err.println("- 3x3BoardNoHoles.txt");
    System.err.println("- 4x4BoardNoHoles.txt");
    System.err.println("- 5x5BoardConnectedCardCells.txt");
    System.err.println("- 6x6Board.txt");
    System.err.println("\nAvailable card configurations:");
    System.err.println("- TenCardSetFor3x3Board.txt");
    System.err.println("- CompleteCardSet.txt");
    System.err.println("\nValid player types: human, maxflips, corner");
  }

  private static boolean validateConfigFile(String filename) {
    File file = new File(filename);
    if (!file.exists()) {
      System.err.println("Error: Configuration file not found: " + filename);
      return false;
    }
    if (!file.canRead()) {
      System.err.println("Error: Cannot read configuration file: " + filename);
      return false;
    }
    return true;
  }

  /**
   * Main method for the Three Trios game.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    if (args.length != 4) {
      printUsage();
      System.exit(1);
    }

    String boardConfig = args[0];
    String cardConfig = args[1];
    String player1Type = args[2];
    String player2Type = args[3];

    if (!validateConfigFile(boardConfig) || !validateConfigFile(cardConfig)) {
      printUsage();
      System.exit(1);
    }

    try {
      // Initialize registry
      GameControllerRegistry.initialize();

      // Create and initialize model
      ThreeTriosModel model;
      if (player1Type.equals("human") && player2Type.equals("human")) {
        model = new BasicThreeTriosGame();
      } else {
        AIThreeTriosGame aiModel = new AIThreeTriosGame();
        aiModel.initializePlayersWithTypes(player1Type, player2Type);
        model = aiModel;
      }

      // Initialize game with provided configurations
      try {
        model.initializeGameFromFiles(boardConfig, cardConfig);
      } catch (IllegalArgumentException e) {
        System.err.println("Error loading configuration files: " + e.getMessage());
        printUsage();
        System.exit(1);
      }

      // Create model adapter for provider's interface
      ModelAdapter modelAdapter = new ModelAdapter(model);

      // Create strategies
      Strategy redStrategy = createStrategy(player1Type);
      Strategy blueStrategy = createStrategy(player2Type);

      // Create Player 1's view (original implementation)
      ThreeTriosView player1View = new SwingThreeTriosView(model);

      // Create Player 1's controller
      GameController redController = new GameController(
              model, player1View, PlayerColor.RED, redStrategy);

      // Create controller adapter for Player 2's view
      ControllerAdapter controllerAdapter = new ControllerAdapter(
              null, model, PlayerColor.BLUE);

      // Create Player 2's provider view with the controller adapter
      ThreeTriosGUIView providerView = new ThreeTriosGUIView(
              modelAdapter,
              Player.BLUE,
              controllerAdapter);

      // Create adapter for provider's view
      ProviderViewAdapter player2ViewAdapter = new ProviderViewAdapter(
              providerView, model, PlayerColor.BLUE);

      // Create Player 2's controller
      GameController blueController = new GameController(
              model, player2ViewAdapter, PlayerColor.BLUE, blueStrategy);

      // Update the controller adapter's features
      controllerAdapter.setFeatures(blueController);

      // Register controllers
      GameControllerRegistry.register(PlayerColor.RED, redController);
      GameControllerRegistry.register(PlayerColor.BLUE, blueController);

      // Start both controllers
      redController.start();
      blueController.start();

      // Make views visible
      player1View.setVisible(true);
      player2ViewAdapter.setVisible(true);

      // Start the game
      model.startGame();

      // Print initial game configuration
      System.out.println("\nGame started with:");
      System.out.println("Board configuration: " + boardConfig);
      System.out.println("Card configuration: " + cardConfig);
      System.out.println("Red player (Player 1): " + player1Type);
      System.out.println("Blue player (Player 2): " + player2Type);

    } catch (Exception e) {
      System.err.println("Error starting game: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }
}