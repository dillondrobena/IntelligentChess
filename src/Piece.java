import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.event.MouseInputAdapter;

import net.sf.image4j.codec.ico.ICODecoder;
import aima.core.agent.Action;
import aima.core.agent.impl.DynamicAction;


public class Piece {

	List<BufferedImage> image;
	JLabel piece;
	Tile currentLocation;
	Board.Type type;
	boolean pawnInit = true;
	int value = 0;
	int id = 0;

	public Piece(Piece p)
	{
		type = p.getType();
		pawnInit = p.pawnInit;
		value = p.value;
		id = p.id;
	}

	public Piece(Board.Type type, int id, int width, int height) throws IOException
	{
		this.id = id;
		switch (type)
		{
		case B_Bishop:
			this.type = type;
			image = ICODecoder.read(getClass().getResourceAsStream("img/Black B.ico"));
			image.set(0, getScaledImage(image.get(0), width, height));
			value = 300;
			break;
		case B_King:
			this.type = type;
			image = ICODecoder.read(getClass().getResourceAsStream("img/Black K.ico"));
			image.set(0, getScaledImage(image.get(0), width, height));
			value = 200000;
			break;
		case B_Knight:
			this.type = type;
			image = ICODecoder.read(getClass().getResourceAsStream("img/Black N.ico"));
			image.set(0, getScaledImage(image.get(0), width, height));
			value = 300;
			break;
		case B_Pawn:
			this.type = type;
			image = ICODecoder.read(getClass().getResourceAsStream("img/Black P.ico"));
			image.set(0, getScaledImage(image.get(0), width, height));
			value = 100;
			break;
		case B_Queen:
			this.type = type;
			image = ICODecoder.read(getClass().getResourceAsStream("img/Black Q.ico"));
			image.set(0, getScaledImage(image.get(0), width, height));
			value = 900;
			break;
		case B_Rook:
			this.type = type;
			image = ICODecoder.read(getClass().getResourceAsStream("img/Black R.ico"));
			image.set(0, getScaledImage(image.get(0), width, height));
			value = 500;
			break;
		case W_Bishop:
			this.type = type;
			image = ICODecoder.read(getClass().getResourceAsStream("img/White B.ico"));
			image.set(0, getScaledImage(image.get(0), width, height));
			value = -300;
			break;
		case W_King:
			this.type = type;
			image = ICODecoder.read(getClass().getResourceAsStream("img/White K.ico"));
			image.set(0, getScaledImage(image.get(0), width, height));
			value = -200000;
			break;
		case W_Knight:
			this.type = type;
			image = ICODecoder.read(getClass().getResourceAsStream("img/White N.ico"));
			image.set(0, getScaledImage(image.get(0), width, height));
			value = -300;
			break;
		case W_Pawn:
			this.type = type;
			image = ICODecoder.read(getClass().getResourceAsStream("img/White P.ico"));
			image.set(0, getScaledImage(image.get(0), width, height));
			value = -100;
			break;
		case W_Queen:
			this.type = type;
			image = ICODecoder.read(getClass().getResourceAsStream("img/White Q.ico"));
			image.set(0, getScaledImage(image.get(0), width, height));
			value = -900;
			break;
		case W_Rook:
			this.type = type;
			image = ICODecoder.read(getClass().getResourceAsStream("img/White R.ico"));
			image.set(0, getScaledImage(image.get(0), width, height));
			value = -500;
			break;
		default:
			break;

		}
		piece = new JLabel();
		piece.setIcon(new ImageIcon(image.get(0)));
		MouseListener listener = new MouseListener();
		piece.addMouseListener(listener);
	}

	private BufferedImage getScaledImage(Image srcImg, int w, int h){
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();
		return resizedImg;
	}

	public Piece getPiece()
	{
		return this;
	}

	public void setCurrentLocation(Tile tile)
	{
		currentLocation = tile;
	}

	public Tile getCurrentLocation()
	{
		return currentLocation;
	}

	public JLabel getImage()
	{
		return piece;
	}

	public void getActions()
	{
		Actions action = new Actions();
		action.getPseudoLegalActions(null);
	}

	public Map<Action, Integer> getLegalActions(boolean AI)
	{
		Actions action = new Actions();
		HashMap<Action, Integer> possibleActions = (HashMap<Action, Integer>) action.getLegalActions(null, AI);
		return possibleActions;
	}

	public boolean getLegalActionsSize()
	{
		Actions action = new Actions();
		HashMap<Action, Integer> possibleActions = (HashMap<Action, Integer>) action.getLegalActions(null, true);
		return possibleActions.size() != 0;
	}

	public Board.Type getType()
	{
		return type;
	}

	public boolean areEnemies(Piece p1, Piece p2)
	{
		char p1Type = p1.getType().toString().charAt(0);
		char p2Type = p2.getType().toString().charAt(0);
		if ((p1Type == 'B' && p2Type == 'W') || (p1Type == 'W' && p2Type == 'B')) return true;
		else return false;
	}

