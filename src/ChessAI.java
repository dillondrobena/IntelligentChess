import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import javax.swing.JTextField;

import aima.core.agent.Action;
import aima.core.agent.impl.DynamicAction;
import aima.core.search.framework.Metrics;


public class ChessAI extends Thread
{

	public Board.Type[][] boardTiles = new Board.Type[8][8];
	public Semaphore lock = new Semaphore(1, true);
	public DepthFirstAlphaBetaSearch search = new DepthFirstAlphaBetaSearch();
	public Results results = new Results();
	public Actions actions = new Actions();
	public HashMap<Action, Integer> possibleActions;
	public Action LEFT = new DynamicAction("Left");
	public Action RIGHT = new DynamicAction("Right");
	public Action UP = new DynamicAction("Up");
	public Action DOWN = new DynamicAction("Down");
	public Action DIAGUPLEFT = new DynamicAction("DiagUpLeft");
	public Action DIAGUPRIGHT = new DynamicAction("DiagUpRight");
	public Action DIAGDOWNLEFT = new DynamicAction("DiagDownLeft");
	public Action DIAGDOWNRIGHT = new DynamicAction("DiagDownRight");
	public Action UPRIGHT = new DynamicAction("UpRight");
	public Action UPLEFT = new DynamicAction("UpLeft");
	public Action LEFTUP = new DynamicAction("LeftUp");
	public Action LEFTDOWN = new DynamicAction("LeftDown");
	public Action RIGHTUP = new DynamicAction("RightUp");
	public Action RIGHTDOWN = new DynamicAction("RightDown");
	public Action DOWNLEFT = new DynamicAction("DownLeft");
	public Action DOWNRIGHT = new DynamicAction("DownRight");
	public Board superBoard;
	public int maxDepth;
	public int maxTime;
	public int movesMade = 1;

	public JTextField kingPiece;
	public JTextField queenPiece;
	public JTextField bishopPiece;
	public JTextField rookPiece;
	public JTextField knightPiece;
	public JTextField pawnPiece;

	public ChessAI()
	{
	}

