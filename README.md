# OOD-taz-ved

# Three Trios Game Implementation

## Part 1:

## Overview

This repository contains an implementation of the Three Trios card game, a strategic two-player grid-based card game inspired by Triple Triad. Players take turns placing cards on a grid and battling adjacent cards, with the goal of controlling the most cards by the end of the game.

### Core Game Concepts
- Two players (Red and Blue) compete on a customizable grid
- Each card has directional attack values (North, South, East, West)
- Cards battle adjacent cards and can flip ownership through combat
- Winner is determined by total card ownership when grid is full

## Architecture

The implementation follows a component-based architecture with clear separation of concerns:

### Model Components
The model layer is built around several key abstractions:

1. **Game State Management**
   - `ThreeTrios` interface: Core game logic and state management
   - `AbstractThreeTriosGame`: Base implementation with common game functionality
   - `BasicThreeTriosGame`: Concrete implementation of the base game rules
   - `GameStateManager`: Manages turn progression and game status
   - `GameState`: Immutable snapshot of game state

2. **Board Management**
   - `Board`: Grid representation and cell management
   - `Cell`: Individual grid spaces (can be holes or card spaces)
   - `TrioBoard`: Concrete board implementation
   - `CellStatus`: Enum for cell states (AVAILABLE, OCCUPIED, HOLE)

3. **Battle System**
   - `BattleManager`: Handles card combat resolution
   - `TrioBattleManager`: Implements battle rules and combo mechanics
   - `BattleChain`: Tracks battle results and card flips
   - `BattleResult`: Individual battle outcome data

4. **Card Management**
   - `Card`: Base card interface
   - `TrioCard`: Concrete card implementation with directional values
   - `CardDirection`: Enum for card directions and coordinates

5. **Player System**
   - `Player`: Interface for player actions and state
   - `PlayerColor`: Enum for player identification (RED/BLUE)

### View Components
- `TrioView`: Interface for game visualization
- `TextualTrioView`: ASCII-based console rendering
- `ViewFactory`: Factory pattern for view creation
- `ViewUtils`: Helper methods for formatting game state

### Configuration Management
Configuration handling is implemented in the view layer since it deals with external resource interpretation:
- `GridConfigReader`: Parses grid layout files
- `CardConfigReader`: Parses card definition files

## Design Decisions

### 1. Component Organization
We chose to separate the battle system into its own component rather than embedding it in the main game logic because:
- Battle mechanics are complex and warrant their own abstraction
- This allows for future battle rule variations without modifying core game logic
- Enables isolated testing of battle outcomes

### 2. State Management
The game state is managed through multiple coordinating classes:
- `GameStateManager` handles turn progression and game status
- `GameState` provides immutable state snapshots
- State changes are validated through the manager before being applied

This approach:
- Maintains clear ownership of state modification
- Prevents invalid state transitions
- Makes the game state easily observable

### 3. Battle System Design
The battle system uses a chain-of-responsibility pattern:
1. Initial battle phase from card placement
2. Combo phase for newly flipped cards
3. Chain terminates when no more flips occur

Benefits:
- Clean separation of battle resolution steps
- Easy to extend with new battle rules
- Natural handling of combo mechanics

### 4. Configuration Management
File parsing is handled in the view layer because:
- Configuration files are an external representation of game state
- Parsing is conceptually similar to rendering (transforming between representations)
- Keeps model focused on game logic rather than I/O

## Testing Strategy

The codebase includes three types of tests:
1. `ThreeTriosExamples`: Demonstrates basic usage patterns
2. `ThreeTriosModelTest`: Tests public interface contracts
3. `ThreeTriosImplementationTest`: Tests internal implementation details

## Future Extensions

The design supports several planned extensions:
1. GUI implementation
2. AI players
3. Variant battle rules
4. Network play capabilities

## File Structure

```
src/
├── model/
│   ├── AbstractThreeTriosGame.java  # Base game implementation
│   ├── BasicThreeTriosGame.java     # Concrete game implementation
│   ├── battle/                      # Battle system
│   ├── board/                       # Board representation
│   └── state/                       # Game state management
├── view/
│   ├── TextualTrioView.java         # Console rendering
│   └── config/                      # Configuration readers
└── test/
    ├── ThreeTriosExamples.java      # Usage examples
    └── ThreeTriosModelTest.java     # Interface tests
```

# Changes for Part 2

## Battle System Improvements

### What was missing
In the initial implementation, the battle mechanics for the Three Trios game were not fully implemented. Specifically:

1. Card Battle Resolution
   - The original implementation lacked proper card battle mechanics
   - Cards weren't being flipped when a stronger card was placed adjacent to weaker ones
   - Battle directions weren't being properly calculated

2. Battle Chains and Combos
   - The system didn't support combo effects where one card flip could trigger additional battles
   - Multiple flips from a single card placement weren't being handled

3. Game State Management
   - The game state wasn't properly transitioning between different phases (BATTLE_PHASE, COMBO_PHASE)
   - Card ownership changes during battles weren't being tracked correctly