	public class MouseListener extends MouseInputAdapter
	{
		boolean selected = false;
		Board board;
		int rank = 0;
		int file = 0;

		public void mouseClicked(MouseEvent e)
		{
			if (getCurrentLocation().getBoard().getTurn() == getType().toString().charAt(0))
			{
				//Get all possible actions
				Actions actions = new Actions();
				actions.getLegalActions(null, false);
				//Get the current board state from the piece's tile location
				board = getCurrentLocation().getBoard();
				//Set X and Y offset based on tile location
				rank = getCurrentLocation().getComponent().getX();
				file = getCurrentLocation().getComponent().getY();
				//Remove piece from tile
				getCurrentLocation().getComponent().remove(piece);
				//Remove relationship between tile and piece
				getCurrentLocation().setPiece(null);
				//Add piece to board
				board.getLayeredPane().add(piece);
				//Add to upper layer
				board.getLayeredPane().setLayer(piece, 1);
				//Set initial location
				piece.setLocation(rank, file);
				//Let board know piece is selected
				//Create a new selected piece
				Board.SelectedPiece sp = board.new SelectedPiece(piece, e.getX(), e.getY());
				//Give board selected piece
				board.setPiece(sp);
				//Remove relationship with tile
				getCurrentLocation().setPiece(null);
			}
		}
	}

	public class Actions
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
		public Results results = new Results();
		public Tile[][] tiles;
		public HashMap<Action, Integer> possibleActions;

