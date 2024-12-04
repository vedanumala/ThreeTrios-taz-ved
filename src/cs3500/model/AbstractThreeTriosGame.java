package cs3500.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for Three Trios game implementations.
 * Contains common functionality for both human and AI games.
 */
public abstract class AbstractThreeTriosGame implements ThreeTriosModel {
  protected final Map<PlayerColor, Player> players;
  protected PlayerColor currentPlayer;
  protected Board board;
  protected GameState gameState;
  protected BattleHandler battleHandler;

  /**
   * Constructor for the AbstractThreeTriosGame class.
   */
  protected AbstractThreeTriosGame() {
    this.players = new HashMap<>();
    this.currentPlayer = PlayerColor.RED;
    this.gameState = GameState.INITIALIZATION;
    this.board = new GameBoard(new GameGrid(3, 3));
    this.battleHandler = new GameBattleHandler(this.board);
  }

  @Override
  public void initializeGameFromFiles(String gridConfig, String cardsConfig) {
    if (gridConfig == null || cardsConfig == null) {
      throw new IllegalArgumentException("Config file paths cannot be null");
    }

    try {
      Grid grid = loadGridFromFile(gridConfig);
      List<Card> cards = loadCardsFromFile(cardsConfig);
      initializeGame(grid, cards);
    } catch (IOException e) {
      throw new IllegalArgumentException("Error reading configuration files: " + e.getMessage());
    }
  }

