package cs3500.model;

/**
 * Implementation for a card in the game with four directional values and ownership.
 */
public class GameCard implements Card {

  private final int northValue;
  private final int eastValue;
  private final int southValue;
  private final int westValue;
  private final String identifier;
  private Player owner;

  /**
   * Constructor for the GameCard class.
   *
   * @param identifier the unique identifier of the card
   * @param owner the owner of the card
   * @param northValue the value of the card in the north direction
   * @param eastValue the value of the card in the east direction
   * @param southValue the value of the card in the south direction
   * @param westValue the value of the card in the west direction
   */
  public GameCard(String identifier, Player owner, int northValue, int eastValue, int southValue,
                  int westValue) {
    if (identifier == null) {
      throw new IllegalArgumentException("Identifier cannot be null");
    }
    if (northValue < 0 || eastValue < 0 || southValue < 0 || westValue < 0) {
      throw new IllegalArgumentException("Card values must be non-negative");
    }

    this.northValue = northValue;
    this.eastValue = eastValue;
    this.southValue = southValue;
    this.westValue = westValue;
    this.identifier = identifier;
    this.owner = owner;
  }

  @Override
  public String getIdentifier() {
    return this.identifier;
  }

  @Override
  public int getValue(Direction direction) {
    if (direction == Direction.NORTH) {
      return this.northValue;
    } else if (direction == Direction.EAST) {
      return this.eastValue;
    } else if (direction == Direction.SOUTH) {
      return this.southValue;
    } else if (direction == Direction.WEST) {
      return this.westValue;
    }
    throw new IllegalArgumentException("Invalid direction");
  }


  @Override
  public Player getOwner() {
    return this.owner;
  }

  @Override
  public void setOwner(Player newOwner) {
    this.owner = newOwner;
  }
}
