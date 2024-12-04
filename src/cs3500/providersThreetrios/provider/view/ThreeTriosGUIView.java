package cs3500.threetrios.provider.view;

import cs3500.threetrios.provider.controller.TTController;
import cs3500.threetrios.provider.model.Player;
import cs3500.threetrios.provider.model.ReadOnlyTTModel;

import javax.swing.JFrame;

/**
 * Implementation of a view for a Three Trios game.
 * Almost everything relating to the GUI is implemented in the panel rather than the view.
 */
public class ThreeTriosGUIView extends JFrame implements TTGUIView {
  ThreeTriosPanel panel;
  Player player;

  /**
   * Creates a new view.
   * @param model the view's model
   */
  public ThreeTriosGUIView(ReadOnlyTTModel model, Player player, TTController controller) {
    if (model == null) {
      throw new IllegalArgumentException("model cannot be null");
    }
    if (player == null) {
      throw new IllegalArgumentException("player cannot be null");
    }
    if (controller == null) {
      throw new IllegalArgumentException("controller cannot be null");
    }
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.panel = new ThreeTriosPanel(model, player, this);
    this.player = player;
    this.add(panel);
    this.pack();
    this.panel.setController(controller);
  }

  @Override
  public void updateTitle() {
    setTitle(panel.getTitle());
  }

  @Override
  public void startTurn() {
    panel.startTurn();
  }

  @Override
  public void display(boolean show) {
    this.setVisible(show);
  }
}