		public Map<Action, Integer> getPseudoLegalActions(Object p) {
			possibleActions = new HashMap<Action, Integer>(); //Holds list of possible actions
			tiles = getCurrentLocation().getBoard().getAllTiles();
			switch (type)
			{
			case B_Bishop:
				checkDiagDownRightMoves(false, 8, 8, false);
				checkDiagDownLeftMoves(false, 8, 0, false);
				checkDiagUpLeftMoves(false, 0, 0, false);
				checkDiagUpRightMoves(false, 0, 8, false);
				break;
			case B_King:
				int maxRankMoves = getCurrentLocation().getArrayRowLocation();
				int maxFileMoves = getCurrentLocation().getArrayColumnLocation();
				checkDownMoves(false, maxRankMoves + 2, false);
				checkUpMoves(false, maxRankMoves - 1, false);
				checkLeftMoves(false, maxFileMoves - 1, false);
				checkRightMoves(false, maxFileMoves + 2, false);
				checkDiagDownRightMoves(false, maxRankMoves + 2, maxFileMoves + 2, false);
				checkDiagDownLeftMoves(false, maxRankMoves + 2,  maxFileMoves - 1, false);
				checkDiagUpRightMoves(false, maxRankMoves - 1, maxFileMoves + 2, false);
				checkDiagUpLeftMoves(false, maxRankMoves - 1, maxFileMoves - 1, false);
				break;
			case B_Knight:
				checkUpRightMoves(false, false);
				checkUpLeftMoves(false, false);
				checkRightUpMoves(false, false);
				checkRightDownMoves(false, false);
				checkLeftUpMoves(false, false);
				checkLeftDownMoves(false, false);
				checkDownRightMoves(false, false);
				checkDownLeftMoves(false, false);
				break;
			case B_Pawn:
				maxRankMoves = getCurrentLocation().getArrayRowLocation();
				maxFileMoves = getCurrentLocation().getArrayColumnLocation();
				//Check down actions
				if (pawnInit)
				{
					checkDownMoves(false, maxRankMoves + 3, false);
				}
				else
				{
					checkDownMoves(false, maxRankMoves + 2, false);
				}
				//Check diagonal down right attacks
				if (maxRankMoves + 1 < 8 && maxFileMoves + 1 < 8 && !tiles[maxRankMoves + 1][maxFileMoves + 1].isEmpty() && areEnemies(getPiece(), tiles[maxRankMoves + 1][maxFileMoves + 1].getCurrentPiece()))
				{
					checkDiagDownRightMoves(false, maxRankMoves + 2, maxFileMoves + 2, false);
				}
				//Check diagonal down left attacks
				if (maxRankMoves + 1 < 8 && maxFileMoves - 1 >= 0 && !tiles[maxRankMoves + 1][maxFileMoves - 1].isEmpty() && areEnemies(getPiece(), tiles[maxRankMoves + 1][maxFileMoves - 1].getCurrentPiece()))
				{
					checkDiagDownLeftMoves(false, maxRankMoves + 2, maxFileMoves - 1, false);
				}
				break;
			case B_Queen:
				checkDownMoves(false, 8, false);
				checkUpMoves(false, 0, false);
				checkLeftMoves(false, 0, false);
				checkRightMoves(false, 8, false);
				checkDiagUpLeftMoves(false, 0, 0, false);
				checkDiagUpRightMoves(false, 0, 8, false);
				checkDiagDownLeftMoves(false, 8, 0, false);
				checkDiagDownRightMoves(false, 8, 8, false);
				break;
			case B_Rook:
				checkUpMoves(false, 0, false);
				checkDownMoves(false, 8, false);
				checkLeftMoves(false, 0, false);
				checkRightMoves(false, 8, false);
				break;
			case W_Bishop:
				checkDiagUpRightMoves(false, 0, 8, false);
				checkDiagUpLeftMoves(false, 0, 0, false);
				checkDiagDownRightMoves(false, 8, 8, false);
				checkDiagDownLeftMoves(false, 8, 0, false);
				break;
			case W_King:
				maxRankMoves = getCurrentLocation().getArrayRowLocation();
				maxFileMoves = getCurrentLocation().getArrayColumnLocation();
				checkDownMoves(false, maxRankMoves + 2, false);
				checkUpMoves(false, maxRankMoves - 1, false);
				checkLeftMoves(false, maxFileMoves - 1, false);
				checkRightMoves(false, maxFileMoves + 2, false);
				checkDiagDownRightMoves(false, maxRankMoves + 2, maxFileMoves + 2, false);
				checkDiagDownLeftMoves(false, maxRankMoves + 2,  maxFileMoves - 1, false);
				checkDiagUpRightMoves(false, maxRankMoves - 1, maxFileMoves + 2, false);
				checkDiagUpLeftMoves(false, maxRankMoves - 1, maxFileMoves - 1, false);
				break;
			case W_Knight:
				checkUpRightMoves(false, false);
				checkUpLeftMoves(false, false);
				checkRightUpMoves(false, false);
				checkRightDownMoves(false, false);
				checkLeftUpMoves(false, false);
				checkLeftDownMoves(false, false);
				checkDownRightMoves(false, false);
				checkDownLeftMoves(false, false);
				break;
			case W_Pawn:
				maxRankMoves = getCurrentLocation().getArrayRowLocation();
				maxFileMoves = getCurrentLocation().getArrayColumnLocation();
				//Check down actions
				if (pawnInit)
				{
					checkUpMoves(false, maxRankMoves - 2, false);
				}
				else
				{
					checkUpMoves(false, maxRankMoves - 1, false);
				}
				//Check diagonal up right attacks
				if (maxRankMoves - 1 >= 0 && maxFileMoves + 1 < 8 && !tiles[maxRankMoves - 1][maxFileMoves + 1].isEmpty() && areEnemies(getPiece(), tiles[maxRankMoves - 1][maxFileMoves + 1].getCurrentPiece()))
				{
					checkDiagUpRightMoves(false, maxRankMoves - 1, maxFileMoves + 2, false);
				}
				//Check diagonal up left attacks
				if (maxRankMoves - 1 >= 0 && maxFileMoves - 1 >= 0 && !tiles[maxRankMoves - 1][maxFileMoves - 1].isEmpty() && areEnemies(getPiece(), tiles[maxRankMoves - 1][maxFileMoves - 1].getCurrentPiece()))
				{
					checkDiagUpLeftMoves(false, maxRankMoves - 1, maxFileMoves - 1, false);
				}
				break;
			case W_Queen:
				checkDownMoves(false, 8, false);
				checkUpMoves(false, 0, false);
				checkLeftMoves(false, 0, false);
				checkRightMoves(false, 8, false);
				checkDiagUpLeftMoves(false, 0, 0, false);
				checkDiagUpRightMoves(false, 0, 8, false);
				checkDiagDownLeftMoves(false, 8, 0, false);
				checkDiagDownRightMoves(false, 8, 8, false);
				break;
			case W_Rook:
				checkUpMoves(false, 0, false);
				checkDownMoves(false, 8, false);
				checkLeftMoves(false, 0, false);
				checkRightMoves(false, 8, false);
				break;
			default:
				break;
			}
			return possibleActions; //Return list of possible actions
		}