### Implementation Changes

1. Battle Handler Enhancement
   - Created dedicated `GameBattleHandler` class to manage battle resolution
   - Implemented proper direction-based battle calculations (NORTH, SOUTH, EAST, WEST)
   - Added support for card flipping when battle conditions are met

2. Combo System Implementation
   - Added combo phase handling in `GameBattleHandler`
   - Implemented chain reaction support where newly flipped cards can trigger additional battles

3. State Management Improvements
   - Enhanced game state transitions in `BasicGameThreeTriosModel`
   - Added proper tracking of battle phases and combo resolutions

### Design Decisions

1. Separation of Concerns
   - Battle logic was moved to a dedicated handler class to improve modularity
   - Clear separation between board state management and battle resolution

2. Chain of Responsibility Pattern
   - Implemented battle resolution as a chain of events
   - Each battle can trigger subsequent battles in a controlled manner

3. State Pattern Enhancement
   - Improved state transitions to better reflect the game's phases
   - Added explicit states for battle and combo phases

### Testing Improvements

1. Battle Mechanics Testing
   - Added comprehensive tests for basic battle scenarios
   - Verified card flipping behavior in various directions

2. Combo Testing
   - Added tests for chain reactions and multiple card flips
   - Verified combo resolution order and correctness

3. State Transition Testing
   - Added tests to verify proper state transitions during battles
   - Verified game completion conditions after battles

## File Structure and Changes
#### Modified Files:
```
src/
└── cs3500/
└── model/
├── BasicGameThreeTriosModel.java      // Major changes for battle mechanics
│   • Added battle phase handling
│   • Implemented combo handling
│   • Enhanced playCard() logic
│
├── GameBattleHandler.java            // New file for battle handling
│   • Handles battle resolutions
│   • Manages combo chains
│   • Calculates battle outcomes
│
├── BattleHandler.java                // New interface for battle system
│   • Defines battle handling methods
│   • Specifies combo requirements
│
├── Board.java                        // Modified for battle support
│   • Added card flipping support
│   • Enhanced battle direction checks
│
├── GameBoard.java                    // Added battle-related methods
│   • Implemented isCardWinningBattle()
│   • Added direction calculation
│   • Enhanced card flipping logic
│
├── Card.java                         // Modified for battle mechanics
│   • Enhanced direction-based values
│   • Added owner management
│
├── GameState.java                    // Added new states
│   • Added BATTLE_PHASE
│   • Added COMBO_PHASE
│
└── Direction.java                    // New enum for directions
• Defines NORTH, SOUTH, EAST, WEST
• Handles direction opposites
```

These changes have resulted in a more robust and complete implementation of the Three Trios game, particularly in handling card battles and their effects on the game state.


## View Implementation
- Created new view interfaces:
   - `ThreeTriosView`: Main view interface
   - `ThreeTriosPanel`: Interface for game panels
   - `ThreeTriosFrame`: Interface for the main frame
- Implemented Swing-based view components:
   - `SwingThreeTriosView`: Main view implementation
   - `HandPanel`: Displays player hands
   - `GridPanel`: Displays game grid
- Added support for:
   - Mouse interaction
   - Card selection highlighting
   - Dynamic resizing
   - Visual feedback

## Strategy Implementation
- Created `Strategy` interface for implementing different play strategies
- Implemented two core strategies:
   1. `MaxFlipsStrategy`: Maximizes cards flipped in current turn
   2. `CornerStrategy`: Prioritizes corner positions
- Added tie-breaking logic:
   - Positions: Upper-left priority
   - Cards: Lower index priority
- Created comprehensive test suite using mocks

## Testing
- Added mock implementations for testing strategies
- Created visual tests (screenshots) for view implementation
- Added unit tests for:
   - Strategy implementations


# Changes for Part 3

In this part, we made some major improvements to how players interact with the game and how different parts of the code work together. The biggest change was adding a proper controller to manage the game flow instead of having everything run through the model.

## What's New?

### 1. Added Game Controllers
We realized having the model handle everything wasn't ideal, so we created:
- `GameController` - the main brain that coordinates between the view and model for each player
- `ThreeTriosController` - an interface that defines what a controller can do
- `Features` - handles all the player actions (like selecting cards and positions)

The cool thing about this setup is that each player gets their own controller, so it doesn't matter if you're playing against an AI or another person - it all works the same way.

### 2. Better Player Actions
Before, player moves weren't really organized. Now we have:
- Clear handling of card selection
- Position selection on the grid
- Move confirmation
- The ability to cancel moves (really helpful when you change your mind!) by right clicking the mouse after a card is selected

### 3. Keeping Everyone Updated
We added a way for the model to tell everyone what's happening:
- When cards get played
- When battles happen
- When someone wins
- Current scores and game state

This makes the UI much more responsive and keeps players in the loop about what's going on.

