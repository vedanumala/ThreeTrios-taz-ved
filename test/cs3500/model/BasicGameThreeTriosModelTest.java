package cs3500.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests implementation details of BasicThreeTriosGame.
 * Tests internal methods and configuration loading.
 */
public class BasicGameThreeTriosModelTest {
  private BasicThreeTriosGame model;

  /**
   * Sets up the test environment.
   */
  @Before
  public void setup() {
    model = new BasicThreeTriosGame();
  }

  @Test
  public void testLoadGridFromFile() throws IOException {
    Grid grid = model.loadGridFromFile("5x5BoardConnectedCardCells.txt");

    // Verify dimensions
    assertEquals("Grid should be 5x5", 5, grid.getTotalRows());
    assertEquals("Grid should be 5x5", 5, grid.getTotalColumns());

    // Verify specific cell states from known file layout
    assertEquals("Top-left should be card cell",
            CellState.AVAILABLE, grid.getCellState(new GameCoordinate(0, 0)));
    assertEquals("Top-right should be card cell",
            CellState.AVAILABLE, grid.getCellState(new GameCoordinate(0, 4)));
    assertEquals("Center should be card cell",
            CellState.AVAILABLE, grid.getCellState(new GameCoordinate(2, 2)));
    assertEquals("Known hole position",
            CellState.HOLE, grid.getCellState(new GameCoordinate(0, 1)));
  }

  @Test
  public void testLoadCardsFromFile() throws IOException {
    List<Card> cards = model.loadCardsFromFile("TenCardSetFor3x3Board.txt");

    // Verify card count
    assertEquals("Should load 10 cards", 10, cards.size());

    // Verify first card (Dragon)
    Card dragon = cards.get(0);
    assertEquals("Dragon should be first card", "Dragon", dragon.getIdentifier());
    assertEquals("Dragon north value", 8, dragon.getValue(Direction.NORTH));
    assertEquals("Dragon east value", 7, dragon.getValue(Direction.EAST));
    assertEquals("Dragon south value", 9, dragon.getValue(Direction.SOUTH));
    assertEquals("Dragon west value", 6, dragon.getValue(Direction.WEST));

    // Verify last card (Sorcerer)
    Card sorcerer = cards.get(9);
    assertEquals("Sorcerer should be last card", "Sorcerer",
            sorcerer.getIdentifier());
    assertEquals("Sorcerer north value", 8, sorcerer.getValue(Direction.NORTH));
    assertEquals("Sorcerer east value", 7, sorcerer.getValue(Direction.EAST));
    assertEquals("Sorcerer south value", 6, sorcerer.getValue(Direction.SOUTH));
    assertEquals("Sorcerer west value", 9, sorcerer.getValue(Direction.WEST));
  }

  @Test(expected = IOException.class)
  public void testLoadGridFromMissingFile() throws IOException {
    model.loadGridFromFile("nonexistent.txt");
  }

  @Test(expected = IOException.class)
  public void testLoadCardsFromMissingFile() throws IOException {
    model.loadCardsFromFile("nonexistent.txt");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLoadGridWithEvenDimensions() throws IOException {
    Path tempFile = Files.createTempFile("evenGrid", ".txt");
    Files.write(tempFile, "4 4\nCCCC\nCCCC\nCCCC\nCCCC".getBytes());

    try {
      model.loadGridFromFile(tempFile.toString());
    } finally {
      Files.deleteIfExists(tempFile);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLoadGridWithMismatchedContent() throws IOException {
    Path tempFile = Files.createTempFile("mismatchedGrid", ".txt");
    Files.write(tempFile, "3 3\nCC\nCC".getBytes());

    try {
      model.loadGridFromFile(tempFile.toString());
    } finally {
      Files.deleteIfExists(tempFile);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLoadGridWithInvalidCells() throws IOException {
    Path tempFile = Files.createTempFile("invalidGrid", ".txt");
    Files.write(tempFile, "3 3\nCXY\nCXC\nXCX".getBytes());

    try {
      model.loadGridFromFile(tempFile.toString());
    } finally {
      Files.deleteIfExists(tempFile);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLoadCardsWithMalformedData() throws IOException {
    Path tempFile = Files.createTempFile("malformedCards", ".txt");
    Files.write(tempFile, "Dragon bad format\nKnight 1 2".getBytes());

    try {
      model.loadCardsFromFile(tempFile.toString());
    } finally {
      Files.deleteIfExists(tempFile);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLoadCardsWithInvalidValues() throws IOException {
    Path tempFile = Files.createTempFile("invalidCards", ".txt");
    Files.write(tempFile, "Dragon 11 7 9 6".getBytes()); // 11 exceeds max value

    try {
      model.loadCardsFromFile(tempFile.toString());
    } finally {
      Files.deleteIfExists(tempFile);
    }
  }

  @Test
  public void testCardValueParsing() throws IOException {
    Path tempFile = Files.createTempFile("validCards", ".txt");
    // Test both numeric and 'A' values
    Files.write(tempFile, "TestCard 9 A 8 7".getBytes());

    try {
      List<Card> cards = model.loadCardsFromFile(tempFile.toString());
      Card card = cards.get(0);
      assertEquals("Numeric value should parse correctly", 9,
              card.getValue(Direction.NORTH));
      assertEquals("'A' should parse to 10", 10, card.getValue(Direction.EAST));
      assertEquals("Numeric value should parse correctly", 8,
              card.getValue(Direction.SOUTH));
      assertEquals("Numeric value should parse correctly", 7,
              card.getValue(Direction.WEST));
    } finally {
      Files.deleteIfExists(tempFile);
    }
  }

  @Test
  public void testGridCardCellCount() throws IOException {
    Grid grid = model.loadGridFromFile("3x3BoardNoHoles.txt");
    assertEquals("3x3 grid without holes should have 9 card cells",
            9, grid.getCardCellCount());

    grid = model.loadGridFromFile("5x5BoardConnectedCardCells.txt");
    int expectedCells = 25 - 10; // 5x5 grid minus known hole count
    assertEquals("5x5 grid should have correct card cell count",
            expectedCells, grid.getCardCellCount());
  }
}