		public Map<Action, Integer> getLegalActions(Object p, boolean AI) {
			possibleActions = new HashMap<Action, Integer>(); //Holds list of possible actions
			tiles = getCurrentLocation().getBoard().getAllTiles();
			switch (type)
			{
			case B_Bishop:
				checkDiagDownRightMoves(true, 8, 8, AI);
				checkDiagDownLeftMoves(true, 8, 0, AI);
				checkDiagUpLeftMoves(true, 0, 0, AI);
				checkDiagUpRightMoves(true, 0, 8, AI);
				break;
			case B_King:
				int maxRankMoves = getCurrentLocation().getArrayRowLocation();
				int maxFileMoves = getCurrentLocation().getArrayColumnLocation();
				checkDownMoves(true, maxRankMoves + 2, AI);
				checkUpMoves(true, maxRankMoves - 1, AI);
				checkLeftMoves(true, maxFileMoves - 1, AI);
				checkRightMoves(true, maxFileMoves + 2, AI);
				checkDiagDownRightMoves(true, maxRankMoves + 2, maxFileMoves + 2, AI);
				checkDiagDownLeftMoves(true, maxRankMoves + 2,  maxFileMoves - 1, AI);
				checkDiagUpRightMoves(true, maxRankMoves - 1, maxFileMoves + 2, AI);
				checkDiagUpLeftMoves(true, maxRankMoves - 1, maxFileMoves - 1, AI);
				break;
			case B_Knight:
				checkUpRightMoves(true, AI);
				checkUpLeftMoves(true, AI);
				checkRightUpMoves(true, AI);
				checkRightDownMoves(true, AI);
				checkLeftUpMoves(true, AI);
				checkLeftDownMoves(true, AI);
				checkDownRightMoves(true, AI);
				checkDownLeftMoves(true, AI);
				break;
			case B_Pawn:
				maxRankMoves = getCurrentLocation().getArrayRowLocation();
				maxFileMoves = getCurrentLocation().getArrayColumnLocation();
				//Check down actions
				if (pawnInit)
				{
					checkDownMoves(true, maxRankMoves + 3, AI);
				}
				else
				{
					checkDownMoves(true, maxRankMoves + 2, AI);
				}
				//Check diagonal down right attacks
				if (maxRankMoves + 1 < 8 && maxFileMoves + 1 < 8 && !tiles[maxRankMoves + 1][maxFileMoves + 1].isEmpty() && areEnemies(getPiece(), tiles[maxRankMoves + 1][maxFileMoves + 1].getCurrentPiece()))
				{
					checkDiagDownRightMoves(true, maxRankMoves + 2, maxFileMoves + 2, AI);
				}
				//Check diagonal down left attacks
				if (maxRankMoves + 1 < 8 && maxFileMoves - 1 >= 0 && !tiles[maxRankMoves + 1][maxFileMoves - 1].isEmpty() && areEnemies(getPiece(), tiles[maxRankMoves + 1][maxFileMoves - 1].getCurrentPiece()))
				{
					checkDiagDownLeftMoves(true, maxRankMoves + 2, maxFileMoves - 1, AI);
				}
				break;
			case B_Queen:
				checkDownMoves(true, 8, AI);
				checkUpMoves(true, 0, AI);
				checkLeftMoves(true, 0, AI);
				checkRightMoves(true, 8, AI);
				checkDiagUpLeftMoves(true, 0, 0, AI);
				checkDiagUpRightMoves(true, 0, 8, AI);
				checkDiagDownLeftMoves(true, 8, 0, AI);
				checkDiagDownRightMoves(true, 8, 8, AI);
				break;
			case B_Rook:
				checkUpMoves(true, 0, AI);
				checkDownMoves(true, 8, AI);
				checkLeftMoves(true, 0, AI);
				checkRightMoves(true, 8, AI);
				break;
			case W_Bishop:
				checkDiagUpRightMoves(true, 0, 8, AI);
				checkDiagUpLeftMoves(true, 0, 0, AI);
				checkDiagDownRightMoves(true, 8, 8, AI);
				checkDiagDownLeftMoves(true, 8, 0, AI);
				break;
			case W_King:
				maxRankMoves = getCurrentLocation().getArrayRowLocation();
				maxFileMoves = getCurrentLocation().getArrayColumnLocation();
				checkDownMoves(true, maxRankMoves + 2, AI);
				checkUpMoves(true, maxRankMoves - 1, AI);
				checkLeftMoves(true, maxFileMoves - 1, AI);
				checkRightMoves(true, maxFileMoves + 2, AI);
				checkDiagDownRightMoves(true, maxRankMoves + 2, maxFileMoves + 2, AI);
				checkDiagDownLeftMoves(true, maxRankMoves + 2,  maxFileMoves - 1, AI);
				checkDiagUpRightMoves(true, maxRankMoves - 1, maxFileMoves + 2, AI);
				checkDiagUpLeftMoves(true, maxRankMoves - 1, maxFileMoves - 1, AI);
				break;
			case W_Knight:
				checkUpRightMoves(true, AI);
				checkUpLeftMoves(true, AI);
				checkRightUpMoves(true, AI);
				checkRightDownMoves(true, AI);
				checkLeftUpMoves(true, AI);
				checkLeftDownMoves(true, AI);
				checkDownRightMoves(true, AI);
				checkDownLeftMoves(true, AI);
				break;
			case W_Pawn:
				maxRankMoves = getCurrentLocation().getArrayRowLocation();
				maxFileMoves = getCurrentLocation().getArrayColumnLocation();
				//Check down actions
				if (pawnInit)
				{
					checkUpMoves(true, maxRankMoves - 2, AI);
				}
				else
				{
					checkUpMoves(true, maxRankMoves - 1, AI);
				}
				//Check diagonal up right attacks
				if (maxRankMoves - 1 >= 0 && maxFileMoves + 1 < 8 && !tiles[maxRankMoves - 1][maxFileMoves + 1].isEmpty() && areEnemies(getPiece(), tiles[maxRankMoves - 1][maxFileMoves + 1].getCurrentPiece()))
				{
					checkDiagUpRightMoves(true, maxRankMoves - 1, maxFileMoves + 2, AI);
				}
				//Check diagonal up left attacks
				if (maxRankMoves - 1 >= 0 && maxFileMoves - 1 >= 0 && !tiles[maxRankMoves - 1][maxFileMoves - 1].isEmpty() && areEnemies(getPiece(), tiles[maxRankMoves - 1][maxFileMoves - 1].getCurrentPiece()))
				{
					checkDiagUpLeftMoves(true, maxRankMoves - 1, maxFileMoves - 1, AI);
				}
				break;
			case W_Queen:
				checkDownMoves(true, 8, AI);
				checkUpMoves(true, 0, AI);
				checkLeftMoves(true, 0, AI);
				checkRightMoves(true, 8, AI);
				checkDiagUpLeftMoves(true, 0, 0, AI);
				checkDiagUpRightMoves(true, 0, 8, AI);
				checkDiagDownLeftMoves(true, 8, 0, AI);
				checkDiagDownRightMoves(true, 8, 8, AI);
				break;
			case W_Rook:
				checkUpMoves(true, 0, AI);
				checkDownMoves(true, 8, AI);
				checkLeftMoves(true, 0, AI);
				checkRightMoves(true, 8, AI);
				break;
			default:
				break;
			}
			return possibleActions; //Return list of possible actions
		}

