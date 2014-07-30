import java.awt.Color;

import javax.swing.JPanel;


public class Tile {

	private JPanel tile;
	private int xLocation;
	private int yLocation;
	private Piece piece = null;
	private Board board = null;
	private boolean isLegal = false;
	int id = 0;

	public Tile(Tile t, Board b)
	{
		xLocation = t.getXLocation();
		yLocation = t.getYLocation();
		board = b;
		isLegal = t.isLegal();
		id = t.id;
		if (!t.isEmpty())
		{
			piece = new Piece(t.getCurrentPiece());
			piece.setCurrentLocation(this);
			setPiece(piece);
		}
	}

	public Tile(Color color, int xLocation, int yLocation, Board board, int id, int width, int height)
	{
		this.id = id;
		tile = new JPanel();
		tile.setSize(width, height);
		tile.setBackground(color);
		tile.setLocation(xLocation, yLocation);
		this.xLocation = xLocation;
		this.yLocation = yLocation;
		this.board = board;
	}

	public void setPiece(Piece p)
	{
		piece = p;
	}

	public Piece getCurrentPiece()
	{
		return piece;
	}

	public boolean isEmpty()
	{
		return piece == null;
	}

	public int getXLocation()
	{
		return xLocation;
	}

	public int getYLocation()
	{
		return yLocation;
	}

	public Tile getTile()
	{
		return this;
	}

	public Board getBoard()
	{
		return board;
	}

	public int getArrayRowLocation()
	{
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				if (board.getAllTiles()[i][j].equals(this)) return i;
			}
		}
		return -1;
	}

	public int getArrayColumnLocation()
	{
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				if (board.getAllTiles()[i][j].equals(this)) return j;
			}
		}
		return -1;
	}

	public JPanel getComponent()
	{
		return tile;
	}

	public boolean isLegal() {
		return isLegal;
	}

	public void setLegal(boolean isLegal) {
		this.isLegal = isLegal;
	}
}
