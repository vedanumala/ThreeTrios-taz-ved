package cs3500.view;

import cs3500.controller.Features;
import cs3500.model.Card;
import cs3500.model.Coordinate;
import cs3500.model.GameCoordinate;
import cs3500.model.GameState;
import cs3500.model.PlayerColor;
import cs3500.model.ReadOnlyThreeTriosModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Swing implementation of the Three Trios game view.
 * Displays the game board, player hands, and game status.
 */
public class SwingThreeTriosView extends JFrame implements ThreeTriosView {
  private final ReadOnlyThreeTriosModel model;
  private final HandPanel redHandPanel;
  private final HandPanel blueHandPanel;
  private final GridPanel gridPanel;
  private final JLabel statusLabel;
  private final List<Features> featuresListeners;

  /**
   * Creates a new swing view for the Three Trios game.
   *
   * @param model the game model to display
   * @throws IllegalArgumentException if model is null
   */
  public SwingThreeTriosView(ReadOnlyThreeTriosModel model) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }

    this.model = model;
    this.featuresListeners = new ArrayList<>();
    Card selectedCard = null;
    Coordinate selectedPosition = null;

    setTitle("Three Trios");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout(10, 10));

    this.statusLabel = createStatusLabel();
    this.redHandPanel = new HandPanel(model, PlayerColor.RED);
    this.blueHandPanel = new HandPanel(model, PlayerColor.BLUE);
    this.gridPanel = new GridPanel(model, redHandPanel, blueHandPanel);

    setupPanels();
    setupListeners();

    setMinimumSize(new Dimension(1000, 800));
    pack();
    setLocationRelativeTo(null);
  }

  private void setupListeners() {
    // Add mouse listeners to handle red player card selection
    redHandPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {  // Left click
          if (model.getCurrentPlayerColor() == PlayerColor.RED) {
            int index = redHandPanel.getSelectedCardIndex();
            if (index >= 0 && index < model.getPlayerHand(PlayerColor.RED).size()) {
              Card card = model.getPlayerHand(PlayerColor.RED).get(index);
              notifyCardSelect(PlayerColor.RED, card);
            }
          }
        } else if (e.getButton() == MouseEvent.BUTTON3) {  // Right click
          for (Features listener : featuresListeners) {
            listener.handleCancelSelection(PlayerColor.RED);
          }
        }
      }
    });

    // Add mouse listeners to handle blue player card selection
    blueHandPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {  // Left click
          if (model.getCurrentPlayerColor() == PlayerColor.BLUE) {
            int index = blueHandPanel.getSelectedCardIndex();
            if (index >= 0 && index < model.getPlayerHand(PlayerColor.BLUE).size()) {
              Card card = model.getPlayerHand(PlayerColor.BLUE).get(index);
              notifyCardSelect(PlayerColor.BLUE, card);
            }
          }
        } else if (e.getButton() == MouseEvent.BUTTON3) {  // Right click
          for (Features listener : featuresListeners) {
            listener.handleCancelSelection(PlayerColor.BLUE);
          }
        }
      }
    });

    // Add mouse listener to handle grid selection
    gridPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {  // Left click
          PlayerColor currentPlayer = model.getCurrentPlayerColor();
          int cellWidth = gridPanel.getWidth() / model.getBoard().getGrid().getTotalColumns();
          int cellHeight = gridPanel.getHeight() / model.getBoard().getGrid().getTotalRows();

          int row = e.getY() / cellHeight;
          int col = e.getX() / cellWidth;

          Coordinate position = new GameCoordinate(row, col);
          notifyGridSelect(currentPlayer, position);
        } else if (e.getButton() == MouseEvent.BUTTON3) {  // Right click
          for (Features listener : featuresListeners) {
            listener.handleCancelSelection(model.getCurrentPlayerColor());
          }
        }
      }
    });

    // Add keyboard listener for escape key
    this.setFocusable(true);
    this.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          for (Features listener : featuresListeners) {
            listener.handleCancelSelection(model.getCurrentPlayerColor());
          }
        }
      }
    });

    // Add global frame right-click listener
    this.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {  // Right click
          for (Features listener : featuresListeners) {
            listener.handleCancelSelection(model.getCurrentPlayerColor());
          }
        }
      }
    });

    // Ensure component focus for keyboard events
    this.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        requestFocusInWindow();
      }
    });
  }

  private void handleRedHandClick() {
    if (model.getCurrentPlayerColor() != PlayerColor.RED) {
      return;
    }
    int index = redHandPanel.getSelectedCardIndex();
    if (index >= 0 && index < model.getPlayerHand(PlayerColor.RED).size()) {
      Card card = model.getPlayerHand(PlayerColor.RED).get(index);
      notifyCardSelect(PlayerColor.RED, card);
    }
  }

  private void handleBlueHandClick() {
    if (model.getCurrentPlayerColor() != PlayerColor.BLUE) {
      return;
    }
    int index = blueHandPanel.getSelectedCardIndex();
    if (index >= 0 && index < model.getPlayerHand(PlayerColor.BLUE).size()) {
      Card card = model.getPlayerHand(PlayerColor.BLUE).get(index);
      notifyCardSelect(PlayerColor.BLUE, card);
    }
  }

  private void handleGridClick(MouseEvent e) {
    PlayerColor currentPlayer = model.getCurrentPlayerColor();
    int cellWidth = gridPanel.getWidth() / model.getBoard().getGrid().getTotalColumns();
    int cellHeight = gridPanel.getHeight() / model.getBoard().getGrid().getTotalRows();

    int row = e.getY() / cellHeight;
    int col = e.getX() / cellWidth;

    Coordinate position = new GameCoordinate(row, col);
    notifyGridSelect(currentPlayer, position);
  }

  @Override
  public void addFeatures(Features features) {
    if (features == null) {
      throw new IllegalArgumentException("Features cannot be null");
    }
    featuresListeners.add(features);
    redHandPanel.setFeatures(features);
    blueHandPanel.setFeatures(features);
    gridPanel.setFeatures(features);
  }

  private void notifyCardSelect(PlayerColor player, Card card) {
    for (Features listener : featuresListeners) {
      listener.handleCardSelect(player, card);
    }
  }

  private void notifyGridSelect(PlayerColor player, Coordinate position) {
    for (Features listener : featuresListeners) {
      listener.handleGridSelect(player, position);
    }
  }

  @Override
  public void showError(String message) {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE));
  }

  @Override
  public void updateStatus(String message) {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    SwingUtilities.invokeLater(() -> statusLabel.setText(message));
  }

  private JLabel createStatusLabel() {
    JLabel label = new JLabel("Red Player's Turn", SwingConstants.CENTER);
    label.setFont(new Font("Arial", Font.BOLD, 16));
    label.setPreferredSize(new Dimension(1000, 30));
    add(label, BorderLayout.NORTH);
    return label;
  }

  private void setupPanels() {
    redHandPanel.setPreferredSize(new Dimension(180, 800));
    blueHandPanel.setPreferredSize(new Dimension(180, 800));
    gridPanel.setPreferredSize(new Dimension(600, 600));

    JPanel westPanel = createPaddedPanel(redHandPanel, 10, 10, 10, 5);
    JPanel eastPanel = createPaddedPanel(blueHandPanel, 10, 5, 10, 10);
    JPanel centerPanel = createPaddedPanel(gridPanel, 10, 5, 10, 5);

    add(westPanel, BorderLayout.WEST);
    add(centerPanel, BorderLayout.CENTER);
    add(eastPanel, BorderLayout.EAST);
  }

  private JPanel createPaddedPanel(JPanel innerPanel, int top, int left, int bottom, int right) {
    JPanel container = new JPanel(new BorderLayout());
    container.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
    container.add(innerPanel);
    return container;
  }

  @Override
  public void refresh() {
    SwingUtilities.invokeLater(() -> {
      updateGameStatus();
      redHandPanel.refresh();
      blueHandPanel.refresh();
      gridPanel.refresh();
      repaint();
    });
  }

  private void updateGameStatus() {
    if (model.getGameState() == GameState.GAME_OVER) {
      handleGameOverStatus();
    } else {
      handleInGameStatus();
    }
  }

  private void handleGameOverStatus() {
    PlayerColor winner = model.getWinner();
    if (winner != null) {
      statusLabel.setText(winner + " Player Wins! Game Over");
      statusLabel.setForeground(winner == PlayerColor.RED
              ? new Color(255, 0, 0) : new Color(0, 0, 255));
    } else {
      statusLabel.setText("Game Over - It's a Tie!");
      statusLabel.setForeground(Color.BLACK);
    }
  }

  private void handleInGameStatus() {
    PlayerColor currentPlayer = model.getCurrentPlayerColor();
    statusLabel.setText(currentPlayer + " Player's Turn");
    statusLabel.setForeground(currentPlayer == PlayerColor.RED
            ? new Color(255, 0, 0) : new Color(0, 0, 255));
  }
}