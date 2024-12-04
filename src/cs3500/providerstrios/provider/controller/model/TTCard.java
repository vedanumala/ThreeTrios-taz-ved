package cs3500.providerstrios.provider.controller.model;

/**
 * The interface for a card used in a Three Trios game.
 * Each card has a name, a {@link Player} as an owner, and four separate {@link AttackValue}
 * in each {@link Direction}.
 */
public interface TTCard {

  /**
   * Returns the {@link TTCard}'s owner.
   * @return  this {@link TTCard}'s owner
   */
  Player getOwner();

  /**
   * Updates the {@link TTCard}'s owner to the provided {@link Player}.
   * @param owner the {@link TTCard}'s new owner
   * @throws IllegalArgumentException if owner is null
   */
  void setOwner(Player owner);

  /**
   * Sets the other {@link TTCard}'s owner to this {@link TTCard}'s owner if this
   * {@link TTCard} wins against the other {@link TTCard} with this
   * {@link TTCard} attacking in the provided {@link Direction}. Winning means this
   * {@link TTCard}'s {@link TTCard.AttackValue} is greater than the other {@link TTCard}'s
   * {@link TTCard.AttackValue} where they touch.
   *
   * @param other             the {@link TTCard} being attacked
   * @param directionToOther  the {@link Direction} this {@link TTCard} is attacking in
   * @return                  if this {@link TTCard} would beat the other
   *                          {@link TTCard} attacking in the given {@link Direction}
   * @throws IllegalArgumentException if either parameter is null
   * @throws IllegalArgumentException if other is the same object as this
   */
  boolean battle(TTCard other, Direction directionToOther);

  /**
   * Returns the {@link TTCard}'s {@link AttackValue} in the given {@link Direction}.
   * @param direction the {@link Direction} of the {@link AttackValue} to return
   * @return          the {@link AttackValue} in the given {@link Direction}
   * @throws IllegalArgumentException if direction is null
   */
  AttackValue getAttack(Direction direction);

  /**
   * Creates a copy of the {@link TTCard} with identical fields.
   * @return a copy of the {@link TTCard}.
   */
  TTCard copy();

  /**
   * Represents the attack value of a {@link TTCard}.
   */
  enum AttackValue {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10);

    private final int value;

    /**
     * Creates a new {@link AttackValue} of the given value.
     * @param value the {@link AttackValue}'s value
     */
    AttackValue(int value) {
      this.value = value;
    }

    /**
     * Retrieves the numerical value of this AttackValue.
     * @return the int value of this {@link AttackValue}
     */
    public int getValue() {
      return this.value;
    }

    /**
     * Whether this {@link AttackValue} is greater than the other {@link AttackValue}.
     * @param other the {@link AttackValue} being compared to
     * @return      whether this {@link AttackValue} is greater than the other {@link AttackValue}
     * @throws IllegalArgumentException if other is null
     */
    public boolean greaterThan(AttackValue other) {
      if (other == null) {
        throw new IllegalArgumentException("other cannot be null");
      }
      return this.value > other.value;
    }

    @Override
    public String toString() {
      switch (this) {
        case ONE:
          return "1";
        case TWO:
          return "2";
        case THREE:
          return "3";
        case FOUR:
          return "4";
        case FIVE:
          return "5";
        case SIX:
          return "6";
        case SEVEN:
          return "7";
        case EIGHT:
          return "8";
        case NINE:
          return "9";
        case TEN:
          return "A";
        default:
          throw new IllegalArgumentException("Invalid AttackValue");
      }
    }

    /**
     * Returns an {@link AttackValue} from a {@link String}.
     * @param string  the {@link String} to convert from
     * @return        the {@link AttackValue} the {@link String} converts to
     * @throws IllegalArgumentException if the string isn't a valid {@link AttackValue}
     */
    public static AttackValue fromString(String string) {
      switch (string) {
        case "1":
          return ONE;
        case "2":
          return TWO;
        case "3":
          return THREE;
        case "4":
          return FOUR;
        case "5":
          return FIVE;
        case "6":
          return SIX;
        case "7":
          return SEVEN;
        case "8":
          return EIGHT;
        case "9":
          return NINE;
        case "A":
          return TEN;
        default:
          throw new IllegalArgumentException(string + " is not a valid AttackValue");
      }
    }
  }
}