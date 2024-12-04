package cs3500.adapter;

import cs3500.controller.Features;
import cs3500.model.Card;
import cs3500.model.Coordinate;
import cs3500.model.GameCoordinate;
import cs3500.model.PlayerColor;
import cs3500.model.ReadOnlyThreeTriosModel;
import cs3500.providersThreetrios.provider.controller.TTController;
import cs3500.providersThreetrios.provider.model.Move;
import java.util.List;

/**
 * Adapts our features interface to work with provider's TTController interface.
 * Handles the conversion between different control mechanisms and coordinates
 * player actions between the two implementations.
 */
public class ControllerAdapter implements TTController {
  private Features features;
  private final ReadOnlyThreeTriosModel model;
  private final PlayerColor playerColor;
  private Card selectedCard;
  private int selectedCardIndex;

  /**
   * Constructs a new controller adapter.
   * Features can be null initially and set later via setFeatures.
   *
   * @param features initial features interface, can be null
   * @param model the game model
   * @param playerColor the color this controller manages
   * @throws IllegalArgumentException if model or playerColor is null
   */
  public ControllerAdapter(Features features, ReadOnlyThreeTriosModel model,
                           PlayerColor playerColor) {
    if (model == null || playerColor == null) {
      throw new IllegalArgumentException("Model and player color cannot be null");
    }
    this.features = features;
    this.model = model;
    this.playerColor = playerColor;
    this.selectedCard = null;
    this.selectedCardIndex = -1;
  }

  /**
   * Updates the features interface after initialization.
   *
   * @param features the features to use
   * @throws IllegalArgumentException if features is null
   */
  public void setFeatures(Features features) {
    if (features == null) {
      throw new IllegalArgumentException("Features cannot be null");
    }
    this.features = features;
  }

  @Override
  public void startTurn() {
    // Clear any previous selections on turn start
    selectedCard = null;
    selectedCardIndex = -1;
  }

  @Override
  public void playCard(Move move) {
    if (move == null) {
      throw new IllegalArgumentException("Move cannot be null");
    }

    if (features == null) {
      throw new IllegalStateException("Features not initialized");
    }

    if (model.getCurrentPlayerColor() != playerColor) {
      return; // Not this player's turn
    }

    List<Card> hand = model.getPlayerHand(playerColor);

    // Handle card selection from hand
    if (selectedCard == null && move.getHandIdx() >= 0 && move.getHandIdx() < hand.size()) {
      handleCardSelection(move.getHandIdx(), hand);
      return;
    }

    // Handle grid selection when a card is already selected
    if (selectedCard != null) {
      handleGridSelection(move);
    }
  }

  /**
   * Handles the selection of a card from the player's hand.
   *
   * @param index index of the selected card
   * @param hand current player's hand
   */
  private void handleCardSelection(int index, List<Card> hand) {
    // If clicking the same card, deselect it
    if (index == selectedCardIndex) {
      features.handleCancelSelection(playerColor);
      selectedCard = null;
      selectedCardIndex = -1;
      return;
    }

    // Select the new card
    selectedCard = hand.get(index);
    selectedCardIndex = index;
    features.handleCardSelect(playerColor, selectedCard);
  }

  /**
   * Handles the selection of a position on the grid.
   *
   * @param move the move containing the grid position
   */
  private void handleGridSelection(Move move) {
    Coordinate position = new GameCoordinate(move.getRow(), move.getCol());

    if (model.getBoard().canPlaceCard(position)) {
      features.handleGridSelect(playerColor, position);
      features.handleConfirmMove(playerColor);

      // Reset selection state after move
      selectedCard = null;
      selectedCardIndex = -1;
    }
  }

  /**
   * Gets the currently selected card index.
   *
   * @return the index of the selected card, or -1 if no card is selected
   */
  public int getSelectedCardIndex() {
    return selectedCardIndex;
  }

  /**
   * Gets the currently selected card.
   *
   * @return the selected card, or null if no card is selected
   */
  public Card getSelectedCard() {
    return selectedCard;
  }

  /**
   * Cancels the current selection.
   */
  public void cancelSelection() {
    if (features != null) {
      features.handleCancelSelection(playerColor);
    }
    selectedCard = null;
    selectedCardIndex = -1;
  }
}