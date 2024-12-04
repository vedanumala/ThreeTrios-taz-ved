package cs3500.adapter;

import cs3500.controller.Features;
import cs3500.model.PlayerColor;
import cs3500.model.ReadOnlyThreeTriosModel;
import cs3500.providersThreetrios.provider.controller.TTController;
import cs3500.providersThreetrios.provider.view.TTGUIView;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Adapts the provider's view implementation to work with our view interface.
 */
public class ProviderViewAdapter implements cs3500.view.ThreeTriosView {
  private final TTGUIView providerView;
  private final ReadOnlyThreeTriosModel model;
  private final PlayerColor player;
  private TTController controllerAdapter;

  /**
   * Constructs a new ProviderViewAdapter.
   *
   * @param providerView the provider's view implementation to adapt
   * @param model the game model
   * @param player the player color this view represents
   * @throws IllegalArgumentException if any parameter is null
   */
  public ProviderViewAdapter(TTGUIView providerView, ReadOnlyThreeTriosModel model,
                             PlayerColor player) {
    if (providerView == null || model == null || player == null) {
      throw new IllegalArgumentException("Parameters cannot be null");
    }
    this.providerView = providerView;
    this.model = model;
    this.player = player;
  }

  @Override
  public void setVisible(boolean visible) {
    providerView.display(visible);
  }

  @Override
  public void refresh() {
    providerView.repaint();
    providerView.updateTitle();
  }

  @Override
  public void addFeatures(Features features) {
    if (features == null) {
      throw new IllegalArgumentException("Features cannot be null");
    }

    // Only create and set controller adapter if we haven't already
    if (this.controllerAdapter == null) {
      this.controllerAdapter = new ControllerAdapter(features, model, player);
    } else {
      ((ControllerAdapter) this.controllerAdapter).setFeatures(features);
    }
  }

  @Override
  public void showError(String message) {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(null, message,
                    "Error", JOptionPane.ERROR_MESSAGE));
  }

  @Override
  public void updateStatus(String message) {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    providerView.updateTitle();
  }

  /**
   * Gets the controller adapter for this view.
   *
   * @return the controller adapter
   */
  public TTController getControllerAdapter() {
    return controllerAdapter;
  }
}