package cs3500.providersThreetrios.provider.view;

import cs3500.providersThreetrios.provider.controller.TTController;
import cs3500.providersThreetrios.provider.model.TTCard;
import cs3500.providersThreetrios.provider.model.Move;
import cs3500.providersThreetrios.provider.model.Cell;
import cs3500.providersThreetrios.provider.model.Direction;
import cs3500.providersThreetrios.provider.model.ReadOnlyTTModel;
import cs3500.providersThreetrios.provider.model.Player;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

/**
 * Implements nearly everything for the Three Trios GUI:
 * drawing the window and handling user input.
 */
public class ThreeTriosPanel extends JPanel implements ActionListener {
  ReadOnlyTTModel model;
  TTController controller;
  Player player;
  TTGUIView view;
  HashMap<Optional<Player>, Color> playerColors;
  int hue = 0;
  double gridCellLength;
  double handCellWidth;
  Optional<Integer> selectedCardIndex;

  /**
   * Creates a new ThreeTriosPanel.
   * @param model the model for the panel to draw from
   */
  public ThreeTriosPanel(ReadOnlyTTModel model, Player player, TTGUIView view) {
    this.model = Objects.requireNonNull(model);
    this.player = Objects.requireNonNull(player);
    this.view = Objects.requireNonNull(view);
    MouseEventsListener listener = new MouseEventsListener();
    this.addMouseListener(listener);
    Timer timer = new Timer(50, this);
    timer.start();

    playerColors = new HashMap<>();
    playerColors.put(Optional.empty(), Color.BLACK);
    playerColors.put(Optional.of(Player.RED), Color.getHSBColor(hue / 255f, 1f, 1f));
    playerColors.put(Optional.of(Player.BLUE), Color.BLUE);

    selectedCardIndex = Optional.empty();
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(350, 350);
  }

  /**
   * Adds the given controller to the panel.
   * @param controller  the controller to add to the view
   * @throws IllegalArgumentException if the controller is null
   * @throws IllegalStateException    if the panel already has a controller
   */
  public void setController(TTController controller) {
    if (controller == null) {
      throw new IllegalArgumentException("Controller cannot be null");
    } else if (this.controller != null) {
      throw new IllegalStateException("Controller is already set");
    }
    this.controller = controller;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g.create();

    gridCellLength = (double) getHeight() / model.getGrid().get(0).size();
    handCellWidth = (getWidth() - gridCellLength * model.getGrid().size()) / 2;
    if (handCellWidth < getWidth() * .125) {
      handCellWidth = getWidth() * .125;
      gridCellLength = (double) getWidth() * .75 / model.getGrid().size();
    }
    g2d.setColor(Color.BLACK);
    g2d.fillRect(0, 0, getWidth(), getHeight());

    g2d.setColor(playerColors.get(Optional.of(Player.RED)));
    g2d.fillRect(0, 0, (int) handCellWidth, getHeight());

    drawGrid(g2d);
    drawHand(g2d, Player.RED);
    drawHand(g2d, Player.BLUE);

    drawWinner(g2d);
  }

  private void drawGrid(Graphics2D g2d) {
    double xOffset = handCellWidth;
    double yOffset = (getHeight() - gridCellLength * model.getGrid().size()) / 2;
    ArrayList<ArrayList<Cell>> grid = model.getGrid();
    for (int row = 0; row < grid.size(); row++) {
      for (int col = 0; col < grid.get(row).size(); col++) {
        Cell cell = grid.get(row).get(col);
        Optional<TTCard> card;
        if (cell instanceof Cell) {
          if (!cell.isEmpty()) {
            card = Optional.of(cell.getCard());
          } else {
            card = Optional.empty();
          }
          drawCard(g2d, card, xOffset + col * gridCellLength,
              yOffset + row * gridCellLength, gridCellLength, gridCellLength);
        } else {
          drawHole(g2d, xOffset + col * gridCellLength,
              yOffset + row * gridCellLength, gridCellLength, gridCellLength);
        }
      }
    }
  }

  private void drawHand(Graphics2D g2d, Player player) {
    double cellHeight = (double) getHeight() / model.getHand(player).size();
    ArrayList<TTCard> hand = model.getHand(player);
    double x = (player == Player.RED) ? 0 : this.getWidth() - handCellWidth;
    for (int idx = 0; idx < hand.size(); idx++) {
      TTCard card = hand.get(idx);
      drawCard(g2d, Optional.of(card), x, idx * cellHeight, handCellWidth, cellHeight);
      if (selectedCardIndex.isPresent() && idx == selectedCardIndex.get()
          && player.equals(model.getPlayerTurn())) {
        Shape cell = new Rectangle2D.Double(x, idx * cellHeight, handCellWidth, cellHeight);
        g2d.setColor(Color.blue);
        g2d.draw(cell);
      }
    }
    if (selectedCardIndex.isPresent() && player.equals(model.getPlayerTurn())) {
      double y;
      if (selectedCardIndex.get() == 0) {
        y = 0;
      } else if (selectedCardIndex.get() == hand.size() - 1) {
        y = (selectedCardIndex.get() * cellHeight) - .2 * cellHeight;
      } else {
        y = (selectedCardIndex.get() * cellHeight) - .1 * cellHeight;
      }
      if (x != 0) {
        x -= .2 * handCellWidth;
      }
      drawCard(g2d, Optional.of(hand.get(selectedCardIndex.get())), x, y,
          1.2 * handCellWidth, 1.2 * cellHeight);
    }
  }