  /**
   * Loads a grid from a file.
   *
   * @param gridConfig path to grid configuration file
   * @return grid loaded from file
   * @throws IOException if an error occurs reading the file
   */
  protected Grid loadGridFromFile(String gridConfig) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(gridConfig))) {
      String[] dimensions = reader.readLine().split(" ");
      if (dimensions.length != 2) {
        throw new IllegalArgumentException("Invalid grid dimensions format");
      }

      int rows = Integer.parseInt(dimensions[0]);
      int cols = Integer.parseInt(dimensions[1]);
      Grid grid = new GameGrid(rows, cols);

      for (int i = 0; i < rows; i++) {
        String row = reader.readLine();
        if (row == null || row.length() != cols) {
          throw new IllegalArgumentException("Invalid grid row length");
        }

        for (int j = 0; j < cols; j++) {
          char cell = row.charAt(j);
          Coordinate pos = new GameCoordinate(i, j);

          if (cell == 'X') {
            grid.setCellState(pos, CellState.HOLE);
          } else if (cell == 'C') {
            grid.setCellState(pos, CellState.AVAILABLE);
          } else {
            throw new IllegalArgumentException("Invalid cell type: " + cell);
          }
        }
      }
      return grid;
    }
  }

  /**
   * Loads a list of cards from a file.
   *
   * @param cardsConfig path to cards configuration file
   * @return list of cards
   * @throws IOException if an error occurs reading the file
   */
  protected List<Card> loadCardsFromFile(String cardsConfig) throws IOException {
    List<Card> cards = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(cardsConfig))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] cardData = line.split(" ");
        if (cardData.length != 5) {
          throw new IllegalArgumentException("Invalid card data format");
        }

        String identifier = cardData[0];
        int northValue = parseCardValue(cardData[1]);
        int eastValue = parseCardValue(cardData[2]);
        int southValue = parseCardValue(cardData[3]);
        int westValue = parseCardValue(cardData[4]);

        Card card = new GameCard(identifier, null,
              northValue, eastValue, southValue, westValue);
        cards.add(card);
      }
    }
    return cards;
  }

  /**
   * Parses a card value from a string.
   *
   * @param value string representation of card value
   * @return integer value of card
   * @throws IllegalArgumentException if value is invalid
   */
  protected int parseCardValue(String value) {
    if (value.equals("A")) {
      return 10;
    }
    try {
      int numValue = Integer.parseInt(value);
      if (numValue < 1 || numValue > 10) {
        throw new IllegalArgumentException("Card values must be between 1 and 10");
      }
      return numValue;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid card value: " + value);
    }
  }

  @Override
  public void initializeGame(Grid grid, List<Card> cards) {
    if (grid == null || cards == null) {
      throw new IllegalArgumentException("Grid and cards cannot be null");
    }
    if (cards.size() % 2 != 0) {
      throw new IllegalArgumentException("Number of cards must be even");
    }

    this.board = new GameBoard(grid);
    this.battleHandler = new GameBattleHandler(this.board);
    this.gameState = GameState.INITIALIZATION;
    this.currentPlayer = PlayerColor.RED;

    distributeCards(cards);
  }

  /**
   * Distributes cards to players at the start of the game.
   *
   * @param cards list of cards to distribute
   */
  protected void distributeCards(List<Card> cards) {
    // Clear existing hands
    for (Player player : players.values()) {
      player.getHand().clear();
    }

    List<Card> shuffledCards = new ArrayList<>(cards);
    Collections.shuffle(shuffledCards);
    int cardsPerPlayer = cards.size() / 2;

    Player redPlayer = players.get(PlayerColor.RED);
    Player bluePlayer = players.get(PlayerColor.BLUE);

    // Distribute first half to Red player
    for (int i = 0; i < cardsPerPlayer; i++) {
      Card card = shuffledCards.get(i);
      card.setOwner(redPlayer);
      redPlayer.addCardToHand(card);
    }

    // Distribute second half to Blue player
    for (int i = cardsPerPlayer; i < shuffledCards.size(); i++) {
      Card card = shuffledCards.get(i);
      card.setOwner(bluePlayer);
      bluePlayer.addCardToHand(card);
    }
  }

  /**
   * Handles the end of the game.
   *
   * @param position position of the last card played
   */
  protected void handleBattles(Coordinate position) {
    gameState = GameState.BATTLE_PHASE;
    List<Coordinate> flippedCards = battleHandler.runBattle(position);

    if (!flippedCards.isEmpty()) {
      gameState = GameState.COMBO_PHASE;
      while (!flippedCards.isEmpty()) {
        flippedCards = battleHandler.runComboStep(flippedCards);
      }
    }
  }

  @Override
  public Board getBoard() {
    return board;
  }

  @Override
  public List<Card> getPlayerHand(PlayerColor player) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    return Collections.unmodifiableList(players.get(player).getHand());
  }

  @Override
  public PlayerColor getCurrentPlayerColor() {
    return currentPlayer;
  }

  @Override
  public int getPotentialFlips(Card card, Coordinate position) {
    if (card == null || position == null) {
      throw new IllegalArgumentException("Card and position cannot be null");
    }
    if (!board.canPlaceCard(position)) {
      return 0;
    }

    Board tempBoard = board.copy();
    BattleHandler tempBattleHandler = new GameBattleHandler(tempBoard);

    tempBoard.placeCard(card, position);
    List<Coordinate> flippedCards = tempBattleHandler.runBattle(position);
    int totalFlips = flippedCards.size();

    while (!flippedCards.isEmpty()) {
      flippedCards = tempBattleHandler.runComboStep(flippedCards);
      totalFlips += flippedCards.size();
    }

    return totalFlips;
  }

  @Override
  public GameState getGameState() {
    return gameState;
  }

  @Override
  public int getScore(PlayerColor player) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    return board.getCardCount(player) + players.get(player).getHand().size();
  }

  @Override
  public PlayerColor getWinner() {
    if (gameState != GameState.GAME_OVER) {
      return null;
    }

    int redScore = getScore(PlayerColor.RED);
    int blueScore = getScore(PlayerColor.BLUE);
    if (redScore > blueScore) {
      return PlayerColor.RED;
    } else if (blueScore > redScore) {
      return PlayerColor.BLUE;
    } else {
      return null;
    }
  }

  protected Player getCurrentPlayer() {
    return players.get(currentPlayer);
  }

  protected List<Card> getCurrentPlayerHand() {
    return getCurrentPlayer().getHand();
  }
}