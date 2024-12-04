package cs3500.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import cs3500.model.Card;
import cs3500.model.GameCard;
import cs3500.model.GameCoordinate;
import cs3500.model.GamePlayer;
import cs3500.model.GameState;
import cs3500.model.Player;
import cs3500.model.PlayerColor;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for strategy implementations.
 */
public class StrategyTest {
  private MockThreeTriosModel mockModel;
  private Card strongCard;
  private Strategy maxFlipsStrategy;

  /**
   * Sets up the test fixture.
   */
  @Before
  public void setup() {
    mockModel = new MockThreeTriosModel();
    maxFlipsStrategy = new MaxFlipsStrategy();
    Strategy cornerStrategy = new CornerStrategy();  // Initialize cornerStrategy

    // Create players
    Player redPlayer = new GamePlayer(PlayerColor.RED);

    // Create test cards with explicit values
    strongCard = new GameCard("Strong", redPlayer, 9, 9, 9, 9);
    Card weakCard = new GameCard("Weak", redPlayer, 1, 1, 1, 1);

    // Add cards to mock model's hand in specific order
    mockModel.addCardToHand(PlayerColor.RED, strongCard);
    mockModel.addCardToHand(PlayerColor.RED, weakCard);
  }


  @Test
  public void testMaxFlipsStrategyPrioritizesFlips() {
    // Configure mock with specific flip outcomes
    mockModel.setFlipsForPosition(new GameCoordinate(0, 0), 3);
    mockModel.setFlipsForPosition(new GameCoordinate(1, 1), 1);

    Move move = maxFlipsStrategy.chooseMove(mockModel, PlayerColor.RED);

    assertNotNull("Strategy should return a move", move);
    assertEquals("Should choose position with most flips",
            new GameCoordinate(0, 0), move.getPosition());
    assertEquals("Should use strong card for best position",
            strongCard, move.getCard());

    // Verify transcript shows expected evaluation order
    String transcript = mockModel.getTranscript();
    assertTrue(transcript.contains("Getting hand for player: RED"));
    assertTrue(transcript.contains("Checking flips for Strong at position (0, 0)"));
  }

  @Test
  public void testTieBreakingBehavior() {
    // Set all positions to have equal flips
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        mockModel.setFlipsForPosition(new GameCoordinate(i, j), 2);
      }
    }

    Move move = maxFlipsStrategy.chooseMove(mockModel, PlayerColor.RED);

    assertEquals("Should choose uppermost position for ties",
            0, move.getPosition().getRow());
    assertEquals("Should choose leftmost position for ties",
            0, move.getPosition().getCol());
  }

  @Test
  public void testStrategyHandlesGameOver() {
    mockModel.setGameState(GameState.GAME_OVER);
    Move move = maxFlipsStrategy.chooseMove(mockModel, PlayerColor.RED);
    assertNull("Should return null when game is over", move);
  }

  @Test
  public void testStrategyHandlesEmptyHand() {
    mockModel = new MockThreeTriosModel(); // Empty hands
    Move move = maxFlipsStrategy.chooseMove(mockModel, PlayerColor.RED);
    assertNull("Should return null when hand is empty", move);
  }

  @Test
  public void testStrategyTranscriptRecording() {
    maxFlipsStrategy.chooseMove(mockModel, PlayerColor.RED);
    String transcript = mockModel.getTranscript();

    assertTrue("Should check board state",
            transcript.contains("Checking board state"));
    assertTrue("Should get player hand",
            transcript.contains("Getting hand for player: RED"));
  }
}