  private void drawCard(Graphics2D g2d, Optional<TTCard> card, double x, double y,
                        double cellWidth, double cellHeight) {
    if (card.isPresent()) {
      g2d.setColor(playerColors.get(Optional.of(card.get().getOwner())));
    } else {
      g2d.setColor(playerColors.get(Optional.empty()));
    }
    Shape cell = new Rectangle2D.Double(x, y, cellWidth, cellHeight);
    g2d.fill(cell);
    g2d.setColor(Color.WHITE);
    g2d.draw(cell);
    if (card.isPresent()) {
      g2d.setFont(Font.getFont(Font.MONOSPACED));
      int centerX = (int) x + (int) cellWidth / 2 - 2;
      int westX = centerX - (int) (3 * cellWidth / 8) + 4;
      int eastX = centerX + (int) (3 * cellWidth / 8) - 4;
      int centerY = (int) y + (int) cellHeight / 2 + 5;
      int northY = centerY - (int) (3 * cellHeight / 8) + 5;
      int southY = centerY + (int) (3 * cellHeight / 8) - 5;
      g2d.drawString(card.get().getAttack(Direction.NORTH).toString(), centerX, northY);
      g2d.drawString(card.get().getAttack(Direction.SOUTH).toString(), centerX, southY);
      g2d.drawString(card.get().getAttack(Direction.EAST).toString(), eastX, centerY);
      g2d.drawString(card.get().getAttack(Direction.WEST).toString(), westX, centerY);
    }
  }

  private void drawHole(Graphics2D g2d, double x, double y, double cellWidth, double cellHeight) {
    g2d.setColor(Color.WHITE);
    Shape cell = new Rectangle2D.Double(x, y, cellWidth, cellHeight);
    g2d.fill(cell);
  }

  private void drawWinner(Graphics2D g2d) {
    g2d.setColor(Color.WHITE);
    if (model.isGameOver()) {
      g2d.drawString("The Winner is", getWidth() / 2 - 35, getHeight() / 2 - 10);
      if (model.getWinner().isEmpty()) {
        g2d.drawString("No One!", getWidth() / 2 - 20, getHeight() / 2 + 10);
      } else {
        g2d.drawString(playerToColorName(model.getWinner().get()), getWidth() / 2 - 20,
            getHeight() / 2 + 10);
      }
    }
  }

  public String getTitle() {
    return "Current player: " + playerToColorName(this.model.getPlayerTurn());
  }

  private String playerToColorName(Player player) {
    Color color = playerColors.get(Optional.of(player));
    int red = color.getRed();
    int green = color.getGreen();
    int blue = color.getBlue();
    float hue = Color.RGBtoHSB(red, green, blue, null)[0] * 360;
    if (hue > 330 || hue < 20) {
      return "Red";
    } else if (hue < 40) {
      return "Orange";
    } else if (hue < 60) {
      return "Yellow";
    } else if (hue < 100) {
      return "Light Green";
    } else if (hue < 160) {
      return "Green";
    } else if (hue < 200) {
      return "Light Blue";
    } else if (hue < 250) {
      return "Blue";
    } else if (hue < 270) {
      return "Purple";
    } else if (hue < 330) {
      return "Pink";
    }
    throw new IllegalStateException("invalid hue");
  }

  public void startTurn() {
    controller.startTurn();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    hue = (hue + 1) % 256;
    playerColors.put(Optional.of(Player.RED), Color.getHSBColor(hue / 255f, 1f, 1f));
    playerColors.put(Optional.of(Player.BLUE),
        Color.getHSBColor(((180 + hue) % 256) / 255f, 1f, 1f));
    playerColors.put(Optional.empty(), Color.BLACK);
    view.updateTitle();
    repaint();
  }

  private class MouseEventsListener extends MouseInputAdapter {

    @Override
    public void mouseReleased(MouseEvent e) {
      Player currentPlayer = ThreeTriosPanel.this.model.getPlayerTurn();
      if (e.getButton() != MouseEvent.BUTTON1
          || !ThreeTriosPanel.this.player.equals(currentPlayer)) {
        return;
      }
      int x = e.getX();
      int y = e.getY();
      int minX;
      int maxX;
      int handCellHeight = getHeight() / model.getHand(currentPlayer).size();
      if (currentPlayer == Player.RED) {
        minX = 0;
        maxX = (int) ThreeTriosPanel.this.handCellWidth;
      } else {
        minX = ThreeTriosPanel.this.getWidth() - (int) ThreeTriosPanel.this.handCellWidth;
        maxX = ThreeTriosPanel.this.getWidth();
      }
      if (ThreeTriosPanel.this.selectedCardIndex.isEmpty()) {
        if (x < maxX && x >= minX) {
          ThreeTriosPanel.this.selectedCardIndex = Optional.of(y / handCellHeight);
        }
      } else {
        if (x < maxX && x >= minX) {
          int selectedCardIndex = y / handCellHeight;
          if (ThreeTriosPanel.this.selectedCardIndex.get() == selectedCardIndex) {
            ThreeTriosPanel.this.selectedCardIndex = Optional.empty();
          } else {
            ThreeTriosPanel.this.selectedCardIndex = Optional.of(selectedCardIndex);
          }
        } else if (x > ThreeTriosPanel.this.handCellWidth
               && x < ThreeTriosPanel.this.getWidth() - (int) ThreeTriosPanel.this.handCellWidth) {
          int selectedCardIndex = ThreeTriosPanel.this.selectedCardIndex.get();
          ThreeTriosPanel.this.selectedCardIndex = Optional.empty();
          ThreeTriosPanel.this.controller.playCard(new Move(
              (y - (int) (getHeight() - gridCellLength * model.getGrid().size()) / 2)
                  / (int) gridCellLength,
              (x - (int) ThreeTriosPanel.this.handCellWidth) / (int) gridCellLength,
              selectedCardIndex));
        }
      }
    }
  }
}