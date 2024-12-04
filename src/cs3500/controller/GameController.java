package cs3500.controller;

import cs3500.model.Card;
import cs3500.model.Coordinate;
import cs3500.model.GameState;
import cs3500.model.PlayerColor;
import cs3500.model.ThreeTriosModel;
import cs3500.strategy.Move;
import cs3500.strategy.Strategy;
import cs3500.threetrios.GameControllerRegistry;
import cs3500.view.ThreeTriosView;
import javax.swing.SwingUtilities;

/**
 * Controller for managing player interactions with the game.
 */
public class GameController implements ThreeTriosController, Features {
  private final ThreeTriosModel model;
  private final ThreeTriosView view;
  private final PlayerColor playerColor;
  private final Strategy strategy;

  // Only used for human players
  private Card selectedCard;
  private Coordinate selectedPosition;

  /**
   * Creates a new game controller.
   *
   * @param model the game model
   * @param view the view for this player
   * @param playerColor the color this controller manages
   * @param strategy the strategy to use (null for human players)
   * @throws IllegalArgumentException if model, view, or playerColor is null
   */
  public GameController(ThreeTriosModel model, ThreeTriosView view,
                        PlayerColor playerColor, Strategy strategy) {
    if (model == null || view == null || playerColor == null) {
      throw new IllegalArgumentException("Required arguments cannot be null");
    }
    this.model = model;
    this.view = view;
    this.playerColor = playerColor;
    this.strategy = strategy;
  }

  @Override
  public void start() {
    view.addFeatures(this);
    updateTitle();
    checkGameState();
  }

  private void updateTitle() {
    final StringBuilder titleBuilder = new StringBuilder();
    titleBuilder.append("Three Trios - ")
            .append(playerColor)
            .append(" Player (")
            .append(isAIPlayer() ? "AI" : "Human")
            .append(") - ")
            .append(isMyTurn() ? "Your Turn" : "Waiting");

    if (selectedCard != null) {
      titleBuilder.append(" - Card Selected");
      if (selectedPosition != null) {
        titleBuilder.append(" - Position Selected");
      }
    }

    final String finalTitle = titleBuilder.toString();
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        view.updateStatus(finalTitle);
      }
    });
  }

  @Override
  public void refresh() {
    System.out.println("Debug - Refresh called for " + playerColor);
    checkGameState();
    view.refresh();
  }

  private void checkGameState() {
    GameState state = model.getGameState();
    if (state == GameState.GAME_OVER) {
      handleGameOver();
    } else if (isMyTurn()) {
      if (isAIPlayer()) {
        handleAITurn();
      } else {
        view.updateStatus("Your turn! Select a card to play.");
      }
    } else {
      view.updateStatus("Waiting for opponent's move...");
    }
  }

  private void handleGameOver() {
    PlayerColor winner = model.getWinner();
    final String message;
    if (winner == null) {
      message = "Game Over - It's a tie!";
    } else {
      message = String.format("Game Over - %s wins! (Score: %d-%d)",
              winner,
              model.getScore(PlayerColor.RED),
              model.getScore(PlayerColor.BLUE));
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        view.updateStatus(message);
        view.showError(message);
      }
    });
  }

  private void handleAITurn() {
    view.updateStatus("AI is thinking...");
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        makeAIMove();
      }
    });
  }

  private void makeAIMove() {
    if (!isMyTurn() || !isAIPlayer()) {
      return;
    }

    Move move = strategy.chooseMove(model, playerColor);
    if (move != null) {
      try {
        model.playCard(move.getCard(), move.getPosition());

        // Notify opponent's controller
        ThreeTriosController opponent = GameControllerRegistry.getController(
                playerColor.getOpponentColor());
        if (opponent != null) {
          opponent.refresh();
        }

        view.refresh();
      } catch (IllegalStateException | IllegalArgumentException e) {
        view.showError("AI Move failed: " + e.getMessage());
      }
    }
  }

  @Override
  public void handleCardSelect(PlayerColor player, Card card) {
    if (player == null || card == null) {
      throw new IllegalArgumentException("Player and card cannot be null");
    }

    if (isAIPlayer() || !isMyTurn() || player != playerColor) {
      return;
    }

    if (!model.getPlayerHand(playerColor).contains(card)) {
      view.showError("You can only select cards from your own hand!");
      return;
    }

    selectedCard = card;
    selectedPosition = null;
    view.updateStatus("Card selected: " + card.getIdentifier() + ". Choose a position.");
    updateTitle();
    view.refresh();
  }

  @Override
  public void handleGridSelect(PlayerColor player, Coordinate position) {
    if (player == null || position == null) {
      throw new IllegalArgumentException("Player and position cannot be null");
    }

    if (isAIPlayer() || !isMyTurn() || player != playerColor) {
      return;
    }

    if (selectedCard == null) {
      view.showError("Please select a card first!");
      return;
    }

    selectedPosition = position;
    handleConfirmMove(player);
  }

  @Override
  public void handleConfirmMove(PlayerColor player) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }

    if (isAIPlayer() || !isMyTurn() || player != playerColor
            || selectedCard == null || selectedPosition == null) {
      return;
    }

    try {
      model.playCard(selectedCard, selectedPosition);
      selectedCard = null;
      selectedPosition = null;

      // Notify opponent's controller
      ThreeTriosController opponent = GameControllerRegistry.getController(
              playerColor.getOpponentColor());
      if (opponent != null) {
        opponent.refresh();
      }

      view.refresh();
    } catch (IllegalStateException | IllegalArgumentException e) {
      view.showError(e.getMessage());
    }
  }

  @Override
  public void handleCancelSelection(PlayerColor player) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }

    if (player != playerColor) {
      return;
    }

    System.out.println("Debug - Cancelling selection for " + playerColor);
    selectedCard = null;
    selectedPosition = null;
    view.updateStatus(isMyTurn() ? "Selection canceled. It's your turn!"
            : "Selection canceled. Waiting for opponent.");
    updateTitle();
    view.refresh();

    // Notify opponent's controller
    ThreeTriosController opponent = GameControllerRegistry.getController(
            playerColor.getOpponentColor());
    if (opponent != null) {
      opponent.refresh();
    }
  }

  private boolean isMyTurn() {
    return model.getCurrentPlayerColor() == playerColor
            && model.getGameState() == GameState.WAITING_FOR_MOVE;
  }

  @Override
  public PlayerColor getPlayer() {
    return playerColor;
  }

  @Override
  public boolean isAIPlayer() {
    return strategy != null;
  }
}