	public void setBoard(Board board)
	{
		this.superBoard = board;
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				//Copy all chess pieces
				if (!board.getAllTiles()[i][j].isEmpty()) boardTiles[i][j] = board.getAllTiles()[i][j].getCurrentPiece().getType();
				else boardTiles[i][j] = null;
			}
		}
	}

	public void setMaxDepth(int depth)
	{
		maxDepth = depth;
	}

	public void setMaxTime(int time)
	{
		maxTime = time;
	}

	@Override
	public void run()
	{
		int[] results = search.makeDecision(boardTiles, maxDepth, maxTime);
		superBoard.realMove(results);
		movesMade += 2;
	}

	public void printBoard(Board.Type[][] board)
	{
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				if (board[i][j] != null)
				{
					System.out.print(board[i][j].toString() + " ");
				}
				else System.out.print("0" + " ");
			}
			System.out.println();
		}
	}

	public boolean areEnemies(Board.Type friendly, Board.Type enemy)
	{
		if (friendly.toString().charAt(0) == 'B' && enemy.toString().charAt(0) == 'W') return true;
		else if (friendly.toString().charAt(0) == 'W' && enemy.toString().charAt(0) == 'B') return true;
		else return false;
	}

	public Board.Type[][] copyOf(Board.Type[][] arrayToCopy)
	{
		Board.Type[][] newBoard = new Board.Type[8][8];
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				newBoard[i][j] = arrayToCopy[i][j];
			}
		}
		return newBoard;
	}

	public boolean isCheckMate(Board.Type[][] checkBoard, char player)
	{

		//printBoard(checkBoard);
		boolean checkmate = true;
		outerloop:
			for (int i = 0; i < 8; i++)
			{
				for (int j = 0; j < 8; j++)
				{
					if (checkBoard[i][j] != null && checkBoard[i][j].toString().charAt(0) == player)
					{
						if (actions.getLegalActions(checkBoard, checkBoard[i][j], i, j).size() != 0)
						{
							checkmate = false;
							break outerloop;
						}
					}
				}
			}
		return checkmate;
	}

	public boolean causalCheck(Board.Type[][] checkBoard, Board.Type type)
	{
		//Initiate all pieces to check for if king is checked
		char side = type.toString().charAt(0);
		int rank = 0;
		int file = 0;
		//Find king
		outerloop:
			for (int i = 0; i < 8; i++)
			{
				for (int j = 0; j < 8; j++)
				{
					if (side == 'W')
					{
						if (checkBoard[i][j] != null && checkBoard[i][j].equals(Board.Type.W_King))
						{
							rank = i;
							file = j;
							break outerloop;
						}
					}
					else if (side == 'B')
					{
						if (checkBoard[i][j] != null && checkBoard[i][j].equals(Board.Type.B_King))
						{
							rank = i;
							file = j;
							break outerloop;
						}
					}
				}
			}
		//Check all king's possible attackers
		if (side == 'B')
		{
			//Check diagdownright
			for (int i = rank + 1, j = file + 1; i < 8 && j < 8; i++, j++)
			{
				Board.Type piece = checkBoard[i][j];
				if (piece != null && piece.toString().charAt(0) == 'B') break;
				if (i == rank + 1 && j == file + 1 && piece != null && piece.equals(Board.Type.W_Pawn)) return true;
				if (piece != null && piece.toString().charAt(0) != 'B' && (piece.equals(Board.Type.W_Bishop) || piece.equals(Board.Type.W_Queen)))
				{
					return true;
				}
			}
			//Check diagdownleft
			for (int i = rank + 1, j = file - 1; i < 8 && j >= 0; i++, j--)
			{
				Board.Type piece = checkBoard[i][j];
				if (piece != null && piece.toString().charAt(0) == 'B') break;
				if (i == rank + 1 && j == file - 1 && piece != null && piece.equals(Board.Type.W_Pawn)) return true;
				if (piece != null && piece.toString().charAt(0) != 'B' && (piece.equals(Board.Type.W_Bishop) || piece.equals(Board.Type.W_Queen)))
				{
					return true;
				}
			}
			//Check diagupleft
			for (int i = rank - 1, j = file - 1; i >= 0 && j >= 0; i--, j--)
			{
				Board.Type piece = checkBoard[i][j];
				if (piece != null && piece.toString().charAt(0) == 'B') break;
				if (piece != null && piece.toString().charAt(0) != 'B' && (piece.equals(Board.Type.W_Bishop) || piece.equals(Board.Type.W_Queen)))
				{
					return true;
				}
			}
			//Check diagupright
			for (int i = rank - 1, j = file + 1; i >= 0 && j < 8; i--, j++)
			{
				Board.Type piece = checkBoard[i][j];
				if (piece != null && piece.toString().charAt(0) == 'B') break;
				if (piece != null && piece.toString().charAt(0) != 'B' && (piece.equals(Board.Type.W_Bishop) || piece.equals(Board.Type.W_Queen)))
				{
					return true;
				}
			}
			//Check up
			for (int i = rank - 1; i >= 0; i--)
			{
				Board.Type piece = checkBoard[i][file];
				if (piece != null && piece.toString().charAt(0) == 'B') break;
				if (piece != null && piece.toString().charAt(0) != 'B' && (piece.equals(Board.Type.W_Rook) || piece.equals(Board.Type.W_Queen)))
				{
					return true;
				}
			}
			//Check down
			for (int i = rank + 1; i < 8; i++)
			{
				Board.Type piece = checkBoard[i][file];
				if (piece != null && piece.toString().charAt(0) == 'B') break;
				if (piece != null && piece.toString().charAt(0) != 'B' && (piece.equals(Board.Type.W_Rook) || piece.equals(Board.Type.W_Queen)))
				{
					return true;
				}
			}
			//Check left
			for (int i = file - 1; i >= 0; i--)
			{
				Board.Type piece = checkBoard[rank][i];
				if (piece != null && piece.toString().charAt(0) == 'B') break;
				if (piece != null && piece.toString().charAt(0) != 'B' && (piece.equals(Board.Type.W_Rook) || piece.equals(Board.Type.W_Queen)))
				{
					return true;
				}
			}
			//Check right
			for (int i = file + 1; i < 8; i++)
			{
				Board.Type piece = checkBoard[rank][i];
				if (piece != null && piece.toString().charAt(0) == 'B') break;
				if (piece != null && piece.toString().charAt(0) != 'B' && (piece.equals(Board.Type.W_Rook) || piece.equals(Board.Type.W_Queen)))
				{
					return true;
				}
			}
			//Check UpRight
			if (rank - 2 >= 0 && file + 1 < 8 && checkBoard[rank - 2][file + 1] != null && checkBoard[rank - 2][file + 1].equals(Board.Type.W_Knight))
			{
				return true;
			}
			//Check UpLeft
			if (rank - 2 >= 0 && file - 1 >= 0 && checkBoard[rank - 2][file - 1] != null && checkBoard[rank - 2][file - 1].equals(Board.Type.W_Knight))
			{
				return true;
			}
			//Check DownRight
			if (rank + 2 < 8 && file + 1 < 8 && checkBoard[rank + 2][file + 1] != null && checkBoard[rank + 2][file + 1].equals(Board.Type.W_Knight))
			{
				return true;
			}
			//Check DownLeft
			if (rank + 2 < 8 && file - 1 >= 0 && checkBoard[rank + 2][file - 1] != null && checkBoard[rank + 2][file - 1].equals(Board.Type.W_Knight))
			{
				return true;
			}
			//Check LeftUp
			if (rank - 1 >= 0 && file + 2 < 8 && checkBoard[rank - 1][file + 2] != null && checkBoard[rank - 1][file + 2].equals(Board.Type.W_Knight))
			{
				return true;
			}
			//Check LeftDown
			if (rank + 1 < 8 && file + 2 < 8 && checkBoard[rank + 1][file + 2] != null && checkBoard[rank + 1][file + 2].equals(Board.Type.W_Knight))
			{
				return true;
			}
			//Check RightUp
			if (rank - 1 >= 0 && file - 2 >= 0 && checkBoard[rank - 1][file - 2] != null && checkBoard[rank - 1][file - 2].equals(Board.Type.W_Knight))
			{
				return true;
			}
			//Check RightDown
			if (rank + 1 < 8 && file - 2 >= 0 && checkBoard[rank + 1][file - 2] != null && checkBoard[rank + 1][file - 2].equals(Board.Type.W_Knight))
			{
				return true;
			}

		}
		else if (side == 'W')
		{
			//Check diagdownright
			for (int i = rank + 1, j = file + 1; i < 8 && j < 8; i++, j++)
			{
				Board.Type piece = checkBoard[i][j];
				if (piece != null && piece.toString().charAt(0) == 'W') break;
				if (piece != null && piece.toString().charAt(0) != 'W' && (piece.equals(Board.Type.B_Bishop) || piece.equals(Board.Type.B_Queen)))
				{
					return true;
				}
			}
			//Check diagdownleft
			for (int i = rank + 1, j = file - 1; i < 8 && j >= 0; i++, j--)
			{
				Board.Type piece = checkBoard[i][j];
				if (piece != null && piece.toString().charAt(0) == 'W') break;
				if (piece != null && piece.toString().charAt(0) != 'W' && (piece.equals(Board.Type.B_Bishop) || piece.equals(Board.Type.B_Queen)))
				{
					return true;
				}
			}
			//Check diagupleft
			for (int i = rank - 1, j = file - 1; i >= 0 && j >= 0; i--, j--)
			{
				Board.Type piece = checkBoard[i][j];
				if (piece != null && piece.toString().charAt(0) == 'W') break;
				if (i == rank - 1 && j == file - 1 && piece != null && piece.equals(Board.Type.B_Pawn)) return true;
				if (piece != null && piece.toString().charAt(0) != 'W' && (piece.equals(Board.Type.B_Bishop) || piece.equals(Board.Type.B_Queen)))
				{
					return true;
				}
			}
			//Check diagupright
			for (int i = rank - 1, j = file + 1; i >= 0 && j < 8; i--, j++)
			{
				Board.Type piece = checkBoard[i][j];
				if (piece != null && piece.toString().charAt(0) == 'W') break;
				if (i == rank - 1 && j == file + 1 && piece != null && piece.equals(Board.Type.B_Pawn)) return true;
				if (piece != null && piece.toString().charAt(0) != 'W' && (piece.equals(Board.Type.B_Bishop) || piece.equals(Board.Type.B_Queen)))
				{
					return true;
				}
			}
			//Check up
			for (int i = rank - 1; i >= 0; i--)
			{
				Board.Type piece = checkBoard[i][file];
				if (piece != null && piece.toString().charAt(0) == 'W') break;
				if (piece != null && piece.toString().charAt(0) != 'W' && (piece.equals(Board.Type.B_Rook) || piece.equals(Board.Type.B_Queen)))
				{
					return true;
				}
			}
			//Check down
			for (int i = rank + 1; i < 8; i++)
			{
				Board.Type piece = checkBoard[i][file];
				if (piece != null && piece.toString().charAt(0) == 'W') break;
				if (piece != null && piece.toString().charAt(0) != 'W' && (piece.equals(Board.Type.B_Rook) || piece.equals(Board.Type.B_Queen)))
				{
					return true;
				}
			}
			//Check left
			for (int i = file - 1; i >= 0; i--)
			{
				Board.Type piece = checkBoard[rank][i];
				if (piece != null && piece.toString().charAt(0) == 'W') break;
				if (piece != null && piece.toString().charAt(0) != 'W' && (piece.equals(Board.Type.B_Rook) || piece.equals(Board.Type.B_Queen)))
				{
					return true;
				}
			}
			//Check right
			for (int i = file + 1; i < 8; i++)
			{
				Board.Type piece = checkBoard[rank][i];
				if (piece != null && piece.toString().charAt(0) == 'W') break;
				if (piece != null && piece.toString().charAt(0) != 'W' && (piece.equals(Board.Type.B_Rook) || piece.equals(Board.Type.B_Queen)))
				{
					return true;
				}
			}
			//Check UpRight
			if (rank - 2 >= 0 && file + 1 < 8 && checkBoard[rank - 2][file + 1] != null && checkBoard[rank - 2][file + 1].equals(Board.Type.B_Knight))
			{
				return true;
			}
			//Check UpLeft
			if (rank - 2 >= 0 && file - 1 >= 0 && checkBoard[rank - 2][file - 1] != null && checkBoard[rank - 2][file - 1].equals(Board.Type.B_Knight))
			{
				return true;
			}
			//Check DownRight
			if (rank + 2 < 8 && file + 1 < 8 && checkBoard[rank + 2][file + 1] != null && checkBoard[rank + 2][file + 1].equals(Board.Type.B_Knight))
			{
				return true;
			}
			//Check DownLeft
			if (rank + 2 < 8 && file - 1 >= 0 && checkBoard[rank + 2][file - 1] != null && checkBoard[rank + 2][file - 1].equals(Board.Type.B_Knight))
			{
				return true;
			}
			//Check LeftUp
			if (rank - 1 >= 0 && file + 2 < 8 && checkBoard[rank - 1][file + 2] != null && checkBoard[rank - 1][file + 2].equals(Board.Type.B_Knight))
			{
				return true;
			}
			//Check LeftDown
			if (rank + 1 < 8 && file + 2 < 8 && checkBoard[rank + 1][file + 2] != null && checkBoard[rank + 1][file + 2].equals(Board.Type.B_Knight))
			{
				return true;
			}
			//Check RightUp
			if (rank - 1 >= 0 && file - 2 >= 0 && checkBoard[rank - 1][file - 2] != null && checkBoard[rank - 1][file - 2].equals(Board.Type.B_Knight))
			{
				return true;
			}
			//Check RightDown
			if (rank + 1 < 8 && file - 2 >= 0 && checkBoard[rank + 1][file - 2] != null && checkBoard[rank + 1][file - 2].equals(Board.Type.B_Knight))
			{
				return true;
			}
		}
		return false;
	}

	public class Results
	{

		public Board.Type[][] getResult(Board.Type[][] oldState, Action action, int row, int column, int count)
		{
			Board.Type[][] newState = copyOf(oldState);
			int currentRank = row;
			int currentFile = column;
			if (action.equals(LEFT))
			{
				newState[currentRank][currentFile - count] = newState[currentRank][currentFile];
				newState[currentRank][currentFile] = null;
			}
			else if (action.equals(RIGHT))
			{
				newState[currentRank][currentFile + count] = newState[currentRank][currentFile];
				newState[currentRank][currentFile] = null;
			}
			else if (action.equals(UP))
			{
				newState[currentRank - count][currentFile] = newState[currentRank][currentFile];
				/*if (newState[currentRank][currentFile].equals(Board.Type.W_Pawn) && (currentRank - count) == 0)
				{
					newState[currentRank - count][currentFile] = Board.Type.W_Queen;
				}*/
				newState[currentRank][currentFile] = null;
			}
			else if (action.equals(DOWN))
			{
				newState[currentRank + count][currentFile] = newState[currentRank][currentFile];
				/*if (newState[currentRank][currentFile].equals(Board.Type.B_Pawn) && (currentRank - count) == 0)
				{
					newState[currentRank - count][currentFile] = Board.Type.B_Queen;
				}*/
				newState[currentRank][currentFile] = null;
			}
			else if (action.equals(DIAGUPLEFT))
			{
				newState[currentRank - count][currentFile - count] = newState[currentRank][currentFile];
				newState[currentRank][currentFile] = null;
			}
			else if (action.equals(DIAGUPRIGHT))
			{
				newState[currentRank - count][currentFile + count] = newState[currentRank][currentFile];
				newState[currentRank][currentFile] = null;
			}
			else if (action.equals(DIAGDOWNLEFT))
			{
				newState[currentRank + count][currentFile - count] = newState[currentRank][currentFile];
				newState[currentRank][currentFile] = null;
			}
			else if (action.equals(DIAGDOWNRIGHT))
			{
				newState[currentRank + count][currentFile + count] = newState[currentRank][currentFile];
				newState[currentRank][currentFile] = null;
			}
			else if (action.equals(UPRIGHT))
			{
				newState[currentRank - 2][currentFile + 1] = newState[currentRank][currentFile];
				newState[currentRank][currentFile] = null;
			}
			else if (action.equals(UPLEFT))
			{
				newState[currentRank - 2][currentFile - 1] = newState[currentRank][currentFile];
				newState[currentRank][currentFile] = null;
			}
			else if (action.equals(LEFTUP))
			{
				newState[currentRank - 1][currentFile - 2] = newState[currentRank][currentFile];
				newState[currentRank][currentFile] = null;
			}
			else if (action.equals(LEFTDOWN))
			{
				newState[currentRank + 1][currentFile - 2] = newState[currentRank][currentFile];
				newState[currentRank][currentFile] = null;
			}
			else if (action.equals(RIGHTUP))
			{
				newState[currentRank - 1][currentFile + 2] = newState[currentRank][currentFile];
				newState[currentRank][currentFile] = null;
			}
			else if (action.equals(RIGHTDOWN))
			{
				newState[currentRank + 1][currentFile + 2] = newState[currentRank][currentFile];
				newState[currentRank][currentFile] = null;
			}
			else if (action.equals(DOWNLEFT))
			{
				newState[currentRank + 2][currentFile - 1] = newState[currentRank][currentFile];
				newState[currentRank][currentFile] = null;
			}
			else if (action.equals(DOWNRIGHT))
			{
				newState[currentRank + 2][currentFile + 1] = newState[currentRank][currentFile];
				newState[currentRank][currentFile] = null;
			}
			return newState;
		}

		public int[] getResultLocation(Board.Type[][] oldState, Action action, int row, int column, int count)
		{
			int currentRank = row;
			int currentFile = column;
			int[] result = new int[2];
			if (action.equals(LEFT))
			{
				result[0] = currentRank;
				result[1] = currentFile - count;
			}
			else if (action.equals(RIGHT))
			{
				result[0] = currentRank;
				result[1] = currentFile + count;
			}
			else if (action.equals(UP))
			{
				result[0] = currentRank - count;
				result[1] = currentFile;
			}
			else if (action.equals(DOWN))
			{
				result[0] = currentRank + count;
				result[1] = currentFile;
			}
			else if (action.equals(DIAGUPLEFT))
			{
				result[0] = currentRank - count;
				result[1] = currentFile - count;
			}
			else if (action.equals(DIAGUPRIGHT))
			{
				result[0] = currentRank - count;
				result[1] = currentFile + count;
			}
			else if (action.equals(DIAGDOWNLEFT))
			{
				result[0] = currentRank + count;
				result[1] = currentFile - count;
			}
			else if (action.equals(DIAGDOWNRIGHT))
			{
				result[0] = currentRank + count;
				result[1] = currentFile + count;
			}
			else if (action.equals(UPRIGHT))
			{
				result[0] = currentRank - 2;
				result[1] = currentFile + 1;
			}
			else if (action.equals(UPLEFT))
			{
				result[0] = currentRank - 2;
				result[1] = currentFile - 1;
			}
			else if (action.equals(LEFTUP))
			{
				result[0] = currentRank - 1;
				result[1] = currentFile - 2;
			}
			else if (action.equals(LEFTDOWN))
			{
				result[0] = currentRank + 1;
				result[1] = currentFile - 2;
			}
			else if (action.equals(RIGHTUP))
			{
				result[0] = currentRank - 1;
				result[1] = currentFile + 2;
			}
			else if (action.equals(RIGHTDOWN))
			{
				result[0] = currentRank + 1;
				result[1] = currentFile + 2;
			}
			else if (action.equals(DOWNLEFT))
			{
				result[0] = currentRank + 2;
				result[1] = currentFile - 1;
			}
			else if (action.equals(DOWNRIGHT))
			{
				result[0] = currentRank + 2;
				result[1] = currentFile + 1;
			}
			return result;
		}
	}

	public class Actions
	{
		public Board.Type[][] checkBoard;
		public int row;
		public int column;
		public Board.Type[][] board;

		public synchronized Map<Action, Integer> getLegalActions(Board.Type[][] tiles, Board.Type type, int row, int column) 
		{
			board = tiles;
			this.row = row;
			this.column = column;
			possibleActions = new HashMap<Action, Integer>(); //Holds list of possible actions
			switch (type)
			{
			case B_Bishop:
				checkDiagDownRightMoves(true, 8, 8);
				checkDiagDownLeftMoves(true, 8, 0);
				checkDiagUpLeftMoves(true, 0, 0);
				checkDiagUpRightMoves(true, 0, 8);
				break;
			case B_King:
				int maxRankMoves = row;
				int maxFileMoves = column;
				checkDownMoves(true, maxRankMoves + 2);
				checkUpMoves(true, maxRankMoves - 1);
				checkLeftMoves(true, maxFileMoves - 1);
				checkRightMoves(true, maxFileMoves + 2);
				checkDiagDownRightMoves(true, maxRankMoves + 2, maxFileMoves + 2);
				checkDiagDownLeftMoves(true, maxRankMoves + 2,  maxFileMoves - 1);
				checkDiagUpRightMoves(true, maxRankMoves - 1, maxFileMoves + 2);
				checkDiagUpLeftMoves(true, maxRankMoves - 1, maxFileMoves - 1);
				break;
			case B_Knight:
				checkUpRightMoves(true);
				checkUpLeftMoves(true);
				checkRightUpMoves(true);
				checkRightDownMoves(true);
				checkLeftUpMoves(true);
				checkLeftDownMoves(true);
				checkDownRightMoves(true);
				checkDownLeftMoves(true);
				break;
			case B_Pawn:
				maxRankMoves = row;
				maxFileMoves = column;
				//Check down actions
				if (row == 1)
				{
					checkDownMoves(true, maxRankMoves + 3);
				}
				else
				{
					checkDownMoves(true, maxRankMoves + 2);
				}
				//Check diagonal down right attacks
				if (maxRankMoves + 1 < 8 && maxFileMoves + 1 < 8 && board[maxRankMoves + 1][maxFileMoves + 1] != null && areEnemies(board[row][column], board[maxRankMoves + 1][maxFileMoves + 1]))
				{
					checkDiagDownRightMoves(true, maxRankMoves + 2, maxFileMoves + 2);
				}
				//Check diagonal down left attacks
				if (maxRankMoves + 1 < 8 && maxFileMoves - 1 >= 0 && board[maxRankMoves + 1][maxFileMoves - 1] != null && areEnemies(board[row][column], board[maxRankMoves + 1][maxFileMoves - 1]))
				{
					checkDiagDownLeftMoves(true, maxRankMoves + 2, maxFileMoves - 1);
				}
				break;
			case B_Queen:
				checkDownMoves(true, 8);
				checkUpMoves(true, 0);
				checkLeftMoves(true, 0);
				checkRightMoves(true, 8);
				checkDiagUpLeftMoves(true, 0, 0);
				checkDiagUpRightMoves(true, 0, 8);
				checkDiagDownLeftMoves(true, 8, 0);
				checkDiagDownRightMoves(true, 8, 8);
				break;
			case B_Rook:
				checkUpMoves(true, 0);
				checkDownMoves(true, 8);
				checkLeftMoves(true, 0);
				checkRightMoves(true, 8);
				break;
			case W_Bishop:
				checkDiagUpRightMoves(true, 0, 8);
				checkDiagUpLeftMoves(true, 0, 0);
				checkDiagDownRightMoves(true, 8, 8);
				checkDiagDownLeftMoves(true, 8, 0);
				break;
			case W_King:
				maxRankMoves = row;
				maxFileMoves = column;
				checkDownMoves(true, maxRankMoves + 2);
				checkUpMoves(true, maxRankMoves - 1);
				checkLeftMoves(true, maxFileMoves - 1);
				checkRightMoves(true, maxFileMoves + 2);
				checkDiagDownRightMoves(true, maxRankMoves + 2, maxFileMoves + 2);
				checkDiagDownLeftMoves(true, maxRankMoves + 2,  maxFileMoves - 1);
				checkDiagUpRightMoves(true, maxRankMoves - 1, maxFileMoves + 2);
				checkDiagUpLeftMoves(true, maxRankMoves - 1, maxFileMoves - 1);
				break;
			case W_Knight:
				checkUpRightMoves(true);
				checkUpLeftMoves(true);
				checkRightUpMoves(true);
				checkRightDownMoves(true);
				checkLeftUpMoves(true);
				checkLeftDownMoves(true);
				checkDownRightMoves(true);
				checkDownLeftMoves(true);
				break;
			case W_Pawn:
				maxRankMoves = row;
				maxFileMoves = column;
				//Check down actions
				if (row == 6)
				{
					checkUpMoves(true, maxRankMoves - 2);
				}
				else
				{
					checkUpMoves(true, maxRankMoves - 1);
				}
				//Check diagonal up right attacks
				if (maxRankMoves - 1 >= 0 && maxFileMoves + 1 < 8 && board[maxRankMoves - 1][maxFileMoves + 1] != null && areEnemies(board[row][column], board[maxRankMoves - 1][maxFileMoves + 1]))
				{
					checkDiagUpRightMoves(true, maxRankMoves - 1, maxFileMoves + 2);
				}
				//Check diagonal up left attacks
				if (maxRankMoves - 1 >= 0 && maxFileMoves - 1 >= 0 && board[maxRankMoves - 1][maxFileMoves - 1] != null && areEnemies(board[row][column], board[maxRankMoves - 1][maxFileMoves - 1]))
				{
					checkDiagUpLeftMoves(true, maxRankMoves - 1, maxFileMoves - 1);
				}
				break;
			case W_Queen:
				checkDownMoves(true, 8);
				checkUpMoves(true, 0);
				checkLeftMoves(true, 0);
				checkRightMoves(true, 8);
				checkDiagUpLeftMoves(true, 0, 0);
				checkDiagUpRightMoves(true, 0, 8);
				checkDiagDownLeftMoves(true, 8, 0);
				checkDiagDownRightMoves(true, 8, 8);
				break;
			case W_Rook:
				checkUpMoves(true, 0);
				checkDownMoves(true, 8);
				checkLeftMoves(true, 0);
				checkRightMoves(true, 8);
				break;
			default:
				break;
			}
			return possibleActions; //Return list of possible actions
		}

		public void checkDiagDownRightMoves(boolean legal, int rCount, int fCount)
		{
			int counter = 1;
			int rank = row;
			int file = column;
			//Check diag down right actions
			for (int i = rank + 1, j = file + 1; i < Math.min(8, rCount) && j < Math.min(8, fCount); i++, j++)
			{
				if (board[i][j] == null || areEnemies(board[row][column], board[i][j]))
				{
					checkBoard = legal ? results.getResult(board, DIAGDOWNRIGHT, rank, file, counter) : null;
					if (!legal || !causalCheck(checkBoard, board[rank][file]))
					{
						possibleActions.put(DIAGDOWNRIGHT, counter);
						if (board[i][j] != null && areEnemies(board[row][column], board[i][j]))
						{
							break;
						}
					}
					counter++;
				}
				else break;
			}
		}

		public void checkDiagUpRightMoves(boolean legal, int rCount, int fCount)
		{
			int counter = 1;
			int rank = row;
			int file = column;
			//Check diag up right actions
			for (int i = rank - 1, j = file + 1; i >= Math.max(0, rCount) && j < Math.min(8, fCount); i--, j++)
			{
				if (board[i][j] == null || areEnemies(board[row][column], board[i][j]))
				{
					checkBoard = legal ? results.getResult(board, DIAGUPRIGHT, rank, file, counter) : null;
					if (!legal || !causalCheck(checkBoard, board[rank][file]))
					{
						possibleActions.put(DIAGUPRIGHT, counter);
						if (board[i][j] != null && areEnemies(board[row][column], board[i][j]))
						{
							break;
						}
					}
					counter++;
				}
				else break;
			}
		}

		public void checkDiagDownLeftMoves(boolean legal, int rCount, int fCount)
		{
			int counter = 1;
			int rank = row;
			int file = column;
			//Check diag down left actions
			for (int i = rank + 1, j = file - 1; i < Math.min(8, rCount) && j >= Math.max(0, fCount); i++, j--)
			{
				if (board[i][j] == null || areEnemies(board[row][column], board[i][j]))
				{
					checkBoard = legal ? results.getResult(board, DIAGDOWNLEFT, rank, file, counter) : null;
					if (!legal || !causalCheck(checkBoard, board[rank][file]))
					{
						possibleActions.put(DIAGDOWNLEFT, counter);
						if (board[i][j] != null && areEnemies(board[row][column], board[i][j]))
						{
							break;
						}
					}
					counter++;
				}
				else break;
			}
		}

		public void checkDiagUpLeftMoves(boolean legal, int rCount, int fCount)
		{
			int counter = 1;
			int rank = row;
			int file = column;
			//Check diag up left actions
			for (int i = rank - 1, j = file - 1; i >= Math.max(0, rCount) && j >= Math.max(0, fCount); i--, j--)
			{
				if (board[i][j] == null || areEnemies(board[row][column], board[i][j]))
				{
					checkBoard = legal ? results.getResult(board, DIAGUPLEFT, rank, file, counter) : null;
					if (!legal || !causalCheck(checkBoard, board[rank][file]))
					{
						possibleActions.put(DIAGUPLEFT, counter);
						if (board[i][j] != null && areEnemies(board[row][column], board[i][j]))
						{
							break;
						}
					}
					counter++;
				}
				else break;
			}
		}

		public void checkRightMoves(boolean legal, int count)
		{
			int counter = 1;
			int rank = row;
			int file = column;
			//Check right actions
			for (int i = file + 1; i < Math.min(8, count); i++)
			{
				if (board[rank][i] == null || areEnemies(board[row][column], board[rank][i]))
				{
					checkBoard = legal ? results.getResult(board, RIGHT, rank, file, counter) : null;
					if (!legal || !causalCheck(checkBoard, board[rank][file]))
					{
						possibleActions.put(RIGHT, counter);
						if (board[rank][i] != null && areEnemies(board[row][column], board[rank][i]))
						{
							break;
						}
					}
					counter++;
				}
				else break;
			}
		}


		public void checkLeftMoves(boolean legal, int count)
		{
			int counter = 1;
			int rank = row;
			int file = column;
			//Check left actions
			for (int i = file - 1; i >= Math.max(0, count); i--)
			{
				if (board[rank][i] == null || areEnemies(board[row][column], board[rank][i]))
				{
					checkBoard = legal ? results.getResult(board, LEFT, rank, file, counter) : null;
					if (!legal || !causalCheck(checkBoard, board[rank][file]))
					{
						possibleActions.put(LEFT, counter);
						if (board[rank][i] != null && areEnemies(board[row][column], board[rank][i]))
						{
							break;
						}
					}
					counter++;
				}
				else break;
			}
		}

		public void checkDownMoves(boolean legal, int count)
		{
			int counter = 1;
			int rank = row;
			int file = column;
			//Check down actions
			for (int i = rank + 1; i < Math.min(8, count); i++)
			{
				if (board[i][file] == null || (areEnemies(board[row][column], board[i][file]) && !board[rank][file].equals(Board.Type.B_Pawn) && !board[rank][file].equals(Board.Type.W_Pawn)))
				{
					checkBoard = legal ? results.getResult(board, DOWN, rank, file, counter) : null;
					if (!legal || !causalCheck(checkBoard, board[rank][file]))
					{
						possibleActions.put(DOWN, counter);
						if (board[i][file] != null && areEnemies(board[row][column], board[i][file]))
						{
							break;
						}
					}
					counter++;
				}
				else break;
			}
		}

		public void checkUpMoves(boolean legal, int count)
		{
			int counter = 1;
			int rank = row;
			int file = column;
			//Check up actions
			for (int i = rank - 1; i >= Math.max(0, count); i--)
			{
				if (board[i][file] == null || (areEnemies(board[row][column], board[i][file]) && !board[rank][file].equals(Board.Type.B_Pawn) && !board[rank][file].equals(Board.Type.W_Pawn)))
				{
					checkBoard = legal ? results.getResult(board, UP, rank, file, counter) : null;
					if (!legal || !causalCheck(checkBoard, board[rank][file]))
					{
						possibleActions.put(UP, counter);
						if (board[i][file] != null && areEnemies(board[row][column], board[i][file]))
						{
							break;
						}
					}
					counter++;
				}
				else break;
			}
		}

		public void checkUpRightMoves(boolean legal)
		{
			int counter = 1;
			int rank = row;
			int file = column;
			//Check up right actions
			if (rank - 2 >= 0 && file + 1 < 8)
			{
				if (board[rank - 2][file + 1] == null || areEnemies(board[row][column], board[rank - 2][file + 1]))
				{
					checkBoard = legal ? results.getResult(board, UPRIGHT, rank, file, counter) : null;
					if (!legal || !causalCheck(checkBoard, board[rank][file]))
					{
						possibleActions.put(UPRIGHT, counter);
					}
				}
			}
		}

		public void checkUpLeftMoves(boolean legal)
		{
			int counter = 1;
			int rank = row;
			int file = column;
			//Check up left actions
			if (rank - 2 >= 0 && file - 1 >= 0)
			{
				if (board[rank - 2][file - 1] == null || areEnemies(board[row][column], board[rank - 2][file - 1]))
				{
					checkBoard = legal ? results.getResult(board, UPLEFT, rank, file, counter) : null;
					if (!legal || !causalCheck(checkBoard, board[rank][file]))
					{
						possibleActions.put(UPLEFT, counter);
					}
				}
			}
		}

		public void checkLeftUpMoves(boolean legal)
		{
			int counter = 1;
			int rank = row;
			int file = column;
			//Check left up actions
			if (rank - 1 >= 0 && file - 2 >= 0)
			{
				if (board[rank - 1][file - 2] == null || areEnemies(board[row][column], board[rank - 1][file - 2]))
				{
					checkBoard = legal ? results.getResult(board, LEFTUP, rank, file, counter) : null;
					if (!legal || !causalCheck(checkBoard, board[rank][file]))
					{
						possibleActions.put(LEFTUP, counter);
					}
				}
			}
		}

		public void checkLeftDownMoves(boolean legal)
		{
			int counter = 1;
			int rank = row;
			int file = column;
			//Check left down actions
			if (rank + 1 < 8 && file - 2 >= 0)
			{
				if (board[rank + 1][file - 2] == null || areEnemies(board[row][column], board[rank + 1][file - 2]))
				{
					checkBoard = legal ? results.getResult(board, LEFTDOWN, rank, file, counter) : null;
					if (!legal || !causalCheck(checkBoard, board[rank][file]))
					{
						possibleActions.put(LEFTDOWN, counter);
					}
				}
			}
		}

		public void checkRightUpMoves(boolean legal)
		{
			int counter = 1;
			int rank = row;
			int file = column;
			//Check right up actions
			if (rank - 1 >= 0 && file + 2 < 8)
			{
				if (board[rank - 1][file + 2] == null || areEnemies(board[row][column], board[rank - 1][file + 2]))
				{
					checkBoard = legal ? results.getResult(board, RIGHTUP, rank, file, counter) : null;
					if (!legal || !causalCheck(checkBoard, board[rank][file]))
					{
						possibleActions.put(RIGHTUP, counter);
					}
				}
			}
		}

		public void checkRightDownMoves(boolean legal)
		{
			int counter = 1;
			int rank = row;
			int file = column;
			//Check right down actions
			if (rank + 1 < 8 && file + 2 < 8)
			{
				if (board[rank + 1][file + 2] == null || areEnemies(board[row][column], board[rank + 1][file + 2]))
				{
					checkBoard = legal ? results.getResult(board, RIGHTDOWN, rank, file, counter) : null;
					if (!legal || !causalCheck(checkBoard, board[rank][file]))
					{
						possibleActions.put(RIGHTDOWN, counter);
					}
				}
			}
		}

		public void checkDownRightMoves(boolean legal)
		{
			int counter = 1;
			int rank = row;
			int file = column;
			//Check down right actions
			if (rank + 2 < 8 && file + 1 < 8)
			{
				if (board[rank + 2][file + 1] == null || areEnemies(board[row][column], board[rank + 2][file + 1]))
				{
					checkBoard = legal ? results.getResult(board, DOWNRIGHT, rank, file, counter) : null;
					if (!legal || !causalCheck(checkBoard, board[rank][file]))
					{
						possibleActions.put(DOWNRIGHT, counter);
					}
				}
			}
		}

		public void checkDownLeftMoves(boolean legal)
		{
			int counter = 1;
			int rank = row;
			int file = column;
			//Check down left actions
			if (rank + 2 < 8 && file - 1 >= 0)
			{
				if (board[rank + 2][file - 1] == null || areEnemies(board[row][column], board[rank + 2][file - 1]))
				{
					checkBoard = legal ? results.getResult(board, DOWNLEFT, rank, file, counter) : null;
					if (!legal || !causalCheck(checkBoard, board[rank][file]))
					{
						possibleActions.put(DOWNLEFT, counter);
					}
				}
			}
		}
	}

	public class DepthFirstAlphaBetaSearch
	{

		protected Chess game = new Chess();
		private int expandedNodes;
		private boolean searchRegularly = true;
		private boolean exit;
		private int maxDepth;
		private int currentDepthLimit;
		private int maxTime;
		private long startTime;
		private Map<PVNode, Double> PVNodeList = new HashMap<PVNode, Double>();
		private PVNode PVNode = null;
		private double newResultValue = Double.NEGATIVE_INFINITY;
		private int[] result = new int[4];
		private double alpha = Double.NEGATIVE_INFINITY;
		private double beta = Double.POSITIVE_INFINITY;
		private List<Thread> threadList = new ArrayList<Thread>();

		public DepthFirstAlphaBetaSearch()
		{
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private Map sortByComparator(Map unsortMap) {

			List list = new LinkedList(unsortMap.entrySet());

			// sort list based on comparator
			Collections.sort(list, new Comparator() {
				public int compare(Object o1, Object o2) {
					return ((Comparable) ((Map.Entry) (o2)).getValue())
							.compareTo(((Map.Entry) (o1)).getValue());
				}
			});

			// put sorted list into map again
			//LinkedHashMap make sure order in which keys were inserted
			Map sortedMap = new LinkedHashMap();
			for (Iterator it = list.iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				sortedMap.put(entry.getKey(), entry.getValue());
			}
			return sortedMap;
		}

		@SuppressWarnings("unchecked")
		public int[] makeDecision(Board.Type[][] state, int m, int t)
		{
			char player = 'B';
			//Set current depth limit
			currentDepthLimit = 0;
			//Restart exit
			exit = false;
			//Restart nodes
			expandedNodes = 0;
			//Set max depth and time
			maxDepth = m;
			maxTime = t * 1000;
			//Get current start time
			startTime = System.currentTimeMillis();
			newResultValue = Double.NEGATIVE_INFINITY;
			boolean search = false;
			do
			{
				//Reset alpha and new result value to prevent AI from ignoring deep searches only if new depth includes
				//Opponent choosing a min decision
				alpha = Double.NEGATIVE_INFINITY;
				newResultValue = Double.NEGATIVE_INFINITY;
				//Increment current depth limit each iteration
				currentDepthLimit++;
				//Sort PVNodeList
				PVNodeList = sortByComparator(PVNodeList);
				//If user wants to order moves and only the first iteration was complete
				if (superBoard.orderMoves.isSelected() && PVNodeList.size() > 0)
				{
					int branchingFactor = (int) (PVNodeList.size() * (Double) superBoard.branchingFactor.getSelectedItem());
					for (PVNode node : PVNodeList.keySet())
					{
						branchingFactor--;
						if (superBoard.multiThread.isSelected())
						{
							currentDepthLimit = maxDepth;
							Thread thread = new Thread(new minValue(player, alpha, beta, 1, node.state, node.action, node.i, node.j, node.k));
							threadList.add(thread);
						}
						else
						{
							double value = minValue(game.getResult(node.state, node.action, node.i, node.j, node.k), player, alpha, beta, 1);
							if (currentDepthLimit >= maxDepth || System.currentTimeMillis() > startTime + maxTime) exit = true;
							if (value > newResultValue)
							{
								result[0] = node.i;
								result[1] = node.j;
								int[] tempArray = results.getResultLocation(node.state, node.action, node.i, node.j, node.k);
								result[2] = tempArray[0];
								result[3] = tempArray[1];
								newResultValue = value;
								alpha = value;
							}
						}
						if (branchingFactor == 0) break;
					}
					searchRegularly = false;
				}
				//If there are no nodes in PVNodeList
				if (searchRegularly)
				{
					//Search PVNode first if available
					if (PVNode != null && search == true && superBoard.usingPVNode.isSelected())
					{
						int tempHolder = currentDepthLimit;
						//Extend PV range if allowed
						if (superBoard.pvRange.isSelected())
						{
							currentDepthLimit = maxDepth;
						}
						if (superBoard.multiThread.isSelected())
						{
							currentDepthLimit = maxDepth;
							Thread thread = new Thread(new minValue(player, alpha, beta, 1, PVNode.state, PVNode.action, PVNode.i, PVNode.j, PVNode.k));
							threadList.add(thread);
						}
						else
						{
							double value = minValue(game.getResult(PVNode.state, PVNode.action, PVNode.i, PVNode.j, PVNode.k), player, alpha, beta, 1);
							if (value > newResultValue)
							{
								result[0] = PVNode.i;
								result[1] = PVNode.j;
								int[] tempArray = results.getResultLocation(PVNode.state, PVNode.action, PVNode.i, PVNode.j, PVNode.k);
								result[2] = tempArray[0];
								result[3] = tempArray[1];
								newResultValue = value;
								alpha = value;
							}
						}
						currentDepthLimit = tempHolder;
						search = false;
					}
					//Iterate rows
					for (int i = 0; i < 8; i++)
					{
						//Iterate columns
						outerloop:
						for (int j = 0; j < 8; j++)
						{
							if (state[i][j] != null && state[i][j].toString().charAt(0) == player) 
							{
								if (superBoard.queenMidGame.isSelected() && movesMade < 10 && state[i][j].equals(Board.Type.B_Queen)) continue outerloop;
								HashMap<Action, Integer> searchHolder = (HashMap<Action, Integer>) actions.getLegalActions(state, state[i][j], i, j);
								innerloop:
									for (Action action : searchHolder.keySet())
									{
										for (int k = 1; k <= searchHolder.get(action); k++)
										{
											if (superBoard.usingPVNode.isSelected() && PVNode != null && state == PVNode.state && action == PVNode.action && i == PVNode.i && j == PVNode.j && k == PVNode.k)
											{
												continue innerloop;
											}
											//Check if global alpha is checked
											if (!superBoard.globalAlpha.isSelected()) alpha = Double.NEGATIVE_INFINITY;
											//Check if they want multithreading
											if (superBoard.multiThread.isSelected())
											{
												currentDepthLimit = maxDepth;
												Thread thread = new Thread(new minValue(player, alpha, beta, 1, state, action, i, j, k));
												threadList.add(thread);
												thread.start();
												if (currentDepthLimit >= maxDepth || System.currentTimeMillis() > startTime + maxTime) exit = true;
											}
											else
											{
												double value = minValue(game.getResult(state, action, i, j, k), player, alpha, beta, 1);
												PVNode pv = new PVNode(state, action, i, j, k);
												PVNodeList.put(pv, value);
												if (currentDepthLimit >= maxDepth || System.currentTimeMillis() > startTime + maxTime) exit = true;
												if (value > newResultValue)
												{
													PVNode = new PVNode(state, action, i, j, k);
													search = true;
													result[0] = i;
													result[1] = j;
													int[] tempArray = results.getResultLocation(state, action, i, j, k);
													result[2] = tempArray[0];
													result[3] = tempArray[1];
													newResultValue = value;
													alpha = value;
												} 
											}
										}
									}
							}
						}
					}
					//Turn off multithreading while decisions are still being made
					superBoard.multiThread.setEnabled(false);
				}
			}while (!exit);
			PVNodeList.clear();
			searchRegularly = true;
			if (superBoard.multiThread.isSelected())
			{
				for (Thread thread : threadList)
				{
					try {
						thread.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			//Turn multithreading back on
			superBoard.multiThread.setEnabled(true);
			superBoard.expandedNodes.setText("Nodes: " + expandedNodes);
			return result;
		}

		public double maxValue(Board.Type[][] state, char player, double alpha, double beta, int depth)
		{ // returns an utility value
			expandedNodes++;
			if (game.isTerminal(state, player) || depth >= currentDepthLimit || System.currentTimeMillis() > startTime + maxTime) {
				return eval(state, player);
			} else {
				if (player == 'W') player = 'B';
				else player = 'W';
				double value = Double.NEGATIVE_INFINITY;
				//Iterate rows
				for (int i = 0; i < 8; i++)
				{
					//Iterate columns
					for (int j = 0; j < 8; j++)
					{
						//Validate player
						if (state[i][j] != null && state[i][j].toString().charAt(0) == player)
						{
							//Get actions
							HashMap<Action, Integer> searchHolder = (HashMap<Action, Integer>) actions.getLegalActions(state, state[i][j], i, j);
							//Iterate through actions
							for (Action action : searchHolder.keySet())
							{
								//Iterate through repeated actions
								for (int k = 1; k <= searchHolder.get(action); k++)
								{
									value = Math.max(value, minValue(game.getResult(state, action, i, j, k), player, alpha, beta, depth + 1));
									if (value >= beta)
									{
										return beta;
									}
									alpha = Math.max(alpha, value);
								}
							}
						}
					}
				}
				return alpha;
			}
		}

		public double minValue(Board.Type[][] state, char player, double alpha, double beta, int depth)
		{ // returns an utility
			expandedNodes++;
			if (game.isTerminal(state, player) || depth >= currentDepthLimit || System.currentTimeMillis() > startTime + maxTime) {
				return eval(state, player);
			} else {
				if (player == 'W') player = 'B';
				else player = 'W';
				double value = Double.POSITIVE_INFINITY;
				//Iterate rows
				for (int i = 0; i < 8; i++)
				{
					//Iterate columns
					for (int j = 0; j < 8; j++)
					{
						if (state[i][j] != null && state[i][j].toString().charAt(0) == player)
						{
							HashMap<Action, Integer> searchHolder = (HashMap<Action, Integer>) actions.getLegalActions(state, state[i][j], i, j);
							for (Action action : searchHolder.keySet())
							{
								for (int k = 1; k <= searchHolder.get(action); k++)
								{
									value = Math.min(value, maxValue(game.getResult(state, action, i, j, k), player, alpha, beta, depth + 1));
									if (value <= alpha)
									{
										return value;
									}
									beta = Math.min(beta, value);
								}
							}
						}
					}
				}
				return beta;
			}
		}

		public Metrics getMetrics() {
			Metrics result = new Metrics();
			result.set("expandedNodes", expandedNodes);
			result.set("maxDepth", maxDepth);
			return result;
		}

		protected synchronized double eval(Board.Type[][] state, char player)
		{
			return game.getUtility(state, player);
		}

		public void updateValue(double value, Board.Type[][] state, Action action, int i, int j, int k) throws InterruptedException
		{
			lock.acquire();
			if (value > newResultValue)
			{
				result[0] = i;
				result[1] = j;
				int[] tempArray = results.getResultLocation(state, action, i, j, k);
				result[2] = tempArray[0];
				result[3] = tempArray[1];
				newResultValue = value;
				alpha = value;
			} 
			lock.release();
		}

		public class minValue extends Thread
		{
			Chess game = new Chess();
			Board.Type[][] state, state2;
			Action action;
			char player;
			int depth, i, j, k;

			public minValue(char player, double alpha, double beta, int depth, Board.Type[][] state2, Action action, int i, int j, int k)
			{
				this.state = game.getResult(state2, action, i, j, k);
				this.player = player;
				this.depth = depth;
				this.state2 = state2;
				this.action = action;
				this.i = i;
				this.j = j;
				this.k = k;
			}

			public void run()
			{
				double value = getMinValue(state, player, alpha, beta, depth);
				try {
					updateValue(value, state2, action, i, j, k);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			public double getMaxValue(Board.Type[][] state, char player, double alpha, double beta, int depth)
			{ // returns an utility value
				expandedNodes++;
				if (game.isTerminal(state, player) || depth >= currentDepthLimit || System.currentTimeMillis() > startTime + maxTime) {
					return eval(state, player);
				} else {
					if (player == 'W') player = 'B';
					else player = 'W';
					double value = Double.NEGATIVE_INFINITY;
					//Iterate rows
					for (int i = 0; i < 8; i++)
					{
						//Iterate columns
						for (int j = 0; j < 8; j++)
						{
							//Validate player
							if (state[i][j] != null && state[i][j].toString().charAt(0) == player)
							{
								//Get actions
								HashMap<Action, Integer> searchHolder = (HashMap<Action, Integer>) actions.getLegalActions(state, state[i][j], i, j);
								//Iterate through actions
								for (Action action : searchHolder.keySet())
								{
									//Iterate through repeated actions
									for (int k = 1; k <= searchHolder.get(action); k++)
									{
										value = Math.max(value, getMinValue(game.getResult(state, action, i, j, k), player, alpha, beta, depth + 1));
										if (value >= beta)
										{
											return beta;
										}
										alpha = Math.max(alpha, value);
									}
								}
							}
						}
					}
					return alpha;
				}
			}

			public double getMinValue(Board.Type[][] state, char player, double alpha, double beta, int depth)
			{ // returns an utility
				expandedNodes++;
				if (game.isTerminal(state, player) || depth >= currentDepthLimit || System.currentTimeMillis() > startTime + maxTime) {
					return eval(state, player);
				} else {
					if (player == 'W') player = 'B';
					else player = 'W';
					double value = Double.POSITIVE_INFINITY;
					//Iterate rows
					for (int i = 0; i < 8; i++)
					{
						//Iterate columns
						for (int j = 0; j < 8; j++)
						{
							if (state[i][j] != null && state[i][j].toString().charAt(0) == player)
							{
								HashMap<Action, Integer> searchHolder = (HashMap<Action, Integer>) actions.getLegalActions(state, state[i][j], i, j);
								for (Action action : searchHolder.keySet())
								{
									for (int k = 1; k <= searchHolder.get(action); k++)
									{
										value = Math.min(value, getMaxValue(game.getResult(state, action, i, j, k), player, alpha, beta, depth + 1));
										if (value <= alpha)
										{
											return value;
										}
										beta = Math.min(beta, value);
									}
								}
							}
						}
					}
					return beta;
				}
			}
		}

		public class Chess
		{
			PieceSquareTable pst = new PieceSquareTable();
			public Map<Action, Integer> getActions(Piece piece) {
				return piece.getLegalActions(true);
			}


			public Object getInitialState() {
				// TODO Auto-generated method stub
				return null;
			}


			public Object getPlayer(Object arg0) {
				Board board = (Board) arg0;
				return board.getTurn();
			}


			public Object[] getPlayers() {
				// TODO Auto-generated method stub
				return null;
			}


			public Board.Type[][] getResult(Board.Type[][] board, Action action, int row, int column, int count) {
				return results.getResult(board, action, row, column, count);
			}

			public boolean isLonePiece(Board.Type[][] state, Board.Type type)
			{
				int count = 0;
				for (int i = 0; i < 8; i++)
				{
					for (int j = 0; j < 8; j++)
					{
						if (state[i][j] != null && state[i][j].equals(type)) count++;
					}
				}
				return count == 1;
			}


			public synchronized double getUtility(Board.Type[][] state, char player) {
				int utility = 0;
				//Get piece values and movement values
				for (int i = 0; i < 8; i++)
				{
					for (int j = 0; j < 8; j++)
					{
						if (state[i][j] != null)
						{
							//Base cost
							switch (state[i][j])
							{
							case B_Bishop:
								utility += Integer.parseInt(superBoard.bishopPiece.getText());
								if (superBoard.saveLonePiece.isSelected() && isLonePiece(state, state[i][j]))
								{
									utility += (Integer.parseInt(superBoard.bishopPiece.getText()) * 10);
								}
								break;
							case B_King:
								utility += Integer.parseInt(superBoard.kingPiece.getText());
								break;
							case B_Knight:
								utility += Integer.parseInt(superBoard.knightPiece.getText());
								if (superBoard.saveLonePiece.isSelected() && isLonePiece(state, state[i][j]))
								{
									utility += (Integer.parseInt(superBoard.knightPiece.getText()) * 10);
								}
								break;
							case B_Pawn:
								utility += Integer.parseInt(superBoard.pawnPiece.getText());
								break;
							case B_Queen:
								utility += Integer.parseInt(superBoard.queenPiece.getText());
								break;
							case B_Rook:
								utility += Integer.parseInt(superBoard.rookPiece.getText());
								if (superBoard.saveLonePiece.isSelected() && isLonePiece(state, state[i][j]))
								{
									utility += (Integer.parseInt(superBoard.rookPiece.getText()) * 10);
								}
								break;
							case W_Bishop:
								utility -= Integer.parseInt(superBoard.bishopPiece.getText());
								break;
							case W_King:
								utility -= Integer.parseInt(superBoard.kingPiece.getText());
								break;
							case W_Knight:
								utility -= Integer.parseInt(superBoard.knightPiece.getText());
								break;
							case W_Pawn:
								utility -= Integer.parseInt(superBoard.pawnPiece.getText());
								break;
							case W_Queen:
								utility -= Integer.parseInt(superBoard.queenPiece.getText());
								break;
							case W_Rook:
								utility -= Integer.parseInt(superBoard.rookPiece.getText());
								break;
							default:
								break;
							}
							//Move's cost
							for (Integer k : actions.getLegalActions(state, state[i][j], i, j).values())
							{
								if (player == 'B') utility += (10 * k);
								else utility += (-10 * k);
							}
							//PVT Table, if enabled
							if (superBoard.usePVTTable.isSelected())
							{
								utility += pst.evaluate(state[i][j], i, j);
							}
						}
					}
				}
				//Check for checkmate
				if (player == 'B' && isCheckMate(state, 'W'))
				{
					utility += 200000;
				}
				else if (player == 'W' && isCheckMate(state, 'B'))
				{
					utility += -200000;
				}
				return utility;
			}


			public boolean isTerminal(Board.Type[][] arg0, char p) {
				// TODO Auto-generated method stub
				char player = p == 'W' ? 'B' : 'W';
				return isCheckMate(arg0, player);
			}
		}

		public class PieceSquareTable
		{
			int[] pawnTable = new int[]{
					0,  0,  0,  0,  0,  0,  0,  0,
					50, 50, 50, 50, 50, 50, 50, 50,
					10, 10, 20, 30, 30, 20, 10, 10,
					5,  5, 10, 25, 25, 10,  5,  5,
					0,  0,  0, 20, 20,  0,  0,  0,
					5, -5,-10,  0,  0,-10, -5,  5,
					5, 10, 10,-20,-20, 10, 10,  5,
					0,  0,  0,  0,  0,  0,  0,  0
			};
			int[] pawnOpenerTable = new int[]{
					0,  0,  0,  0,  0,  0,  0,  0,
					50, 50, 50, 50, 50, 50, 50, 50,
					10, 10, 20, 30, 30, 20, 10, 10,
					5,  5, 10, 25, 25, 10,  5,  5,
					500, 500, 500, 500, 500, 500, 500, 500,
					500,  500, 500, 500, 500, 500,  500,  500,
					5, 10, 10,-20,-20, 10, 10,  5,
					0,  0,  0,  0,  0,  0,  0,  0
			};
			int[] knightTable = new int[]{
					-50,-40,-30,-30,-30,-30,-40,-50,
					-40,-20,  0,  0,  0,  0,-20,-40,
					-30,  0, 10, 15, 15, 10,  0,-30,
					-30,  5, 15, 20, 20, 15,  5,-30,
					-30,  0, 15, 20, 20, 15,  0,-30,
					-30,  5, 10, 15, 15, 10,  5,-30,
					-40,-20,  0,  5,  5,  0,-20,-40,
					-50,-40,-30,-30,-30,-30,-40,-50
			};
			int[] bishopTable = new int[]{
					-20,-10,-10,-10,-10,-10,-10,-20,
					-10,  0,  0,  0,  0,  0,  0,-10,
					-10,  0,  5, 10, 10,  5,  0,-10,
					-10,  5,  5, 10, 10,  5,  5,-10,
					-10,  0, 10, 10, 10, 10,  0,-10,
					-10, 10, 10, 10, 10, 10, 10,-10,
					-10,  5,  0,  0,  0,  0,  5,-10,
					-20,-10,-10,-10,-10,-10,-10,-20
			};
			int[] rookTable = new int[]{
					0,  0,  0,  0,  0,  0,  0,  0,
					5, 10, 10, 10, 10, 10, 10,  5,
					-5,  0,  0,  0,  0,  0,  0, -5,
					-5,  0,  0,  0,  0,  0,  0, -5,
					-5,  0,  0,  0,  0,  0,  0, -5,
					-5,  0,  0,  0,  0,  0,  0, -5,
					-5,  0,  0,  0,  0,  0,  0, -5,
					0,  0,  0,  5,  5,  0,  0,  0
			};
			int[] kingTable = new int[]{
					-30,-40,-40,-50,-50,-40,-40,-30,
					-30,-40,-40,-50,-50,-40,-40,-30,
					-30,-40,-40,-50,-50,-40,-40,-30,
					-30,-40,-40,-50,-50,-40,-40,-30,
					-20,-30,-30,-40,-40,-30,-30,-20,
					-10,-20,-20,-20,-20,-20,-20,-10,
					20, 20,  0,  0,  0,  0, 20, 20,
					20, 30, 10,  0,  0, 10, 30, 20
			};
			int[] kingEndGameTable = new int[]{
					-50,-40,-30,-20,-20,-30,-40,-50,
					-30,-20,-10,  0,  0,-10,-20,-30,
					-30,-10, 20, 30, 30, 20,-10,-30,
					-30,-10, 30, 40, 40, 30,-10,-30,
					-30,-10, 30, 40, 40, 30,-10,-30,
					-30,-10, 20, 30, 30, 20,-10,-30,
					-30,-30,  0,  0,  0,  0,-30,-30,
					-50,-30,-30,-30,-30,-30,-30,-50
			};
			int[] queenTable = new int[]{
					-20,-10,-10, -5, -5,-10,-10,-20,
					-10,  0,  0,  0,  0,  0,  0,-10,
					-10,  0,  5,  5,  5,  5,  0,-10,
					-5,  0,  5,  5,  5,  5,  0, -5,
					0,  0,  5,  5,  5,  5,  0, -5,
					-10,  5,  5,  5,  5,  5,  0,-10,
					-10,  0,  5,  0,  0,  0,  0,-10,
					-20,-10,-10, -5, -5,-10,-10,-20
			};
			public int evaluate(Board.Type type, int row, int column)
			{
				switch (type)
				{
				case B_Bishop:
					return bishopTable[((7-row) * 8) + column];
				case B_King:
					if (superBoard.isBlackEndGame() && superBoard.kingEndGame.isSelected()) return kingEndGameTable[((7-row) * 8) + column];
					else return kingTable[((7-row) * 8) + column];
				case B_Knight:
					return knightTable[((7-row) * 8) + column];
				case B_Pawn:
					if (superBoard.pawnsDefense.isSelected()) return pawnOpenerTable[((7-row) * 8) + column];
					else return pawnTable[((7-row) * 8) + column];
				case B_Queen:
					return queenTable[((7-row) * 8) + column];
				case B_Rook:
					return rookTable[((7-row) * 8) + column];
				case W_Bishop:
					return bishopTable[((row) * 8) + column] * -1;
				case W_King:
					if (superBoard.isWhiteEndGame()) return kingEndGameTable[((row) * 8) + column] * -1;
					else return kingTable[((row) * 8) + column] * -1;
				case W_Knight:
					return knightTable[((row) * 8) + column] * -1;
				case W_Pawn:
					return pawnTable[((row) * 8) + column] * -1;
				case W_Queen:
					return queenTable[((row) * 8) + column] * -1;
				case W_Rook:
					return rookTable[((row) * 8) + column] * -1;
				default:
					break;
				}
				return -1;
			}
		}

		public class PVNode
		{
			int i,j,k;
			Action action;
			Board.Type[][] state;

			public PVNode(Board.Type[][] state, Action action, int i, int j, int k)
			{
				this.i = i;
				this.j = j;
				this.k = k;
				this.state = state;
				this.action = action;
			}
		}
	}
}