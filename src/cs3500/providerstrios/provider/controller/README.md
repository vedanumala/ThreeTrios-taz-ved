## Overview
This code implements the game Three Trios devised by the great Lucia Nunez. 
Our interfaces were designed to be easily extendable but our classes are final.
Despite that, the classes we've implemented heavily take advantage of generics so a new class 
implementing one of our interfaces can easily be slotted in to work with our other classes.
Our model class's constructor takes two Scanners to set up the game. The grid data Scanner needs to
have the following format:
```
Template            Example

row col             3 5
row_0_values        CCCCX
row_1_values        CCCCC
row_2_values        CCXXC
...
```
Spots with an 'X' are holes and spots with a 'C' can hold cards.
The card data Scanner needs to have the following format:
```
Template                                                            Example

card_1_name north_damage south_damage east_damage west_damage       Skobby_Mouse 2 2 3 7
card_2_name north_damage south_damage east_damage west_damage       Tickle_ME_Elmo 1 2 3 4
card_3_name north_damage south_damage east_damage west_damage       Blue_Eyes_White_Dragon 7 3 4 2
card_3_name north_damage south_damage east_damage west_damage       El_Horso 7 8 9 1
...
```

## Getting Started
To simply play the game, place the provided jar in a folder with another folder called `board`.
The `board` folder should contain the .txt files with the board and card information. 
The default game is played with two human players, 
though the first two command line arguments allow you to choose more options. 
The keyword `player` is the default: a human player.
The keyword `strategy1` is the corner strategy while `strategy2` is the maximum flipping strategy.
The first command line argument designates the type of the first player while the second, the second.
The third command line argument allows you to provide a filepath to a board file to allow custom boards.
The fourth command line argument allows you to provide a filepath to a card file to allow custom decks.

To start with our code, we recommend creating a new instance of `ThreeTriosModel` and using it to 
play a game of Three Trios. For example:
```java
import model.ThreeTriosModel;

class YourClass {
  void yourMethod() {
    // Take in game setup data from files and start the game
    FileReader gridFile = new FileReader("no_holes_grid.txt");
    FileReader cardFile = new FileReader("cards_using_max.txt");
    Scanner gridScanner = new Scanner(gridFile);
    Scanner mycardscanner = new Scanner(cardFile);
    ThreeTriosModel model = new ThreeTriosModel();
    model.startGame(gridScanner, cardScanner);
    
    // Your code here to interact with the model
    
  }
}
```
Users can make their own grid and card files to customize their games. 
More advanced users might want to try extending one of our interfaces and creating a new version of
Three Trios, interfacing with the Classes we've written or overwriting them entirely.

## Key Components
Our implementation of the game has a model, view, and controller: `TTModel`, `TTView`, and 
`TTController`.
These are implemented by the classes `ThreeTriosModel`, `ThreeTriosTextView`, and 
`ThreeTriosController`.
The intention is that eventually the controller will drive the model and the view, but it has not
yet been implemented. As for how the view and the model interact, the model is the core of the 
complexity in this codebase and the view simply uses the getters of the model to give a text 
representation of its state.

## Key Subcomponents
### Model
That model then contains a grid of cells that can be either a hole or a cell that can hold a card.
The concept of a cell is represented by the interface 'Cell' while the types of cells that are holes and hold 
cards are the classes `Hole` and `CardCell` respectively.
The card themselves are represented by the interface `TTCard`s.

Both `TTModel` and `TTCard` have implementations with the game actually implemented: 
`ThreeTriosModel` and `ThreeTriosCard`.

It is intended that the model is the main class users interact with, while the other classes are 
seen only as a result of working with the model.

### View
The view renders the game and handles player input. The GUI view is the main view intended to be used
while the text view was for testing. 
It renders the cards as constantly changing color because it's fun!
It also rescales the size of the cards so the grid cells are always square and the hand cards stretch
from the top of the screen to the bottom. 
The hand cards also have a minimum width.

### Controller
There are two different types of controllers: one for human players, the other for automated strategies.
The human player controller acts as an intermediary between the view and the model, while the automated
controller acts as an intermediary between the strategy and the model.

## Organization
Everything is where you would expect: everything relating to the model is in the model package, 
everything relating to the model is in the model package, everything relating to the view is in the 
view package, and all tests are in the test directory.

## Strategies
With the `TTStrategy` interface, strategies were implemented in order to choose moves for the
`ThreeTriosModel` game, based on a specific technique. The best move from these strategies is stored
as a `Move`, which contains the row, column, and card index in the hand of the specific move.

The first strategy, and also the simplest, is the `FlipStrategy`. The strategy chooses a move that
flips as many cards on this turn as possible. If there is a tie, it chooses the move with uppermost, 
leftmost coordinate. Furthermore, if the coordinates are tied for a move, the strategy 
chooses the leftmost card index.

The second strategy is the `CornerStrategy`. Since cards have fewer attack values exposed in corners,
this strategy chooses a to play a card in a corner in a way that it is the hardest to flip. 
A card is the 'hardest to flip' if the average of its exposed attack values is the highest. 
If a cell adjacent to an exposed side of the corner cell is filled or a hole, the other exposed 
side is considered alone (so the 'average' is just the single attack value). 
If both cells adjacent to the corner cell are filled or are holes, the corner cell is impossible 
to flip and is therefore considered the 'hardest to flip.' In the case of any tie, the move with 
uppermost, leftmost corner is chosen. Furthermore, if two tied moves are in the same corner, 
chooses the move with the leftmost card index. 
If no corners are valid, the uppermost, leftmost corner is chosen with the leftmost card index.

## Changes for Part 2
* To improve the design from the first part, a stalemate option was added to the model. 
* Javadoc comments were improved for more specificity. 
* Classes in the model package were refactored to remove overly complex generics.

## Changes for Part 3
* Controllers were added.
* View was modified to support controllers and show current turn.
* Model was modified to support controllers.
* General cleaning of codebase.