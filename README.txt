Run without compiling:
	java -jar Chess.jar (Windows/Linux)

Compile manually:
	javac -cp aima-core.jar;forms-1.3.0.jar;image4j.jar *.java (Windows)
	javac -cp aima-core.jar:forms-1.3.0.jar:iamge4j.jar *.java (Linux)

Run manually
	java -cp .;aima-core.jar;forms-1.3.0.jar;image4j.jar main (Windows)
	java -cp .:aima-core.jar:forms-1.3.0.jar:image4j.jar main (Linux)


IMPORTANT! The graphical environment is set to run on a MINIMUM of a 600x600 resolution, it is important that your display meets this requirement. Because of this, you can't run the program through an SSH connection that doesn't support the use of graphics.

INSTRUCTIONS--------------------------
1. choose resolution when starting the game, either 800x800 or 600x600
2. The player is always White and always starts the game
3. There are various options the player can choose from to customize the AI, these will be presented below
OPTIONS-------------------------------
1. Depth slider - This slider is at the top of the main game window and allows the user to choose the maximum depth the AI will search through. It is capped at a maximum of 5 because even with proper move ordering during midgame the AI can spend several minutes picking a move. It is advised to stick with a depth of 3 because these will usually be the most optimal moves during play

2. Time slider - This slider allows the user to choose the maximum amount of time the AI is allowed to search for a move. With a depth of 3 the AI will always find a move within a few seconds, when increasing the depth to 4 or 5 it is advised to greatly increase the time slider because the AI will generally take a few minutes at this depth and if the AI runs out of time before completing the search it is almost gauranteed to be an unoptimal move.

3. Piece weights - These 6 boxes allow the player to customize the value of each piece, this will greatly affect the way the AI plays the game

4. Use PV-Node radio button - Selecting this option allows the AI to choose a principle variation node during searching in order to improve alpha-beta pruning by first searching the best node from the previous iteration.

5. Save global alpha radio button - Selecting this keeps a global alpha variable. Standard alpha-beta pruning usually resets the alpha to Negative infinity before searching a new node. By keeping a global alpha we are able to reuse the previous best alpha value in order to encourage earlier pruning

6. Multi-threaded radio button - Selecting this will perform all searched in a multi-threaded environment. This generally does not improve performance, and will most likely lead to the AI searching more nodes since the threads can't directly communicate with each other

7. Long PV depth radio button - This changes the PV node from using iterative deepening to standard depth-first search. If the AI chose a PV Node that will result in a greater alpha value than choosing this option will usually allow the AI to find it sooner. However, if the PV node changes frequently, or the AI chooses a PV node that won't result in a solution, than this option will result in a larger overhead because more nodes will be searched than standard iterative deepening.

8. Order Moves radio button - This saves all the moves from the first iteration and organizes them based on value in an attempt to reach perfect pruning in the alpha-beta tree. This means the most valuable or promising nodes will be searched first, while the least valuable nodes will be searched last.

9. Use PVT radio button - This uses a built in Piece Value Table for each piece to add to our heuristic value. Originally the AI uses only the piece weights and the total amount of legal moves, but with the PVT the AI has an idea of what areas are bad to have certain pieces in

10. Branching Factor combo box - After selecting the Order Moves radio button, the user can also choose the branching factor. Since we organized the nodes in a way such that the most valuable nodes are searched first, we can limit the branching factor, assuming a less valuable node does not turn out to be the solution. However, this if we limit the branching factor it is always possible that an unoptimal solution will be picked if we mistakenly prune a branch that we would've searched later on. The options in this combo box start at 1 (meaning we search every branch) and decreases by 1/8th every time. So the second option is 7/8ths (meaning we only search 7/8ths of the tree) the third options is 6/8ths and so on until we get to the minum value of 1/8ths (meaning we only search 1/8ths of the entire tree).

11. King End Game radio button - If the opponent determines it is near the end game it will encourage the king to move towards the center of the board for a clearer defense

12. Save Lone Piece radio button - If the opponent only has one piece of a certain pair left, i.e., one bishop left or one knight left, than it will try it's best to prevent that piece from being captured.

13. Queen's Mid Game Radio Button - This option prevents the AI from considering any Queen movements until mid game. This can be unoptimal, especially if an early checkmate from a queen move is available.

14. Pawn’s Defense Radio Button – This options pushes the AI to create an opening defense that heavily utilizes the pawns during the beginning of the game, instead of starting with pure Queen attacks.