### 4. Game Coordination
Added a `GameControllerRegistry` to help controllers find each other. This might sound fancy, but it just means:
- The Red player's controller can tell the Blue player's controller when it's their turn
- AI players can jump in at the right time
- Everything stays synchronized

## What Changed From Part 2?

1. **Model Changes**
- Moved all the player interaction stuff to the controller
- Made the model focus just on game rules and state
- Added ways to notify when things change

2. **View Updates**
- Views now talk to controllers instead of directly to the model
- Added feedback for player actions
- Made it clearer whose turn it is

3. **Testing**
- Added proper mocks for testing controllers
- Made tests more focused on specific features
- Better coverage of player interactions


## Project Structure

```
src/
├── cs3500/
├── controller/
│   ├── Features.java           // Handles player actions
│   ├── GameController.java     // Main controller implementation
│   └── ThreeTriosController.java // Controller interface
│
├── model/
│   ├── basic/
│   │   ├── AbstractThreeTriosGame.java
│   │   ├── BasicThreeTriosGame.java
│   │   └── AIThreeTriosGame.java
│   │
│   ├── board/
│   │   ├── Board.java
│   │   ├── Cell.java
│   │   ├── CellState.java
│   │   ├── GameBoard.java
│   │   └── GameCell.java
│   │
│   ├── battle/
│   │   ├── BattleHandler.java
│   │   └── GameBattleHandler.java
│   │
│   ├── player/
│   │   ├── AIPlayer.java
│   │   ├── GamePlayer.java
│   │   ├── Player.java
│   │   └── PlayerColor.java
│   │
│   ├── grid/
│   │   ├── Grid.java
│   │   ├── GameGrid.java
│   │   ├── Coordinate.java
│   │   └── GameCoordinate.java
│   │
│   ├── card/
│   │   ├── Card.java
│   │   ├── GameCard.java
│   │   └── Direction.java
│   │
│   ├── state/
│   │   └── GameState.java
│   │
│   ├── ThreeTriosModel.java
│   └── ReadOnlyThreeTriosModel.java
│
├── strategy/
│   ├── AbstractStrategy.java
│   ├── CornerStrategy.java
│   ├── MaxFlipsStrategy.java
│   ├── Move.java
│   └── Strategy.java
│
├── view/
│   ├── GridPanel.java
│   ├── HandPanel.java
│   ├── SwingThreeTriosView.java
│   ├── ThreeTriosFrame.java
│   ├── ThreeTriosPanel.java
│   └── ThreeTriosView.java
│
└── ThreeTrios.java        // Main class

test/
├── cs3500/
├── controller/
│   └── GameControllerTest.java
│
├── model/
│   ├── BasicGameThreeTriosModelTest.java
│   └── ThreeTriosModelTest.java
│
├── strategy/
│   ├── MockThreeTriosModel.java
│   └── StrategyTest.java
│
├── view/
│   └── MockView.java
│
└── ThreeTriosExamples.java
```

## Changes Made in HW 8

For this assignment, we had to work with another team's view implementation for our Three Trios game. We had to keep our original view for Player 1 (Red) and use their view for Player 2 (Blue). This required creating adapter classes to make the two different implementations work together.

### What Works and What Doesn't

What Works:
- Playing the game with a human player (Red) and an AI using maxflips strategy (Blue)
- Basic game functions like placing cards and battles
- Turn switching between players
- Keeping track of scores
- Game ending correctly

What Doesn't Work:
- The 5x5 board layout causes errors
- Two human players trying to play together
- Using the corner strategy for the AI
- Some of the provider's view features like animations

### Files We Created

We had to create four new files to make everything work together:

1. ModelAdapter.java - Makes our game model work with their view
2. ControllerAdapter.java - Helps handle player moves between the two views
3. ProviderViewAdapter.java - Makes their view work with our game
4. PlayerTypeAdapter.java - Helps convert between different player types

We also had to update our main ThreeTrios.java file to support using both views at once. This meant changing how we set up the game and how we handle different types of players.

### What We Learned From Their Code

Looking at the provider's code was interesting. Their view had some cool features, but it was hard to make it work with our implementation. The main issue was how they handled the game board and cards - they expected things to work differently than how we designed our game.

The code itself was pretty well written and organized. They included good comments that helped us understand what their code does. However, we ran into some problems when their view expected certain things about the cards and board that our game handles differently.

### Our Experience

Working on this project taught us a lot about making different code work together. The biggest challenge was dealing with null values - their view would crash sometimes because it expected data in a different format than what our game provides.

The provider team was helpful when we had questions. They responded quickly and helped explain how their code works. Even though we couldn't get everything working perfectly, we learned a lot about how to design code that other people might need to use later.

If we could do this project over, we would:
- Add better error checking in our code
- Write clearer comments about how our code works
- Make our interfaces more flexible
- Add more tests to catch problems early

Right now, the best way to run our game is using a human player for Red (Player 1) and the maxflips AI strategy for Blue (Player 2). Other combinations might cause errors, especially with larger board sizes.
