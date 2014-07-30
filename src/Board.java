import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;


public class Board {

	private LoadingScreen loading;
	private Tile[][] boardTiles = new Tile[8][8];
	private boolean whiteInCheck = false;
	private boolean blackInCheck = false;
	private JFrame screen;
	private JLayeredPane board;
	private JPanel glassPane;
	private JPanel menuPanel;
	private JPanel options;
	private SelectedPiece selectedPiece = null;
	private List<Piece> chessPieces = new ArrayList<Piece>();
	private ChessAI opponent = new ChessAI();
	private char turn = 'W';
	private int idCounter = 1;
	private JLabel lblTest;
	private JLabel sliderLabel;
	private JSlider slider;
	private JLabel lblSearchTime;
	private JSlider slider_1;
	private JLabel searchTime;
	private JTextArea textArea;
	private JScrollPane scrollArea;
	public JTextField kingPiece;
	public JTextField queenPiece;
	public JTextField bishopPiece;
	public JTextField rookPiece;
	public JTextField knightPiece;
	public JTextField pawnPiece;
	public JRadioButton usingPVNode = new JRadioButton("Use PV-Node");
	public JRadioButton globalAlpha = new JRadioButton("Save global alpha");
	public JRadioButton multiThread = new JRadioButton("Multi-threaded");
	public JRadioButton pvRange = new JRadioButton("Long PV Depth");
	public JRadioButton orderMoves = new JRadioButton("Order Moves");
	public JRadioButton usePVTTable = new JRadioButton("Use PVT");
	public JRadioButton kingEndGame = new JRadioButton("King's End Game");
	public JRadioButton saveLonePiece = new JRadioButton("Save Lone Piece");
	public JRadioButton queenMidGame = new JRadioButton("Queen's Mid Game");
	public JRadioButton pawnsDefense = new JRadioButton("Pawn's Defense");
	public JLabel expandedNodes = new JLabel("");
	public JComboBox<Double> branchingFactor = new JComboBox<Double>();
	private JRadioButton animateMoves;

	public enum Type {
		W_Rook, W_Knight, W_Bishop, W_Queen, W_King, W_Pawn,
		B_Rook, B_Knight, B_Bishop, B_Queen, B_King, B_Pawn
	}

