package cs3500.view;

import cs3500.controller.Features;
import cs3500.model.Card;
import cs3500.model.CellState;
import cs3500.model.Coordinate;
import cs3500.model.Direction;
import cs3500.model.GameCoordinate;
import cs3500.model.GameState;
import cs3500.model.Grid;
import cs3500.model.PlayerColor;
import cs3500.model.ReadOnlyThreeTriosModel;
import cs3500.model.ThreeTriosModel;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 * Represents a panel for displaying the game grid in the Three Trios game.
 */
public class GridPanel extends JPanel implements ThreeTriosPanel {
  private final ReadOnlyThreeTriosModel model;
  private final HandPanel redHandPanel;
  private final HandPanel blueHandPanel;
  private Features features;

  /**
   * Constructs a new GridPanel object.
   *
   * @param model the model to use
   * @param redHandPanel the red player's hand panel
   * @param blueHandPanel the blue player's hand panel
   */
  public GridPanel(ReadOnlyThreeTriosModel model, HandPanel redHandPanel, HandPanel blueHandPanel) {
    this.model = model;
    this.redHandPanel = redHandPanel;
    this.blueHandPanel = blueHandPanel;

    // Add right-click support
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (model.getGameState() != GameState.GAME_OVER) {
          if (e.getButton() == MouseEvent.BUTTON3) { // Right click
            PlayerColor currentPlayer = model.getCurrentPlayerColor();
            if (features != null) {
              features.handleCancelSelection(currentPlayer);
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
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          PlayerColor currentPlayer = model.getCurrentPlayerColor();
          if (features != null) {
            features.handleCancelSelection(currentPlayer);
          }
        }
      }
    });
  }

  public void setFeatures(Features features) {
    this.features = features;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Grid grid = model.getBoard().getGrid();
    int totalColumns = grid.getTotalColumns();
    int totalRows = grid.getTotalRows();

    if (totalColumns == 0 || totalRows == 0) {
      return;
    }

    int cellWidth = getWidth() / totalColumns;
    int cellHeight = getHeight() / totalRows;

    // Draw grid cells
    drawGrid(g2d, grid, totalRows, totalColumns, cellWidth, cellHeight);

    // Draw game over overlay if needed
    if (model.getGameState() == GameState.GAME_OVER) {
      drawGameOverOverlay(g2d);
    }
  }

  private void drawGrid(Graphics2D g2d, Grid grid, int totalRows, int totalColumns,
                        int cellWidth, int cellHeight) {
    for (int row = 0; row < totalRows; row++) {
      for (int col = 0; col < totalColumns; col++) {
        Coordinate pos = new GameCoordinate(row, col);
        Rectangle cellBounds = new Rectangle(
                col * cellWidth, row * cellHeight, cellWidth, cellHeight);

        // Fill cell based on state
        CellState state = grid.getCellState(pos);
        if (state == CellState.HOLE) {
          g2d.setColor(new Color(128, 128, 0)); // Olive green
        } else {
          g2d.setColor(Color.YELLOW);
        }
        g2d.fill(cellBounds);

        // Draw card if present
        if (state == CellState.OCCUPIED) {
          Card card = model.getBoard().getCardAt(pos);
          drawCard(g2d, card, cellBounds);
        }

        // Draw cell border
        g2d.setColor(Color.BLACK);
        g2d.draw(cellBounds);
      }
    }
  }

  private void drawCard(Graphics2D g2d, Card card, Rectangle bounds) {
    // Fill card background based on owner
    g2d.setColor(card.getOwner().getColor() == PlayerColor.RED
            ? new Color(255, 200, 200) : new Color(200, 200, 255));
    g2d.fill(bounds);

    // Draw card values
    g2d.setColor(Color.BLACK);
    g2d.setFont(new Font("Arial", Font.BOLD, 16));

    int centerX = bounds.x + bounds.width / 2;
    int centerY = bounds.y + bounds.height / 2;

    drawDirectionalValues(g2d, card, bounds, centerX, centerY);
    drawCardIdentifier(g2d, card, centerX, centerY);
  }

  private void drawDirectionalValues(Graphics2D g2d, Card card, Rectangle bounds,
                                     int centerX, int centerY) {
    String[] values = {
            String.valueOf(card.getValue(Direction.NORTH)),
            String.valueOf(card.getValue(Direction.EAST)),
            String.valueOf(card.getValue(Direction.SOUTH)),
            String.valueOf(card.getValue(Direction.WEST))
    };

    Point[] positions = {
      new Point(centerX, bounds.y + 25),
      new Point(centerX + bounds.width / 3, centerY),
      new Point(centerX, bounds.y + bounds.height - 25),
      new Point(centerX - bounds.width / 3, centerY)
    };

    FontMetrics fm = g2d.getFontMetrics();
    for (int i = 0; i < 4; i++) {
      String value = values[i];
      int textWidth = fm.stringWidth(value);
      g2d.drawString(value,
              positions[i].x - textWidth / 2,
              positions[i].y + fm.getAscent() / 2);
    }
  }

  private void drawCardIdentifier(Graphics2D g2d, Card card, int centerX, int centerY) {
    g2d.setFont(new Font("Arial", Font.BOLD, 12));
    FontMetrics fm = g2d.getFontMetrics();
    String name = card.getIdentifier();
    int nameWidth = fm.stringWidth(name);
    g2d.drawString(name, centerX - nameWidth / 2, centerY + fm.getAscent());
  }

  private void drawGameOverOverlay(Graphics2D g2d) {
    // Draw semi-transparent overlay
    g2d.setColor(new Color(0, 0, 0, 150));
    g2d.fillRect(0, 0, getWidth(), getHeight());

    // Draw game over text
    g2d.setFont(new Font("Arial", Font.BOLD, 36));
    PlayerColor winner = model.getWinner();
    String gameOverText = winner == null ? "Game Over - It's a tie!"
            : "Game Over - " + winner + " Wins!";
    String scoreText = String.format("Final Score - Red: %d  Blue: %d",
            model.getScore(PlayerColor.RED),
            model.getScore(PlayerColor.BLUE));

    // Center and draw the text
    FontMetrics fm = g2d.getFontMetrics();
    int gameOverX = (getWidth() - fm.stringWidth(gameOverText)) / 2;
    int gameOverY = getHeight() / 2;

    // Draw text with shadow effect
    g2d.setColor(Color.BLACK);
    g2d.drawString(gameOverText, gameOverX + 2, gameOverY + 2);
    g2d.drawString(scoreText, gameOverX + 2, gameOverY + fm.getHeight() + 2);

    g2d.setColor(Color.WHITE);
    g2d.drawString(gameOverText, gameOverX, gameOverY);
    g2d.drawString(scoreText, gameOverX, gameOverY + fm.getHeight());
  }

  private void handleMouseClick(MouseEvent e) {
    if (!(model instanceof ThreeTriosModel)) {
      return;  // Can't make moves if not mutable model
    }

    Grid grid = model.getBoard().getGrid();
    int cellWidth = getWidth() / grid.getTotalColumns();
    int cellHeight = getHeight() / grid.getTotalRows();

    int row = e.getY() / cellHeight;
    int col = e.getX() / cellWidth;
    Coordinate clickedPos = new GameCoordinate(row, col);

    HandPanel currentHandPanel = model.getCurrentPlayerColor() == PlayerColor.RED
            ? redHandPanel : blueHandPanel;

    if (currentHandPanel != null && currentHandPanel.getSelectedCardIndex() != -1) {
      try {
        Card selectedCard = model.getPlayerHand(model.getCurrentPlayerColor())
                .get(currentHandPanel.getSelectedCardIndex());

        if (model.getBoard().canPlaceCard(clickedPos)) {
          ((ThreeTriosModel) model).playCard(selectedCard, clickedPos);
          currentHandPanel.setSelectedCard(-1);
          refresh();
          redHandPanel.refresh();
          blueHandPanel.refresh();
        }
      } catch (Exception ex) {
        System.err.println("Failed to play card: " + ex.getMessage());
      }
    }
  }

  @Override
  public void refresh() {
    repaint();
  }

  @Override
  public void setSelectedCard(int index) {
    // Not needed for grid panel
  }
}