		public void checkDiagDownRightMoves(boolean legal, int rCount, int fCount, boolean AI)
		{
			int counter = 1;
			int rank = getCurrentLocation().getArrayRowLocation();
			int file = getCurrentLocation().getArrayColumnLocation();
			//Check diag down right actions
			for (int i = rank + 1, j = file + 1; i < Math.min(8, rCount) && j < Math.min(8, fCount); i++, j++)
			{
				if (tiles[i][j].isEmpty() || areEnemies(getPiece(), tiles[i][j].getCurrentPiece()))
				{
					Board newBoard = legal ? (Board) results.getResult(getCurrentLocation().getBoard(), DIAGDOWNRIGHT, getPiece(), counter) : null;
					if (!legal || !newBoard.causalCheck(getType()))
					{
						tiles[i][j].setLegal(true);
						if (legal && !AI) tiles[i][j].getComponent().setBackground(Color.CYAN);
						possibleActions.put(DIAGDOWNRIGHT, counter);
						if (!tiles[i][j].isEmpty() && areEnemies(getPiece(), tiles[i][j].getCurrentPiece()))
						{
							if (legal && !AI) tiles[i][j].getComponent().setBackground(Color.RED);
							break;
						}
					}
					counter++;
				}
				else break;
			}
		}

		public void checkDiagUpRightMoves(boolean legal, int rCount, int fCount, boolean AI)
		{
			int counter = 1;
			int rank = getCurrentLocation().getArrayRowLocation();
			int file = getCurrentLocation().getArrayColumnLocation();
			//Check diag up right actions
			for (int i = rank - 1, j = file + 1; i >= Math.max(0, rCount) && j < Math.min(8, fCount); i--, j++)
			{
				if (tiles[i][j].isEmpty() || areEnemies(getPiece(), tiles[i][j].getCurrentPiece()))
				{
					Board newBoard = legal ? (Board) results.getResult(getCurrentLocation().getBoard(), DIAGUPRIGHT, getPiece(), counter) : null;
					if (!legal || !newBoard.causalCheck(getType()))
					{
						tiles[i][j].setLegal(true);
						if (legal && !AI) tiles[i][j].getComponent().setBackground(Color.CYAN);
						possibleActions.put(DIAGUPRIGHT, counter);
						if (!tiles[i][j].isEmpty() && areEnemies(getPiece(), tiles[i][j].getCurrentPiece()))
						{
							if (legal && !AI) tiles[i][j].getComponent().setBackground(Color.RED);
							break;
						}
					}
					counter++;
				}
				else break;
			}
		}

		public void checkDiagDownLeftMoves(boolean legal, int rCount, int fCount, boolean AI)
		{
			int counter = 1;
			int rank = getCurrentLocation().getArrayRowLocation();
			int file = getCurrentLocation().getArrayColumnLocation();
			//Check diag down left actions
			for (int i = rank + 1, j = file - 1; i < Math.min(8, rCount) && j >= Math.max(0, fCount); i++, j--)
			{
				if (tiles[i][j].isEmpty() || areEnemies(getPiece(), tiles[i][j].getCurrentPiece()))
				{
					Board newBoard = legal ? (Board) results.getResult(getCurrentLocation().getBoard(), DIAGDOWNLEFT, getPiece(), counter) : null;
					if (!legal || !newBoard.causalCheck(getType()))
					{
						tiles[i][j].setLegal(true);
						if (legal && !AI) tiles[i][j].getComponent().setBackground(Color.CYAN);
						possibleActions.put(DIAGDOWNLEFT, counter);
						if (!tiles[i][j].isEmpty() && areEnemies(getPiece(), tiles[i][j].getCurrentPiece()))
						{
							if (legal && !AI) tiles[i][j].getComponent().setBackground(Color.RED);
							break;
						}
					}
					counter++;
				}
				else break;
			}
		}