	public Board(Board oldState)
	{
		this.turn = oldState.getTurn();
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				boardTiles[i][j] = new Tile(oldState.getAllTiles()[i][j], this);
				if (!boardTiles[i][j].isEmpty()) chessPieces.add(boardTiles[i][j].getCurrentPiece());
			}
		}
	}

	public Board() {
		// TODO Auto-generated constructor stub
	}

	public void init(int width, int height) throws IOException
	{
		//Create loading screen
		loading = new LoadingScreen();
		//Create screen
		screen = new JFrame();
		screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		screen.setResizable(false);
		screen.setSize(width, height);
		screen.setLocationRelativeTo(null);

		//Create primary panel
		board = new JLayeredPane();
		//Create glassPane for mouse movement interception
		glassPane = new JPanel();
		menuPanel = new JPanel();
		options = new JPanel();
		menuPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		options.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		textArea = new JTextArea();
		scrollArea = new JScrollPane(textArea);
		scrollArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		textArea.setBackground(UIManager.getColor("Panel.background"));
		screen.getContentPane().add(options, BorderLayout.WEST);
		screen.getContentPane().add(board, BorderLayout.CENTER);
		screen.getContentPane().add(menuPanel, BorderLayout.NORTH);
		screen.getContentPane().add(scrollArea, BorderLayout.EAST);
		//Add initial kerning
		textArea.setEditable(false);
		textArea.append("Move History...\n");
		textArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		lblTest = new JLabel("Depth");
		menuPanel.add(lblTest);
		//Set up slider
		slider = new JSlider();
		slider.setMinimum(1);
		slider.setMaximum(5);
		slider.setMajorTickSpacing(1);
		slider.setValue(3);
		menuPanel.add(slider);

		sliderLabel = new JLabel("");
		sliderLabel.setText("3");
		menuPanel.add(sliderLabel);
		SliderChanger slideListener = new SliderChanger(sliderLabel, slider);

		lblSearchTime = new JLabel("Time");
		menuPanel.add(lblSearchTime);

		//Set up second slider
		slider_1 = new JSlider();
		slider_1.setMinimum(1);
		slider_1.setMaximum(300);
		slider_1.setMajorTickSpacing(10);
		slider_1.setMinorTickSpacing(1);
		slider_1.setValue(10);
		menuPanel.add(slider_1);

		searchTime = new JLabel("");
		searchTime.setText("10");
		menuPanel.add(searchTime);

		GroupLayout gl_board = new GroupLayout(board);
		gl_board.setHorizontalGroup(
				gl_board.createParallelGroup(Alignment.LEADING)
				.addGap(0, 794, Short.MAX_VALUE)
				);
		gl_board.setVerticalGroup(
				gl_board.createParallelGroup(Alignment.LEADING)
				.addGap(0, 767, Short.MAX_VALUE)
				);
		board.setLayout(gl_board);
		board.setPreferredSize(screen.getSize());
		glassPane.setPreferredSize(screen.getSize());
		//Make sure glasspane is transparent
		glassPane.setOpaque(false);
		screen.setGlassPane(glassPane);
		//Draw all black and white tiles
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				if ((j + i) % 2 == 0) boardTiles[i][j] = new Tile(Color.WHITE, j * (width / 8), i * (height / 8), this, idCounter, (width / 8), (height / 8));
				else boardTiles[i][j] = new Tile(Color.BLACK, j * (width / 8), i * (height / 8), this, idCounter, (width / 8), (height / 8));
				board.add(boardTiles[i][j].getComponent());
				idCounter++;
				loading.update();
			}
		}
		//Draw all black and white pieces
		setBlackPieces((width / 8), (height / 8));
		setWhitePieces((width / 8), (height / 8));
		//Draw engine options screen


		JLabel lblPieceWeights = new JLabel("Piece weights");

		JLabel lblKing = new JLabel("King");

		JLabel lblQueen = new JLabel("Queen");

		JLabel lblBishop = new JLabel("Bishop");

		JLabel lblRook = new JLabel("Rook");

		JLabel lblKnight = new JLabel("Knight");

		JLabel lblPawn = new JLabel("Pawn");

		kingPiece = new JTextField();
		kingPiece.setColumns(10);

		queenPiece = new JTextField();
		queenPiece.setColumns(10);

		bishopPiece = new JTextField();
		bishopPiece.setColumns(10);

		rookPiece = new JTextField();
		rookPiece.setColumns(10);

		knightPiece = new JTextField();
		knightPiece.setColumns(10);

		pawnPiece = new JTextField();
		pawnPiece.setColumns(10);

		usingPVNode.setSelected(true);
		globalAlpha.setSelected(true);
		orderMoves.setSelected(true);
		usePVTTable.setSelected(true);
		branchingFactor.setModel(new DefaultComboBoxModel<Double>(new Double[] {(double) 1, .875, .75, .625, .5, .375, .25, .125}));
		branchingFactor.setSelectedIndex(0);

		JLabel lblBranchingFactor = new JLabel("Branching Factor");		

		animateMoves = new JRadioButton("Animate Moves");
		animateMoves.setSelected(true);

		GroupLayout groupLayout = new GroupLayout(options);
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(animateMoves)
								.addComponent(pawnsDefense)
								.addComponent(kingEndGame)
								.addComponent(usePVTTable)
								.addComponent(lblPieceWeights)
								.addGroup(groupLayout.createSequentialGroup()
										.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
												.addComponent(lblKing)
												.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
														.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
																.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
																		.addGroup(groupLayout.createSequentialGroup()
																				.addGap(6)
																				.addComponent(lblPawn))
																				.addComponent(lblKnight))
																				.addComponent(lblRook))
																				.addComponent(lblBishop)
																				.addComponent(lblQueen)))
																				.addPreferredGap(ComponentPlacement.RELATED)
																				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
																						.addComponent(pawnPiece, 0, 0, Short.MAX_VALUE)
																						.addComponent(knightPiece, 0, 0, Short.MAX_VALUE)
																						.addComponent(rookPiece, 0, 0, Short.MAX_VALUE)
																						.addComponent(bishopPiece, 0, 0, Short.MAX_VALUE)
																						.addComponent(kingPiece, 0, 0, Short.MAX_VALUE)
																						.addComponent(queenPiece, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)))
																						.addComponent(usingPVNode)
																						.addComponent(multiThread)
																						.addComponent(pvRange)
																						.addComponent(expandedNodes)
																						.addComponent(orderMoves)
																						.addComponent(globalAlpha)
																						.addComponent(lblBranchingFactor)
																						.addComponent(branchingFactor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																						.addComponent(saveLonePiece)
																						.addComponent(queenMidGame))
																						.addContainerGap(9, Short.MAX_VALUE))
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblPieceWeights)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(kingPiece, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblKing))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(queenPiece, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblQueen))
										.addPreferredGap(ComponentPlacement.RELATED)
										.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
												.addComponent(bishopPiece, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(lblBishop))
												.addPreferredGap(ComponentPlacement.RELATED)
												.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
														.addComponent(rookPiece, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addComponent(lblRook))
														.addPreferredGap(ComponentPlacement.RELATED)
														.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
																.addComponent(knightPiece, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																.addComponent(lblKnight))
																.addPreferredGap(ComponentPlacement.RELATED)
																.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
																		.addComponent(pawnPiece, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																		.addComponent(lblPawn))
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addComponent(usingPVNode)
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addComponent(globalAlpha)
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addComponent(multiThread)
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addComponent(pvRange)
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addComponent(orderMoves)
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addComponent(usePVTTable)
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addComponent(lblBranchingFactor)
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addComponent(branchingFactor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(ComponentPlacement.UNRELATED)
																		.addComponent(kingEndGame)
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addComponent(saveLonePiece)
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addComponent(queenMidGame)
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addComponent(pawnsDefense)
																		.addPreferredGap(ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
																		.addComponent(animateMoves)
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addComponent(expandedNodes))
				);
		options.setLayout(groupLayout);
		kingPiece.setText("200000");
		queenPiece.setText("900");
		bishopPiece.setText("330");
		knightPiece.setText("320");
		rookPiece.setText("500");
		pawnPiece.setText("100");
		options.setSize(150, 440);
		options.setVisible(true);
		orderMoves.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if (orderMoves.isSelected())
				{
					branchingFactor.setEnabled(true);
					pvRange.setEnabled(false);
					pvRange.setSelected(false);
					usingPVNode.setEnabled(false);
					usingPVNode.setSelected(false);
				}
				else
				{
					branchingFactor.setEnabled(false);
					pvRange.setEnabled(true);
					usingPVNode.setEnabled(true);
				}
			}

		});
		//Pack the screen
		packScreen();
		//Set mouse listener
		MouseMotion listener = new MouseMotion();
		SliderChanger slideListener_1 = new SliderChanger(searchTime, slider_1);
		glassPane.addMouseMotionListener(listener);
		glassPane.addMouseListener(listener);
		slider.addChangeListener(slideListener);
		slider_1.addChangeListener(slideListener_1);
	}
	//Set all black pieces
	public void setBlackPieces(int width, int height)
	{
		int row = 0;
		int column = 0;
		//Iterate through all piece types
		for (Type t : Type.values())
		{
			//Make sure only using black pieces
			if (t.toString().toCharArray()[0] == 'B')
			{
				try {
					if (t == Type.B_Pawn) //If black pawn
					{
						for (int i = 0; i < 8; i++)
						{
							//Create piece and put it on a tile
							Piece piece = new Piece(t, idCounter, width, height);
							idCounter++;
							boardTiles[1][i].getComponent().add(piece.getImage());
							//Create relationship between piece and tile
							boardTiles[1][i].setPiece(piece);
							piece.setCurrentLocation(boardTiles[1][i]);
							//Add piece to directory
							chessPieces.add(piece);
							loading.update();
						}
					}
					else if (t == Type.B_Queen || t == Type.B_King) //If king or queen
					{
						//Create piece and put it on a tile
						Piece piece = new Piece(t, idCounter, width, height);
						idCounter++;
						boardTiles[row][column].getComponent().add(piece.getImage());
						//Create relationship between piece and tile
						boardTiles[row][column].setPiece(piece);
						piece.setCurrentLocation(boardTiles[row][column]);
						column++;
						//Add piece to directory
						chessPieces.add(piece);
						loading.update();
					}
					else //All pieces besides pawn, queen and king
					{
						//Create two pieces and put them on tiles
						//We need two because they are unique objects but need to be mirrored across the board
						Piece pieceLeft = new Piece(t, idCounter, width, height);
						idCounter++;
						Piece pieceRight = new Piece(t, idCounter, width, height);
						idCounter++;
						boardTiles[row][column].getComponent().add(pieceLeft.getImage());
						boardTiles[row][7 - column].getComponent().add(pieceRight.getImage());
						//Set relationship between each piece and board tile
						boardTiles[row][column].setPiece(pieceLeft);
						boardTiles[row][7 - column].setPiece(pieceRight);
						pieceLeft.setCurrentLocation(boardTiles[row][column]);
						pieceRight.setCurrentLocation(boardTiles[row][7 - column]);
						column++;
						//Add pieces to directory
						chessPieces.add(pieceLeft);
						chessPieces.add(pieceRight);
						loading.update();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	//Look at setBlackPieces() for comments and relationship setting between pieces and tiles
	public void setWhitePieces(int width, int height)
	{
		int row = 7;
		int column = 0;

		for (Type t : Type.values())
		{
			if (t.toString().toCharArray()[0] == 'W')
			{
				try {
					if (t == Type.W_Pawn)
					{
						for (int i = 0; i < 8; i++)
						{
							Piece piece = new Piece(t, idCounter, width, height);
							idCounter++;
							boardTiles[6][i].getComponent().add(piece.getImage());
							boardTiles[6][i].setPiece(piece);
							piece.setCurrentLocation(boardTiles[6][i]);
							chessPieces.add(piece);
							loading.update();
						}
					}
					else if (t == Type.W_Queen || t == Type.W_King)
					{
						Piece piece = new Piece(t, idCounter, width, height);
						idCounter++;
						boardTiles[row][column].getComponent().add(piece.getImage());
						boardTiles[row][column].setPiece(piece);
						piece.setCurrentLocation(boardTiles[row][column]);
						column++;
						chessPieces.add(piece);
						loading.update();
					}
					else
					{
						Piece pieceLeft = new Piece(t, idCounter, width, height);
						idCounter++;
						Piece pieceRight = new Piece(t, idCounter, width, height);
						idCounter++;
						boardTiles[row][column].getComponent().add(pieceLeft.getImage());
						boardTiles[row][7 - column].getComponent().add(pieceRight.getImage());
						boardTiles[row][column].setPiece(pieceLeft);
						boardTiles[row][7 - column].setPiece(pieceRight);
						pieceLeft.setCurrentLocation(boardTiles[row][column]);
						pieceRight.setCurrentLocation(boardTiles[row][7 - column]);
						column++;
						chessPieces.add(pieceLeft);
						chessPieces.add(pieceRight);
						loading.update();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	//Enable screen and pack all components together
	public void packScreen()
	{
		loading.setMax();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		screen.setVisible(true);
		loading.close();
		screen.pack();
	}

	public Tile findTileByPiece(Piece p)
	{
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				if (!boardTiles[i][j].isEmpty() && boardTiles[i][j].getCurrentPiece().id == p.id) return boardTiles[i][j];
			}
		}
		return null;
	}


	//Returns the layered pane
	public JLayeredPane getLayeredPane()
	{
		return board;
	}

	public void setPiece(SelectedPiece sp)
	{
		selectedPiece = sp;
	}

	public Tile findTileByComponent(Component comp)
	{
		for (Tile[] t : boardTiles)
		{
			for (Tile tile : t)
			{
				if (tile.getComponent().equals(comp)){
					return tile;
				}
			}
		}
		return null;
	}

	public Piece findPieceByComponent(Component comp)
	{
		for (Piece piece : chessPieces)
		{
			if (piece.getImage().equals(comp)) {
				return piece;
			}
		}
		return null;
	}

	public Tile[][] getAllTiles()
	{
		return boardTiles;
	}

	public boolean isBlackEndGame()
	{
		for (Piece p : chessPieces)
		{
			if (p.getType().equals(Board.Type.B_Queen)) return false;
		}
		return true;
	}

	public boolean isWhiteEndGame()
	{
		for (Piece p : chessPieces)
		{
			if (p.getType().equals(Board.Type.W_Queen)) return false;
		}
		return true;
	}

	public List<Piece> getAllChessPieces(String player)
	{
		List<Piece> list = new ArrayList<Piece>();
		for (Piece piece : chessPieces)
		{
			if (piece.getType().toString().charAt(0) == player.charAt(0))
			{
				list.add(piece);
			}
		}
		return list;
	}

	public List<Piece> getAllChessPieces()
	{
		return chessPieces;
	}

	public boolean isWhiteInCheck()
	{
		return whiteInCheck;
	}

	public boolean isBlackInCheck()
	{
		return blackInCheck;
	}

	public void setWhiteInCheck(boolean b)
	{
		whiteInCheck = b;
	}

	public void setBlackInCheck(boolean b)
	{
		blackInCheck = b;
	}

	public void resetTileColors()
	{
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				boardTiles[i][j].setLegal(false);
				if ((j + i) % 2 == 0) boardTiles[i][j].getComponent().setBackground(Color.WHITE);
				else boardTiles[i][j].getComponent().setBackground(Color.BLACK);
			}
		}
	}

	public boolean causalCheck(Type t)
	{
		//Initiate all pieces to check for if king is checked
		for (Piece p : chessPieces)
		{
			p.getActions();
		}
		for (Piece p : chessPieces)
		{
			//Check only for correct black or white type
			if (p.getType().toString().charAt(0) == t.toString().charAt(0))
			{
				//Check if type is a king
				if (p.getType().equals(Type.B_King) || p.getType().equals(Type.W_King))
				{
					//Check if tile is legal for play
					return (p.getCurrentLocation().isLegal());
				}
			}
		}
		return false;
	}

	public synchronized boolean isCheckMate(char c)
	{
		//Reset tile legality first
		//resetTileLegality();
		boolean checkMate = true;
		//ACQUIRE MUTEX
		for (Piece p : chessPieces)
		{
			if (p.getType().toString().charAt(0) == c && p.getLegalActionsSize()) checkMate = false;
		}
		return checkMate;
	}

	public void resetTileLegality()
	{
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				boardTiles[i][j].setLegal(false);
			}
		}
	}

	public void fakeMove(Piece p, Tile t)
	{
		p.getCurrentLocation().setPiece(null);
		if (!t.isEmpty()) 
		{
			chessPieces.remove(t.getCurrentPiece());
		}
		t.setPiece(p);
		p.setCurrentLocation(t);
		switchTurn();
	}

	public void realMove(int[] locations)
	{
		double xLocation;
		double yLocation;
		double xGoal;
		double yGoal;
		double sleepTime;
		double straightLineDistance;
		double xOffset;
		double yOffset;
		Piece piece = getAllTiles()[locations[0]][locations[1]].getCurrentPiece();
		Tile tile = getAllTiles()[locations[2]][locations[3]];
		//Add move to history
		addToHistory(getTurn(), piece.getCurrentLocation().getArrayRowLocation(), piece.getCurrentLocation().getArrayColumnLocation(), tile.getArrayRowLocation(), tile.getArrayColumnLocation());
		//Remove old piece from old tile
		piece.getCurrentLocation().getComponent().remove(piece.getImage());
		piece.getCurrentLocation().setPiece(null);
		if (animateMoves.isSelected())
		{
			//Animate movement
			xLocation = piece.getCurrentLocation().getXLocation();
			yLocation = piece.getCurrentLocation().getYLocation();
			//Add piece to board
			screen.getLayeredPane().add(piece.getImage());
			screen.getLayeredPane().setLayer(piece.getImage(), 1);
			piece.getImage().setLocation((int)xLocation + options.getWidth(), (int)yLocation + menuPanel.getHeight());
			//Get goal location
			xGoal = tile.getXLocation();
			yGoal = tile.getYLocation();
			//Calculate distance
			straightLineDistance = Math.sqrt(((xGoal - xLocation)*(xGoal - xLocation)) + ((yGoal - yLocation) * (yGoal - yLocation)));
			//Calculate sleep time
			sleepTime = (500/straightLineDistance);
			//Calculate move offset
			yOffset = ((yGoal - yLocation) / (straightLineDistance));
			xOffset = ((xGoal - xLocation) / (straightLineDistance));
			//Move piece
			int count = 0;
			for (int i = 0; i < straightLineDistance; i++)
			{
				xLocation += xOffset;
				yLocation += yOffset;
				piece.getImage().setLocation((int)xLocation + options.getWidth(), (int)yLocation + menuPanel.getHeight());
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				count++;
			}
			//End animation
			screen.getLayeredPane().setLayer(piece.getImage(), 0);
		}
		//Check if pawn
		if (piece.getType().equals(Type.B_Pawn))
		{
			piece.pawnInit = false;
		}
		//Remove piece from new tile
		if (!tile.isEmpty())
		{
			tile.getComponent().remove(tile.getCurrentPiece().getImage());
			//Remove piece from play
			chessPieces.remove(tile.getCurrentPiece());
		}
		//Add new piece to new tile
		piece.setCurrentLocation(tile);
		tile.setPiece(piece);
		tile.getComponent().add(piece.getImage());
		//Repaint screen
		if (animateMoves.isSelected()) screen.validate();
		else screen.repaint();
		//Switch turn
		//Check if board is in checkmate
		switchTurn();
		if (isCheckMate(getTurn()))
		{
			textArea.append("Checkmate!\n");
			if (getTurn() == 'W') JOptionPane.showMessageDialog(null, "Game over!\nBlack wins!");
			else JOptionPane.showMessageDialog(null, "Game over!\nWhite wins!");
			System.exit(0);
		}
		//Reset tiles
		resetTileColors();
	}

	public void realMove(Piece p, Tile t)
	{
		//Find real tiles and real piece
		Piece piece = findPieceById(p);
		Tile tile = findTileById(t);

		//Check if pawn
		if (piece.getType().equals(Type.B_Pawn))
		{
			piece.pawnInit = false;
		}
		//Remove piece from new tile
		if (!tile.isEmpty())
		{
			tile.getComponent().remove(tile.getCurrentPiece().getImage());
			//Remove piece from play
			chessPieces.remove(tile.getCurrentPiece());
		}
		//Remove old piece from old tile
		piece.getCurrentLocation().getComponent().remove(piece.getImage());
		piece.getCurrentLocation().setPiece(null);
		//Add new piece to new tile
		piece.setCurrentLocation(tile);
		tile.setPiece(piece);
		tile.getComponent().add(piece.getImage());
		//Reset tiles
		resetTileColors();
		//Repaint screen
		screen.repaint();
		//Switch turn
		//Check if board is in checkmate
		switchTurn();
		if (isCheckMate(getTurn()))
		{
			if (getTurn() == 'W') JOptionPane.showMessageDialog(null, "Game over!\nBlack wins!");
			else JOptionPane.showMessageDialog(null, "Game over!\nWhite wins!");
			System.exit(0);
		}
	}

	public Tile findTileById(Tile t)
	{
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				if (boardTiles[i][j].id == t.id) return boardTiles[i][j];
			}
		}
		return null;
	}

	public Piece findPieceById(Piece p)
	{
		for (Piece piece : chessPieces)
		{
			if (piece.id == p.id) return piece;
		}
		return null;
	}

	public void printBoard()
	{
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				if (!boardTiles[i][j].isEmpty()) System.out.print(boardTiles[i][j].getCurrentPiece().getType().toString() + " ");
				else System.out.print(" 0 ");
			}
			System.out.println();
		}
	}

	public char getTurn()
	{
		return turn;
	}

	public void switchTurn()
	{
		if (menuPanel != null)
		{
			for (Component comps : menuPanel.getComponents())
			{
				if (comps.isEnabled()) comps.setEnabled(false);
				else comps.setEnabled(true);
			}
		}
		if (turn == 'W') turn = 'B';
		else turn = 'W';
		//Reset tile legality, mostly for king plays
		resetTileLegality();
	}

	public Board getBoard()
	{
		return this;
	}

	public void setOpponent(ChessAI opponent)
	{
		this.opponent = opponent;
	}

	private void addToHistory(char turn, int arrayRowLocation, int arrayColumnLocation, int arrayRowLocation2, int arrayColumnLocation2)
	{
		textArea.append(turn + ": " + (char)(arrayColumnLocation + 65) + (8 - arrayRowLocation) + " " + (char)(arrayColumnLocation2 + 65) + (8 - arrayRowLocation2) + "\n");
	}

	public class MouseMotion extends MouseInputAdapter implements MouseMotionListener
	{

		public void mouseClicked(MouseEvent e)
		{
			boolean madeMove = false;
			//Disable glass pane
			glassPane.setVisible(false);
			//Move piece back to base layer
			board.setLayer(selectedPiece.getSelectedPiece(), 0);
			//Find tile mouse is hovering over
			Tile newSpot = findTileByComponent(board.getComponentAt(e.getX() - options.getWidth(), e.getY() - menuPanel.getHeight()));
			//Find piece associated with the selected piece
			Piece piece = findPieceByComponent(selectedPiece.getSelectedPiece());
			//Make sure newspot is a legal action
			if (newSpot.isLegal())
			{
				if (newSpot.isEmpty())
				{
					//Tell move to move history
					addToHistory(getTurn(), piece.getCurrentLocation().getArrayRowLocation(), piece.getCurrentLocation().getArrayColumnLocation(), newSpot.getArrayRowLocation(), newSpot.getArrayColumnLocation());
					//Create relationship between newspot and piece
					newSpot.setPiece(piece);
					piece.setCurrentLocation(newSpot);
					//Add piece to tile
					newSpot.getComponent().add(piece.getImage());
					if (piece.getType() == Type.B_Pawn || piece.getType() == Type.W_Pawn) piece.pawnInit = false;
					//Say made legal move
					madeMove = true;
				}
				//Make sure newspot is either empty, or of opposing piece
				else if (!newSpot.isEmpty() && piece.areEnemies(piece, newSpot.getCurrentPiece()))
				{
					//newspot contains enemy
					//Remove piece from playing pieces
					chessPieces.remove(newSpot.getCurrentPiece());
					//Remove enemy from block
					newSpot.getComponent().remove(newSpot.getCurrentPiece().getImage());
					//Set relationship between piece and newspot
					newSpot.setPiece(piece);
					piece.setCurrentLocation(newSpot);
					//Add piece to the board
					newSpot.getComponent().add(piece.getImage());
					//Say made move
					madeMove = true;
					//newspot contains friendly
				}
				//Set opponent's turn
				switchTurn();
				//Check for checkmate
				if (isCheckMate(getTurn()))
				{
					textArea.append("Checkmate!\n");
					if (getTurn() == 'W') JOptionPane.showMessageDialog(null, "Game over!\nBlack wins!");
					else JOptionPane.showMessageDialog(null, "Game over!\nWhite wins!");
					System.exit(0);
				}
			}
			else
			{
				//Put back in old location
				piece.getCurrentLocation().getComponent().add(piece.getImage());
				piece.getCurrentLocation().setPiece(piece);
			}
			//Reset current piece
			setPiece(null);
			//Reset all colors
			resetTileColors();
			//Check if made move
			if (madeMove)
			{
				//Activity AI search
				Thread t = new Thread(opponent);
				opponent.setBoard(getBoard());
				opponent.setMaxDepth(slider.getValue());
				opponent.setMaxTime(slider_1.getValue());
				t.start();
			}
		}

		@Override
		public void mouseDragged(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			if (selectedPiece.isPieceSelected())
			{
				//JLabel p = selectedPiece.getSelectedPiece();
				selectedPiece.getSelectedPiece().setLocation(e.getX() - selectedPiece.getSelectedPieceXOffset() - options.getWidth(), e.getY() - selectedPiece.getSelectedPieceYOffset() - menuPanel.getHeight());
			}
		}
	}
	//Currently selected chess piece
	public class SelectedPiece
	{

		private boolean pieceSelected = false;
		private JLabel selectedPiece = null;
		private int selectedPieceXOffset = 0;
		private int selectedPieceYOffset = 0;

		public SelectedPiece(JLabel piece, int xOffset, int yOffset)
		{
			setSelectedPiece(piece);
			setSelectedPieceXOffset(xOffset);
			setSelectedPieceYOffset(yOffset);
			pieceSelected = true;
			glassPane.setVisible(true);
		}

		public boolean isPieceSelected()
		{
			return pieceSelected;
		}

		public int getSelectedPieceYOffset() {
			return selectedPieceYOffset;
		}

		public void setSelectedPieceYOffset(int selectedPieceYOffset) {
			this.selectedPieceYOffset = selectedPieceYOffset;
		}

		public int getSelectedPieceXOffset() {
			return selectedPieceXOffset;
		}

		public void setSelectedPieceXOffset(int selectedPieceXOffset) {
			this.selectedPieceXOffset = selectedPieceXOffset;
		}

		public JLabel getSelectedPiece() {
			return selectedPiece;
		}

		public void setSelectedPiece(JLabel selectedPiece) {
			this.selectedPiece = selectedPiece;
		}
	}

	class SliderChanger implements ChangeListener
	{
		JSlider slider;
		JLabel label;
		SliderChanger(JLabel label, JSlider slider)
		{
			this.slider = slider;
			this.label = label;
		}

		@Override
		public void stateChanged(ChangeEvent arg0) {
			// TODO Auto-generated method stub
			label.setText(String.valueOf(this.slider.getValue()));
		}

	}
}
