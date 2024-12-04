package cs3500.strategy;

import cs3500.model.Card;
import cs3500.model.Coordinate;
import cs3500.model.Direction;
import cs3500.model.GameState;
import cs3500.model.PlayerColor;
import cs3500.model.ReadOnlyThreeTriosModel;
import java.util.List;

/**
 * Strategy that chooses the move with the most flips.
 */
public class MaxFlipsStrategy implements Strategy {

  @Override
  public Move chooseMove(ReadOnlyThreeTriosModel model, PlayerColor player) {
    if (model.getGameState() == GameState.GAME_OVER) {
      return null;
    }
    List<Card> hand = model.getPlayerHand(player);
    List<Coordinate> emptyCells = model.getBoard().getEmptyCardCells();
    Move bestMove = null;
    int maxFlips = -1;

    for (Card card : hand) {
      for (Coordinate pos : emptyCells) {
        if (model.getBoard().canPlaceCard(pos)) {
          int flips = model.getPotentialFlips(card, pos);
          if (flips > maxFlips
                  || (flips == maxFlips
                  && shouldPreferMove(bestMove, card, pos, model, player))) {
            maxFlips = flips;
            bestMove = new Move(card, pos, flips);
          }
        }
      }
    }

    return bestMove;
  }

  private boolean shouldPreferMove(Move currentBest, Card newCard, Coordinate newPos,
                                   ReadOnlyThreeTriosModel model, PlayerColor player) {
    if (currentBest == null) {
      return true;
    }

    // Prefer higher value cards (assuming average of directional values indicates strength)
    double currentCardStrength = getCardStrength(currentBest.getCard());
    double newCardStrength = getCardStrength(newCard);
    return newCardStrength > currentCardStrength;
  }

  private double getCardStrength(Card card) {
    return (card.getValue(Direction.NORTH)
            + card.getValue(Direction.SOUTH)
            + card.getValue(Direction.EAST)
            + card.getValue(Direction.WEST)) / 4.0;
  }
}