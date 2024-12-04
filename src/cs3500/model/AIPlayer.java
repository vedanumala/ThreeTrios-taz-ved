package cs3500.model;

import cs3500.strategy.Move;
import cs3500.strategy.Strategy;
import java.util.ArrayList;
import java.util.List;

/**
 * AI implementation of Player that automatically makes moves using a strategy.
 */
public class AIPlayer implements Player {
  private final PlayerColor color;
  private final List<Card> hand;
  private final Strategy strategy;
  private final ThreeTriosModel gameModel;

  /**
   * Creates a new AI player with the specified color and strategy.
   *
   * @param color the player's color
   * @param strategy the strategy to use for moves
   * @param gameModel the game model reference for making moves
   */
  public AIPlayer(PlayerColor color, Strategy strategy, ThreeTriosModel gameModel) {
    if (color == null || strategy == null || gameModel == null) {
      throw new IllegalArgumentException("Parameters cannot be null");
    }
    this.color = color;
    this.strategy = strategy;
    this.gameModel = gameModel;
    this.hand = new ArrayList<>();
  }

  @Override
  public PlayerColor getColor() {
    return color;
  }

  @Override
  public List<Card> getHand() {
    return hand;
  }

  @Override
  public void addCardToHand(Card card) {
    if (card == null) {
      throw new IllegalArgumentException("Card cannot be null");
    }
    hand.add(card);
  }

  @Override
  public void removeCardFromHand(Card card) {
    if (card == null || !hand.contains(card)) {
      throw new IllegalArgumentException("Card cannot be null or not in hand");
    }
    hand.remove(card);
  }

  /**
   * Makes an automatic move using the AI strategy.
   *
   * @return true if a move was made successfully, false otherwise
   */
  public boolean makeMove() {
    if (gameModel.getCurrentPlayerColor() != color
            || gameModel.getGameState() != GameState.WAITING_FOR_MOVE) {
      return false;
    }

    try {
      Move move = strategy.chooseMove(gameModel, color);
      if (move != null) {
        Card selectedCard = move.getCard();
        Coordinate selectedPosition = move.getPosition();

        if (!hand.contains(selectedCard)) {
          System.err.println("AI tried to play card not in hand");
          return false;
        }

        if (!gameModel.getBoard().canPlaceCard(selectedPosition)) {
          System.err.println("AI tried to play card in invalid position");
          return false;
        }

        System.out.println("AI " + color + " playing " + selectedCard.getIdentifier()
                + " at position " + selectedPosition);

        gameModel.playCard(selectedCard, selectedPosition);
        return true;
      } else {
        System.err.println("AI strategy returned null move");
        return false;
      }
    } catch (Exception e) {
      System.err.println("AI Move failed: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }
}