		public void checkDiagUpLeftMoves(boolean legal, int rCount, int fCount, boolean AI)
		{
			int counter = 1;
			int rank = getCurrentLocation().getArrayRowLocation();
			int file = getCurrentLocation().getArrayColumnLocation();
			//Check diag up left actions
			for (int i = rank - 1, j = file - 1; i >= Math.max(0, rCount) && j >= Math.max(0, fCount); i--, j--)
			{
				if (tiles[i][j].isEmpty() || areEnemies(getPiece(), tiles[i][j].getCurrentPiece()))
				{
					Board newBoard = legal ? (Board) results.getResult(getCurrentLocation().getBoard(), DIAGUPLEFT, getPiece(), counter) : null;
					if (!legal || !newBoard.causalCheck(getType()))
					{
						tiles[i][j].setLegal(true);
						if (legal && !AI) tiles[i][j].getComponent().setBackground(Color.CYAN);
						possibleActions.put(DIAGUPLEFT, counter);
						if (!tiles[i][j].isEmpty() && areEnemies(getPiece(), tiles[i][j].getCurrentPiece()))
						{
							if (legal && !AI) tiles[i][j].getComponent().setBackground(Color.RED);
							break;
						}
					}
					counter++;
				}
				else break;
			}
		}

		public void checkRightMoves(boolean legal, int count, boolean AI)
		{
			int counter = 1;
			int rank = getCurrentLocation().getArrayRowLocation();
			int file = getCurrentLocation().getArrayColumnLocation();
			//Check right actions
			for (int i = file + 1; i < Math.min(8, count); i++)
			{
				if (tiles[rank][i].isEmpty() || areEnemies(getPiece(), tiles[rank][i].getCurrentPiece()))
				{
					Board newBoard = legal ? (Board) results.getResult(getCurrentLocation().getBoard(), RIGHT, getPiece(), counter) : null;
					if (!legal || !newBoard.causalCheck(getType()))
					{
						tiles[rank][i].setLegal(true);
						if (legal && !AI) tiles[rank][i].getComponent().setBackground(Color.CYAN);
						possibleActions.put(RIGHT, counter);
						if (!tiles[rank][i].isEmpty() && areEnemies(getPiece(), tiles[rank][i].getCurrentPiece()))
						{
							if (legal && !AI) tiles[rank][i].getComponent().setBackground(Color.RED);
							break;
						}
					}
					counter++;
				}
				else break;
			}
		}


		public void checkLeftMoves(boolean legal, int count, boolean AI)
		{
			int counter = 1;
			int rank = getCurrentLocation().getArrayRowLocation();
			int file = getCurrentLocation().getArrayColumnLocation();
			//Check left actions
			for (int i = file - 1; i >= Math.max(0, count); i--)
			{
				if (tiles[rank][i].isEmpty() || areEnemies(getPiece(), tiles[rank][i].getCurrentPiece()))
				{
					Board newBoard = legal ? (Board) results.getResult(getCurrentLocation().getBoard(), LEFT, getPiece(), counter) : null;
					if (!legal || !newBoard.causalCheck(getType()))
					{
						tiles[rank][i].setLegal(true);
						if (legal && !AI) tiles[rank][i].getComponent().setBackground(Color.CYAN);
						possibleActions.put(LEFT, counter);
						if (!tiles[rank][i].isEmpty() && areEnemies(getPiece(), tiles[rank][i].getCurrentPiece()))
						{
							if (legal && !AI) tiles[rank][i].getComponent().setBackground(Color.RED);
							break;
						}
					}
					counter++;
				}
				else break;
			}
		}

		public void checkDownMoves(boolean legal, int count, boolean AI)
		{
			int counter = 1;
			int rank = getCurrentLocation().getArrayRowLocation();
			int file = getCurrentLocation().getArrayColumnLocation();
			//Check down actions
			for (int i = rank + 1; i < Math.min(8, count); i++)
			{
				if (tiles[i][file].isEmpty() || areEnemies(getPiece(), tiles[i][file].getCurrentPiece()) && (!getType().equals(Board.Type.B_Pawn) && !getType().equals((Board.Type.W_Pawn))))
				{
					Board newBoard = legal ? (Board) results.getResult(getCurrentLocation().getBoard(), DOWN, getPiece(), counter) : null;
					if (!legal || !newBoard.causalCheck(getType()))
					{
						tiles[i][file].setLegal(true);
						if (legal && !AI) tiles[i][file].getComponent().setBackground(Color.CYAN);
						possibleActions.put(DOWN, counter);
						if (!tiles[i][file].isEmpty() && areEnemies(getPiece(), tiles[i][file].getCurrentPiece()))
						{
							if (legal && !AI) tiles[i][file].getComponent().setBackground(Color.RED);
							break;
						}
					}
					counter++;
				}
				else break;
			}
		}

