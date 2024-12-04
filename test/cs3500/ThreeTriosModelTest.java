package cs3500;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import cs3500.model.BasicThreeTriosGame;
import cs3500.model.Card;
import cs3500.model.CellState;
import cs3500.model.Coordinate;
import cs3500.model.GameCard;
import cs3500.model.GameCoordinate;
import cs3500.model.GameGrid;
import cs3500.model.GamePlayer;
import cs3500.model.GameState;
import cs3500.model.Grid;
import cs3500.model.Player;
import cs3500.model.PlayerColor;
import cs3500.model.ThreeTriosModel;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the public interface of ThreeTriosModel.
 */
public class ThreeTriosModelTest {
  private ThreeTriosModel model;
  private Grid testGrid;
  private List<Card> testCards;
  private Player redPlayer;
  private Player bluePlayer;

  /**
   * Sets up the test environment.
   */
  @Before
  public void setup() {
    model = new BasicThreeTriosGame();
    testGrid = new GameGrid(3, 3);
    redPlayer = new GamePlayer(PlayerColor.RED);
    bluePlayer = new GamePlayer(PlayerColor.BLUE);

    testCards = Arrays.asList(
            new GameCard("Dragon", redPlayer, 8, 7,
                    9, 6),
            new GameCard("Knight", redPlayer, 6, 8,
                    7, 5),
            new GameCard("Wizard", bluePlayer, 9, 5,
                    6, 7),
            new GameCard("Rogue", bluePlayer, 7, 6,
                    8, 5)
    );
  }

