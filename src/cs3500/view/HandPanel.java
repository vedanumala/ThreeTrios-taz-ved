package cs3500.view;

import cs3500.controller.Features;
import cs3500.model.Card;
import cs3500.model.Direction;
import cs3500.model.GameState;
import cs3500.model.PlayerColor;
import cs3500.model.ReadOnlyThreeTriosModel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JPanel;

/**
 * Panel for displaying a player's hand in the Three Trios game.
 */
public class HandPanel extends JPanel implements ThreeTriosPanel {
  private static final int MARGIN = 10;
  private static final int MAX_CARD_HEIGHT = 120;
  private static final int STROKE_WIDTH = 3;

  private final ReadOnlyThreeTriosModel model;
  private final PlayerColor player;
  private Features features;
  private int selectedIndex;

  /**
   * Creates a new hand panel for the specified player.
   *
   * @param model the game model
   * @param player the player whose hand to display
   */
  public HandPanel(ReadOnlyThreeTriosModel model, PlayerColor player) {
    if (model == null || player == null) {
      throw new IllegalArgumentException("Model and player cannot be null");
    }

    this.model = model;
    this.player = player;
    this.selectedIndex = -1;

    setBackground(getPlayerColor(player));
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (model.getGameState() != GameState.GAME_OVER
                && model.getCurrentPlayerColor() == player) {
          if (e.getButton() == MouseEvent.BUTTON3) { // Right click
            if (features != null) {
              features.handleCancelSelection(player);
            }
          } else if (e.getButton() == MouseEvent.BUTTON1) { // Left click
            handleMouseClick(e);
          }
        }
      }
    });

    // Add key listener support
    setFocusable(true);
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE
                && model.getCurrentPlayerColor() == player) {
          if (features != null) {
            features.handleCancelSelection(player);
          }
        }
      }
    });
  }

  /**
   * Sets the features listener for handling player actions.
   *
   * @param features the features listener to set
   */
  public void setFeatures(Features features) {
    this.features = features;
    // Add right-click handler
    this.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) { // Right click
          if (features != null) {
            features.handleCancelSelection(player);
            clearSelection();  // Clear the visual selection
          }
        }
      }
    });
  }

  /**
   * Clears the current card selection.
   */
  public void clearSelection() {
    selectedIndex = -1;
    refresh();
  }

  private Color getPlayerColor(PlayerColor player) {
    return player == PlayerColor.RED
            ? new Color(255, 220, 220)
            : new Color(220, 220, 255);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(150, 600);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    List<Card> hand = model.getPlayerHand(player);

    if (hand.isEmpty()) {
      drawEmptyHand(g2d);
      return;
    }

    drawHand(g2d, hand);

    if (model.getGameState() == GameState.GAME_OVER) {
      drawGameOverOverlay(g2d);
    }
  }

  private void drawEmptyHand(Graphics2D g2d) {
    g2d.setColor(Color.BLACK);
    g2d.setFont(new Font("Arial", Font.BOLD, 14));
    g2d.drawString("No cards", MARGIN, 30);
  }

  private void drawHand(Graphics2D g2d, List<Card> hand) {
    int cardHeight = Math.min(MAX_CARD_HEIGHT, (getHeight() - MARGIN) / hand.size() - MARGIN);

    for (int i = 0; i < hand.size(); i++) {
      Card card = hand.get(i);
      Rectangle cardBounds = new Rectangle(
              MARGIN,
              i * (cardHeight + MARGIN) + MARGIN,
              getWidth() - 2 * MARGIN,
              cardHeight);

      drawCard(g2d, card, cardBounds, i == selectedIndex);
    }
  }

  private void drawCard(Graphics2D g2d, Card card, Rectangle bounds, boolean isSelected) {
    // Card background
    g2d.setColor(getCardBackgroundColor());
    g2d.fill(bounds);

    // Selection highlight
    if (isSelected && model.getCurrentPlayerColor() == player) {
      g2d.setColor(Color.YELLOW);
      g2d.setStroke(new java.awt.BasicStroke(STROKE_WIDTH));
      g2d.draw(bounds);
    }

    // Card border
    g2d.setColor(Color.BLACK);
    g2d.setStroke(new java.awt.BasicStroke(1));
    g2d.draw(bounds);

    // Card contents
    drawCardContents(g2d, card, bounds);
  }

  private Color getCardBackgroundColor() {
    return player == PlayerColor.RED
            ? new Color(255, 150, 150)
            : new Color(150, 150, 255);
  }

  private void drawCardContents(Graphics2D g2d, Card card, Rectangle bounds) {
    g2d.setColor(Color.BLACK);

    // Draw values
    g2d.setFont(new Font("Arial", Font.BOLD, 16));
    int centerX = bounds.x + bounds.width / 2;
    int centerY = bounds.y + bounds.height / 2;

    drawValue(g2d, card.getValue(Direction.NORTH), centerX, bounds.y + 20);
    drawValue(g2d, card.getValue(Direction.EAST), centerX + 25, centerY);
    drawValue(g2d, card.getValue(Direction.SOUTH), centerX, bounds.y + bounds.height - 20);
    drawValue(g2d, card.getValue(Direction.WEST), centerX - 25, centerY);

    // Draw card name
    g2d.setFont(new Font("Arial", Font.BOLD, 12));
    g2d.drawString(card.getIdentifier(), bounds.x + 5, bounds.y + bounds.height - 5);
  }

  private void drawValue(Graphics2D g2d, int value, int x, int y) {
    String text = String.valueOf(value);
    FontMetrics fm = g2d.getFontMetrics();
    g2d.drawString(text, x - fm.stringWidth(text) / 2, y);
  }

  private void drawGameOverOverlay(Graphics2D g2d) {
    // Draw semi-transparent overlay
    g2d.setColor(new Color(0, 0, 0, 100));
    g2d.fillRect(0, 0, getWidth(), getHeight());

    // Draw final score
    g2d.setFont(new Font("Arial", Font.BOLD, 18));
    String scoreText = String.format("%s Score: %d",
            player, model.getScore(player));

    FontMetrics fm = g2d.getFontMetrics();
    int textX = (getWidth() - fm.stringWidth(scoreText)) / 2;
    int textY = getHeight() - 20;

    // Draw text with shadow effect
    g2d.setColor(Color.BLACK);
    g2d.drawString(scoreText, textX + 1, textY + 1);
    g2d.setColor(Color.WHITE);
    g2d.drawString(scoreText, textX, textY);
  }

  private void handleMouseClick(MouseEvent e) {
    List<Card> hand = model.getPlayerHand(player);
    if (hand.isEmpty()) {
      return;
    }

    int cardHeight = Math.min(MAX_CARD_HEIGHT, (getHeight() - MARGIN) / hand.size() - MARGIN);
    int clickedIndex = (e.getY() - MARGIN) / (cardHeight + MARGIN);

    if (clickedIndex >= 0 && clickedIndex < hand.size()) {
      selectedIndex = (clickedIndex == selectedIndex) ? -1 : clickedIndex;
      repaint();
    }
  }

  @Override
  public void refresh() {
    if (model.getCurrentPlayerColor() != player) {
      // Clear selection if it's not this player's turn
      selectedIndex = -1;
    }
    repaint();
  }

  @Override
  public void setSelectedCard(int index) {
    this.selectedIndex = index;
    refresh();  // Important: Refresh after selection change
  }

  /**
   * Gets the index of the currently selected card.
   *
   * @return the selected card index, or -1 if no card is selected
   */
  public int getSelectedCardIndex() {
    return selectedIndex;
  }


}