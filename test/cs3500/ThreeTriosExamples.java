package cs3500;

import cs3500.model.BasicThreeTriosGame;
import cs3500.model.Card;
import cs3500.model.GameCard;
import cs3500.model.GameCoordinate;
import cs3500.model.GameGrid;
import cs3500.model.GamePlayer;
import cs3500.model.Grid;
import cs3500.model.Player;
import cs3500.model.PlayerColor;
import cs3500.model.ThreeTriosModel;
import java.util.Arrays;
import java.util.List;

/**
 * Example usage of the Three Trios game model.
 * Provides sample scenarios and demonstrates key functionality.
 */
public class ThreeTriosExamples {
  private Card dragonCard;
  private Card knightCard;
  private Card wizardCard;
  private ThreeTriosModel game;
  private Grid basicGrid;

  /**
   * Sets up common test examples.
   */
  private void initializeExample() {
    Player redPlayer = new GamePlayer(PlayerColor.RED);
    Player bluePlayer = new GamePlayer(PlayerColor.BLUE);

    dragonCard = new GameCard("Dragon", redPlayer, 8, 7,
            9, 6);
    knightCard = new GameCard("Knight", redPlayer, 6, 8,
            7, 5);
    wizardCard = new GameCard("Wizard", bluePlayer, 9, 5,
            6, 7);

    game = new BasicThreeTriosGame();
    basicGrid = new GameGrid(3, 3);
  }

  /**
   * Example of basic game setup and card placement.
   */
  public void basicGameExample() {
    initializeExample();
    List<Card> cards = Arrays.asList(dragonCard, knightCard, wizardCard);
    game.initializeGame(basicGrid, cards);
    game.startGame();  // Start game before playing cards

    // Red player places Dragon card in center
    game.playCard(dragonCard, new GameCoordinate(1, 1));
    assert game.getCurrentPlayerColor() == PlayerColor.BLUE : "Turn should switch to Blue";
  }

  /**
   * Example of battle resolution and card flipping.
   */
  public void battleExample() {
    initializeExample();
    List<Card> cards = Arrays.asList(dragonCard, knightCard, wizardCard);
    game.initializeGame(basicGrid, cards);
    game.startGame();  // Start game before playing cards

    // Create and verify a battle scenario
    game.playCard(dragonCard, new GameCoordinate(1, 1)); // Red's dragon in center
    game.playCard(wizardCard, new GameCoordinate(0, 1)); // Blue's wizard above dragon

    // Verify the battle outcome
    Card flippedCard = game.getBoard().getCardAt(new GameCoordinate(0, 1));
    assert flippedCard.getOwner().getColor() == PlayerColor.RED :
            "Wizard should be flipped to Red's control";
  }
}