  @Test
  public void testInitializeGame() {
    model.initializeGame(testGrid, testCards);
    model.startGame(); // Must start game before checking state
    assertEquals(2, model.getPlayerHand(PlayerColor.RED).size());
    assertEquals(2, model.getPlayerHand(PlayerColor.BLUE).size());
    assertEquals(PlayerColor.RED, model.getCurrentPlayerColor());
    assertEquals(GameState.WAITING_FOR_MOVE, model.getGameState());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInitializeGameNullGrid() {
    model.initializeGame(null, testCards);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInitializeGameOddCards() {
    model.initializeGame(testGrid, testCards.subList(0, 3));
  }

  @Test
  public void testPlayValidCard() {
    Card redCard = new GameCard("Red", redPlayer, 5, 5,
            5, 5);
    Card blueCard = new GameCard("Blue", bluePlayer, 5, 5,
            5, 5);
    List<Card> cards = Arrays.asList(redCard, blueCard);

    model.initializeGame(new GameGrid(3, 3), cards);
    model.startGame();

    Card cardToPlay = model.getPlayerHand(PlayerColor.RED).get(0);
    Coordinate pos = new GameCoordinate(1, 1);
    model.playCard(cardToPlay, pos);

    Card placedCard = model.getBoard().getCardAt(pos);
    assertNotNull("Card should be placed on board", placedCard);
    assertEquals("Placed card should match played card", cardToPlay, placedCard);
    assertEquals("Cell should be occupied",
            CellState.OCCUPIED, model.getBoard().getGrid().getCellState(pos));
    assertEquals("Should be Blue's turn", PlayerColor.BLUE, model.getCurrentPlayerColor());
  }

  @Test(expected = IllegalStateException.class)
  public void testPlayCardToOccupiedPosition() {
    List<Card> cards = Arrays.asList(
            new GameCard("Red1", redPlayer, 5, 5,
                    5, 5),
            new GameCard("Blue1", bluePlayer, 5, 5,
                    5, 5)
    );

    model.initializeGame(new GameGrid(3, 3), cards);
    model.startGame();

    Coordinate pos = new GameCoordinate(1, 1);
    Card redCard = model.getPlayerHand(PlayerColor.RED).get(0);
    model.playCard(redCard, pos);

    Card blueCard = model.getPlayerHand(PlayerColor.BLUE).get(0);
    model.playCard(blueCard, pos); // Should throw IllegalStateException
  }

  @Test
  public void testBattleMechanics() {
    Card weakCard = new GameCard("Weak", redPlayer, 1, 1,
            1, 1);
    Card strongCard = new GameCard("Strong", bluePlayer, 9, 9,
            9, 9);
    List<Card> cards = Arrays.asList(weakCard, strongCard);

    model.initializeGame(new GameGrid(3, 3), cards);
    model.startGame();

    // Red plays weak card
    Coordinate centerPos = new GameCoordinate(1, 1);
    model.playCard(weakCard, centerPos);

    // Blue plays strong card adjacent to capture
    Coordinate adjacentPos = new GameCoordinate(1, 2);
    model.playCard(strongCard, adjacentPos);

    // Verify battle outcome
    Card centerCard = model.getBoard().getCardAt(centerPos);
    assertEquals("Weak card should be captured by Blue",
            PlayerColor.BLUE, centerCard.getOwner().getColor());
    assertEquals("Blue should have 2 cards on board",
            2, model.getBoard().getCardCount(PlayerColor.BLUE));
  }

  @Test
  public void testPotentialFlips() {
    // Set up game with specific cards for testing
    Player redPlayer = new GamePlayer(PlayerColor.RED);
    Player bluePlayer = new GamePlayer(PlayerColor.BLUE);

    Card weakCard = new GameCard("Weak", redPlayer, 1, 1, 1, 1);
    Card strongCard = new GameCard("Strong", bluePlayer, 9, 9, 9, 9);

    List<Card> cards = Arrays.asList(weakCard, strongCard);
    Grid grid = new GameGrid(3, 3);
    model.initializeGame(grid, cards);
    model.startGame();

    // Test scenario 1: Test potential flips on empty board
    assertEquals("Empty board should have no potential flips",
            0, model.getPotentialFlips(strongCard, new GameCoordinate(1, 1)));

    // Place red's weak card using the board directly
    Coordinate centerPos = new GameCoordinate(1, 1);
    model.getBoard().placeCard(weakCard, centerPos);

    // Test scenario 2: Test potential flips with cards on board
    Coordinate attackPos = new GameCoordinate(1, 2);
    assertEquals("Strong card should flip 1 card",
            1, model.getPotentialFlips(strongCard, attackPos));
  }

  @Test
  public void testGameEndScore() {
    // Set up even-strength cards
    List<Card> cards = Arrays.asList(
        new GameCard("R1", redPlayer, 5, 5, 5, 5),
        new GameCard("R2", redPlayer, 5, 5, 5, 5),
        new GameCard("B1", bluePlayer, 5, 5, 5, 5),
        new GameCard("B2", bluePlayer, 5, 5, 5, 5),
        new GameCard("R3", redPlayer, 5, 5, 5, 5),
        new GameCard("R4", redPlayer, 5, 5, 5, 5),
        new GameCard("B3", bluePlayer, 5, 5, 5, 5),
        new GameCard("B4", bluePlayer, 5, 5, 5, 5),
        new GameCard("R5", redPlayer, 5, 5, 5, 5),
        new GameCard("B5", bluePlayer, 5, 5, 5, 5)
    );

    model.initializeGame(new GameGrid(3, 3), cards);
    model.startGame();

    // Initial scores
    assertEquals("Red initial score should be "
                    + (cards.size() / 2), cards.size() / 2,
            model.getScore(PlayerColor.RED));
    assertEquals("Blue initial score should be "
                    + (cards.size() / 2), cards.size() / 2,
            model.getScore(PlayerColor.BLUE));

    // Fill board (simplified for test)
    Coordinate[] positions = {
      new GameCoordinate(0, 0),
      new GameCoordinate(0, 1),
      new GameCoordinate(0, 2),
      new GameCoordinate(1, 0),
      new GameCoordinate(1, 1),
      new GameCoordinate(1, 2),
      new GameCoordinate(2, 0),
      new GameCoordinate(2, 1),
      new GameCoordinate(2, 2)
    };

    for (int i = 0; i < positions.length && i < cards.size(); i++) {
      Card card = model.getPlayerHand(model.getCurrentPlayerColor()).get(0);
      model.playCard(card, positions[i]);
    }

    // Verify final scores
    assertTrue("Game should be finished",
            model.getGameState() == GameState.GAME_OVER);
    assertEquals("Scores should be equal",
            model.getScore(PlayerColor.RED), model.getScore(PlayerColor.BLUE));
    assertNull("Should be a tie", model.getWinner());
  }
}