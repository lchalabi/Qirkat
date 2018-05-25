Exhibited here is my ability to build, debug, and test a larger application using OOP in java.

In this project, I wrote a program to play the game, named Qirkat or Alquerque, similar to checkers.  For more details on general rules please see https://en.wikipedia.org/wiki/Alquerque (please note that my rules may slightly differ from those cited on wikipedia). My program incorporates a minimax, alpha-beta pruning AI so that a single human player can play against the machine, although it provides options for completely manual play and for completely automated play. The players (we'll simply call them White and Black) can each be either a manual player, entering moves as input, or an automated player (AI for short). Manual players can talk to the program by entering commands via a textual interface.


Commands to begin and end a game:
clear - Abandons the current game (if one is in progress), clears the board to its initial configuration, and places the program in the set-up state. Abandoning a game implies that you resign. This command is valid in any state.
start -  Enters playing state (has no effect if already in playing state). White and Black alternate moves. If there have been moves made during the set-up state, then play picks up at the point where these moves leave off (so, for example, if there was one set-up move made before start, then Black will move first). In the (unusual) case where the set-up moves have already won the game, start causes the program to report the winner and go into set-up state.
quit - Abandons any current game (as for clear) and exits the program. The end of input has the same effect.

Parameter setting commands:
auto C - Puts the game in set-up state and sets up the program so that player C (White or Black) is an AI. Initially, and after a clear command, White is a manual player and Black is an AI. Thus, the command auto White causes both White and Black to be AIs, so that the start command causes the machine to play a game against itself.
manual C - Puts the game in set-up state and Sets up the program so that player C (White or Black) is a manual player. Thus, the command manual Black causes both White and Black to be manual players (who presumably alternate entering moves on a terminal).

Entering moves:
The first and then every other move is for the White player, the second and then every other is for Black. We'll denote columns with letters a--e from the left and rows with numerals 1--5 from the bottom, as shown below.

===
5  b b b b b
4  b b b b b
3  b b - w w
2  w w w w w
1  w w w w w
   a b c d e
===

A move or jump consists of two or more positions separated by hyphens in the format c0r0−c1r1−...c0r0−c1r1−... (e.g., a1-b2 or c3-a5-a3 ). The first position gives a piece owned by the current player, and the second -- and for jumps, subsequent -- positions give empty positions to which the piece moves or jumps. The program rejects illegal moves and the AI does not make illegal moves.

Miscellaneous Commands:
help - Print a brief summary of the commands.
dump - This command is especially for testing and debugging. It prints the board out in exactly the following format at the start of a game:

===
  b b b b b
  b b b b b
  b b - w w
  w w w w w
  w w w w w
===

Here, - indicates an empty square, w indicates a White piece, and b indicates a Black piece.

To start a game versus the AI, enter Auto Black.

Notable Classes:
AI: Implements a player that chooses its own moves.  I implemented an alpha-beta pruning, minimax search algorithm for my AI.
Move: Represents a single move on a Qirkat board.
Board: Represents the Qirkat board.
Game: Controls the play of the game.
Player: Represents a Qirkat player (Black or White).
PieceColor: Used to describe the kind of piece on a Qirkat board.

Testing:
All files that end in .in are integration tests passed through the python3 files in the testing directory such as test-qirkat.py (I did not write test-qirkat.py, only the integration tests themselves).  To run all integrations tests, type "make check" inside the testing directory in the terminal.