		public void checkUpMoves(boolean legal, int count, boolean AI)
		{
			int counter = 1;
			int rank = getCurrentLocation().getArrayRowLocation();
			int file = getCurrentLocation().getArrayColumnLocation();
			//Check up actions
			for (int i = rank - 1; i >= Math.max(0, count); i--)
			{
				if (tiles[i][file].isEmpty() || (areEnemies(getPiece(), tiles[i][file].getCurrentPiece()) && (!getType().equals(Board.Type.B_Pawn) && !getType().equals((Board.Type.W_Pawn)))))
				{
					Board newBoard = legal ? (Board) results.getResult(getCurrentLocation().getBoard(), UP, getPiece(), counter) : null;
					if (!legal || !newBoard.causalCheck(getType()))
					{
						tiles[i][file].setLegal(true);
						if (legal && !AI) tiles[i][file].getComponent().setBackground(Color.CYAN);
						possibleActions.put(UP, counter);
						if (!tiles[i][file].isEmpty() && areEnemies(getPiece(), tiles[i][file].getCurrentPiece()))
						{
							if (legal && !AI) tiles[i][file].getComponent().setBackground(Color.RED);
							break;
						}
					}
					counter++;
				}
				else break;
			}
		}

		public void checkUpRightMoves(boolean legal, boolean AI)
		{
			int rank = getCurrentLocation().getArrayRowLocation();
			int file = getCurrentLocation().getArrayColumnLocation();
			rank = rank - 2;
			file = file + 1;
			//Check for up right actions
			if (file < 8 && rank >= 0 && (tiles[rank][file].isEmpty() || areEnemies(getPiece(), tiles[rank][file].getCurrentPiece())))
			{
				//See if move puts king in check
				Board newBoard = legal ? (Board) results.getResult(getCurrentLocation().getBoard(), UPRIGHT, getPiece(), 1) : null;
				if (!legal || !newBoard.causalCheck(getType()))
				{
					possibleActions.put(UPRIGHT, 1);
					if (legal && !AI) tiles[rank][file].getComponent().setBackground(Color.CYAN);
					tiles[rank][file].setLegal(true);
					if (!tiles[rank][file].isEmpty() && areEnemies(getPiece(), tiles[rank][file].getCurrentPiece()) && legal && !AI)
					{
						tiles[rank][file].getComponent().setBackground(Color.RED);
					}
				}
			}
		}

		public void checkUpLeftMoves(boolean legal, boolean AI)
		{
			int rank = getCurrentLocation().getArrayRowLocation();
			int file = getCurrentLocation().getArrayColumnLocation();
			//Check for up left actions
			rank = rank - 2;
			file = file - 1;
			if (file >= 0 && rank >= 0 && (tiles[rank][file].isEmpty() || areEnemies(getPiece(), tiles[rank][file].getCurrentPiece())))
			{
				//See if move puts king in check
				Board newBoard = legal ? (Board) results.getResult(getCurrentLocation().getBoard(), UPLEFT, getPiece(), 1) : null;
				if (!legal || !newBoard.causalCheck(getType()))
				{
					possibleActions.put(UPLEFT, 1);
					if (legal && !AI) tiles[rank][file].getComponent().setBackground(Color.CYAN);
					tiles[rank][file].setLegal(true);
					if (!tiles[rank][file].isEmpty() && areEnemies(getPiece(), tiles[rank][file].getCurrentPiece()) && legal && !AI)
					{
						tiles[rank][file].getComponent().setBackground(Color.RED);
					}
				}
			}
		}

		public void checkLeftUpMoves(boolean legal, boolean AI)
		{
			int rank = getCurrentLocation().getArrayRowLocation();
			int file = getCurrentLocation().getArrayColumnLocation();
			//Check for left up actions
			rank = rank - 1;
			file = file - 2;
			if (file >= 0 && rank >= 0 && (tiles[rank][file].isEmpty() || areEnemies(getPiece(), tiles[rank][file].getCurrentPiece())))
			{
				Board newBoard = legal ? (Board) results.getResult(getCurrentLocation().getBoard(), LEFTUP, getPiece(), 1) : null;
				if (!legal || !newBoard.causalCheck(getType()))
				{
					possibleActions.put(LEFTUP, 1);
					if (legal && !AI) tiles[rank][file].getComponent().setBackground(Color.CYAN);
					tiles[rank][file].setLegal(true);
					if (!tiles[rank][file].isEmpty() && areEnemies(getPiece(), tiles[rank][file].getCurrentPiece()) && legal && !AI)
					{
						tiles[rank][file].getComponent().setBackground(Color.RED);
					}
				}
			}
		}

		public void checkLeftDownMoves(boolean legal, boolean AI)
		{
			int rank = getCurrentLocation().getArrayRowLocation();
			int file = getCurrentLocation().getArrayColumnLocation();
			//Check for left down actions
			rank = rank + 1;
			file = file - 2;
			if (file >= 0 && rank < 8 && (tiles[rank][file].isEmpty() || areEnemies(getPiece(), tiles[rank][file].getCurrentPiece())))
			{
				Board newBoard = legal ? (Board) results.getResult(getCurrentLocation().getBoard(), LEFTDOWN, getPiece(), 1) : null;
				if (!legal || !newBoard.causalCheck(getType()))
				{
					possibleActions.put(LEFTDOWN, 1);
					if (legal && !AI) tiles[rank][file].getComponent().setBackground(Color.CYAN);
					tiles[rank][file].setLegal(true);
					if (!tiles[rank][file].isEmpty() && areEnemies(getPiece(), tiles[rank][file].getCurrentPiece()) && legal && !AI)
					{
						tiles[rank][file].getComponent().setBackground(Color.RED);
					}
				}
			}
		}

