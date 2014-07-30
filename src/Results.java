import aima.core.agent.Action;
import aima.core.agent.impl.DynamicAction;

//Returns a state with some action applied to some previous state
public class Results
{
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

	public Board getResult(Object tempState, Action action, Piece pieceToCopy, int count)
	{
		int currentRank = pieceToCopy.getCurrentLocation().getArrayRowLocation();
		int currentFile = pieceToCopy.getCurrentLocation().getArrayColumnLocation();
		Tile newTile;
		Board newBoard = new Board((Board) tempState);
		Piece piece = newBoard.getAllTiles()[currentRank][currentFile].getCurrentPiece();
		for (int i = 1; i <= count; i++)
		{
			if (action.equals(LEFT))
			{
				newTile = newBoard.getAllTiles()[currentRank][currentFile - i];
				newBoard.fakeMove(piece, newTile);
			}
			else if (action.equals(RIGHT))
			{
				newTile = newBoard.getAllTiles()[currentRank][currentFile + i];
				newBoard.fakeMove(piece, newTile);
			}
			else if (action.equals(UP))
			{
				newTile = newBoard.getAllTiles()[currentRank - i][currentFile];
				newBoard.fakeMove(piece, newTile);
			}
			else if (action.equals(DOWN))
			{
				newTile = newBoard.getAllTiles()[currentRank + i][currentFile];
				newBoard.fakeMove(piece, newTile);
			}
			else if (action.equals(DIAGUPLEFT))
			{
				newTile = newBoard.getAllTiles()[currentRank - i][currentFile - i];
				newBoard.fakeMove(piece, newTile);
			}
			else if (action.equals(DIAGUPRIGHT))
			{
				newTile = newBoard.getAllTiles()[currentRank - i][currentFile + i];
				newBoard.fakeMove(piece, newTile);
			}
			else if (action.equals(DIAGDOWNLEFT))
			{
				newTile = newBoard.getAllTiles()[currentRank + i][currentFile - i];
				newBoard.fakeMove(piece, newTile);
			}
			else if (action.equals(DIAGDOWNRIGHT))
			{
				newTile = newBoard.getAllTiles()[currentRank + i][currentFile + i];
				newBoard.fakeMove(piece, newTile);
			}
			else if (action.equals(UPRIGHT))
			{
				newTile = newBoard.getAllTiles()[currentRank - 2][currentFile + 1];
				newBoard.fakeMove(piece, newTile);
			}
			else if (action.equals(UPLEFT))
			{
				newTile = newBoard.getAllTiles()[currentRank - 2][currentFile - 1];
				newBoard.fakeMove(piece, newTile);
			}
			else if (action.equals(LEFTUP))
			{
				newTile = newBoard.getAllTiles()[currentRank - 1][currentFile - 2];
				newBoard.fakeMove(piece, newTile);
			}
			else if (action.equals(LEFTDOWN))
			{
				newTile = newBoard.getAllTiles()[currentRank + 1][currentFile - 2];
				newBoard.fakeMove(piece, newTile);
			}
			else if (action.equals(RIGHTUP))
			{
				newTile = newBoard.getAllTiles()[currentRank - 1][currentFile + 2];
				newBoard.fakeMove(piece, newTile);
			}
			else if (action.equals(RIGHTDOWN))
			{
				newTile = newBoard.getAllTiles()[currentRank + 1][currentFile + 2];
				newBoard.fakeMove(piece, newTile);
			}
			else if (action.equals(DOWNLEFT))
			{
				newTile = newBoard.getAllTiles()[currentRank + 2][currentFile - 1];
				newBoard.fakeMove(piece, newTile);
			}
			else if (action.equals(DOWNRIGHT))
			{
				newTile = newBoard.getAllTiles()[currentRank + 2][currentFile + 1];
				newBoard.fakeMove(piece, newTile);
			}
		}
		return newBoard;
	}
}