		public void checkRightUpMoves(boolean legal, boolean AI)
		{
			int rank = getCurrentLocation().getArrayRowLocation();
			int file = getCurrentLocation().getArrayColumnLocation();
			//Check for right up actions
			rank = rank - 1;
			file = file + 2;
			if (file < 8 && rank >= 0 && (tiles[rank][file].isEmpty() || areEnemies(getPiece(), tiles[rank][file].getCurrentPiece())))
			{
				Board newBoard = legal ? (Board) results.getResult(getCurrentLocation().getBoard(), RIGHTUP, getPiece(), 1) : null;
				if (!legal || !newBoard.causalCheck(getType()))
				{
					possibleActions.put(RIGHTUP, 1);
					if (legal && !AI) tiles[rank][file].getComponent().setBackground(Color.CYAN);
					tiles[rank][file].setLegal(true);
					if (!tiles[rank][file].isEmpty() && areEnemies(getPiece(), tiles[rank][file].getCurrentPiece()) && legal && !AI)
					{
						tiles[rank][file].getComponent().setBackground(Color.RED);
					}
				}
			}
		}

		public void checkRightDownMoves(boolean legal, boolean AI)
		{
			int rank = getCurrentLocation().getArrayRowLocation();
			int file = getCurrentLocation().getArrayColumnLocation();
			//Check for right down actions
			rank = rank + 1;
			file = file + 2;
			if (file < 8 && rank < 8 && (tiles[rank][file].isEmpty() || areEnemies(getPiece(), tiles[rank][file].getCurrentPiece())))
			{
				Board newBoard = legal ? (Board) results.getResult(getCurrentLocation().getBoard(), RIGHTDOWN, getPiece(), 1) : null;
				if (!legal || !newBoard.causalCheck(getType()))
				{
					possibleActions.put(RIGHTDOWN, 1);
					if (legal && !AI) tiles[rank][file].getComponent().setBackground(Color.CYAN);
					tiles[rank][file].setLegal(true);
					if (!tiles[rank][file].isEmpty() && areEnemies(getPiece(), tiles[rank][file].getCurrentPiece()) && legal && !AI)
					{
						tiles[rank][file].getComponent().setBackground(Color.RED);
					}
				}
			}
		}

		public void checkDownRightMoves(boolean legal, boolean AI)
		{
			int rank = getCurrentLocation().getArrayRowLocation();
			int file = getCurrentLocation().getArrayColumnLocation();
			//Check for down right actions
			rank = rank + 2;
			file = file + 1;
			if (file < 8 && rank < 8 && (tiles[rank][file].isEmpty() || areEnemies(getPiece(), tiles[rank][file].getCurrentPiece())))
			{
				Board newBoard = legal ? (Board) results.getResult(getCurrentLocation().getBoard(), DOWNRIGHT, getPiece(), 1) : null;
				if (!legal || !newBoard.causalCheck(getType()))
				{
					possibleActions.put(DOWNRIGHT, 1);
					if (legal && !AI) tiles[rank][file].getComponent().setBackground(Color.CYAN);
					tiles[rank][file].setLegal(true);
					if (!tiles[rank][file].isEmpty() && areEnemies(getPiece(), tiles[rank][file].getCurrentPiece()) && legal && !AI)
					{
						tiles[rank][file].getComponent().setBackground(Color.RED);
					}
				}
			}
		}

		public void checkDownLeftMoves(boolean legal, boolean AI)
		{
			int rank = getCurrentLocation().getArrayRowLocation();
			int file = getCurrentLocation().getArrayColumnLocation();
			//Check for down left actions
			rank = rank + 2;
			file = file - 1;
			if (file >= 0 && rank < 8 && (tiles[rank][file].isEmpty() || areEnemies(getPiece(), tiles[rank][file].getCurrentPiece())))
			{
				Board newBoard = legal ? (Board) results.getResult(getCurrentLocation().getBoard(), DOWNLEFT, getPiece(), 1) : null;
				if (!legal || !newBoard.causalCheck(getType()))
				{
					possibleActions.put(DOWNLEFT, 1);
					if (legal && !AI) tiles[rank][file].getComponent().setBackground(Color.CYAN);
					tiles[rank][file].setLegal(true);
					if (!tiles[rank][file].isEmpty() && areEnemies(getPiece(), tiles[rank][file].getCurrentPiece()) && legal && !AI)
					{
						tiles[rank][file].getComponent().setBackground(Color.RED);
					}
				}
			}
		}
	}
}
