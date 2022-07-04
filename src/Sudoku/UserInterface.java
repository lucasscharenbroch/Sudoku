package Sudoku;

import java.awt.*;

import javax.swing.*;

public class UserInterface {
	
	private SudokuBoard board;

	public void showGui() {
		new SudokuFrame(board);
	}
	
	public UserInterface(SudokuBoard board) {
		this.board = board;
	}
}

class SudokuFrame extends JFrame {
	public BoardPanel gameBoardPanel;
	public ButtonsPanel gameButtonsPanel;
	
	public SudokuFrame(SudokuBoard board) {
		setTitle("Sudoku");
		setMinimumSize(new Dimension(1000, 700));
		setResizable(false);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());
		
		ImageIcon icon = new ImageIcon("Icon.png");
		setIconImage(icon.getImage());
		
		gameBoardPanel = new BoardPanel(board, this);
		add(gameBoardPanel, GuiUtil.createGBC(0, 0));
		new BoardKeyBindings(gameBoardPanel);
		
		add(GuiUtil.createSpacer(100, 0), GuiUtil.createGBC(1, 0));
		
		gameButtonsPanel = new ButtonsPanel(board, this);
		add(gameButtonsPanel, GuiUtil.createGBC(2, 0));

		setVisible(true);
	}
}

class BoardPanel extends JPanel {
	public SudokuFrame parent;

	private SudokuBoard board;
	private boolean boardInitialized = false;
	
	private enum Correctness {
		NEUTRAL, CORRECT, INCORRECT
	}

	JLabel[][] labelArray = new JLabel[9][9];
	boolean[][] cellSelections = new boolean[9][9];
	Correctness[][] cellCorrectness = new Correctness[9][9];
	JPanel[] boxArray = new JPanel[9];
	
	
	private int getBoxNumber(int row, int col) {
		row /= 3;
		col /= 3;
		
		return row * 3 + col;
	}
	
	private int lastSelectedRow = 0;
	private int lastSelectedCol = 0;

	public void moveSelection(String direction, boolean keepOtherCellsSelected) {
		switch(direction) {
			case "UP" :
				lastSelectedRow = (lastSelectedRow - 1 + 9) % 9;
				break;
			case "DOWN" :
				lastSelectedRow = (lastSelectedRow + 1 + 9) % 9;
				break;
			case "LEFT" :
				lastSelectedCol = (lastSelectedCol - 1 + 9) % 9;
				break;
			case "RIGHT" :
				lastSelectedCol = (lastSelectedCol + 1 + 9) % 9;
				break;
		}
		if(!keepOtherCellsSelected) {
			clearSelections();
		}
		setSelected(lastSelectedRow, lastSelectedCol);
		update();
	}
	
	public void setSelected(int r, int c) {
		lastSelectedRow = r;
		lastSelectedCol = c;
		cellSelections[r][c] = true;
	}
	
	public void setUnselected(int r, int c) {
		cellSelections[r][c] = false;
	}
	
	public void swapSelection(int r, int c) {
		if(isSelected(r, c)) {
			setUnselected(r, c);
		} else {
			setSelected(r, c);
		}
	}
	
	public boolean isSelected(int r, int c) {
		return cellSelections[r][c];
	}
	
	public void clearSelections() {
		for(int r = 0; r < 9; r++) {
			for(int c= 0; c < 9; c++) {
				cellSelections[r][c] = false;
			}
		}
	}
	
	public void fillSelectedCells(int digit) {
		for(int r = 0; r < 9; r++) {
			for(int c = 0; c < 9; c++) {
				if(cellSelections[r][c]) {
					board.putGuess(digit, r, c);
					update(r, c);
				}
			}
		}
	}
	
	// color constants
	private final Color lightBlue = new Color(0xADD8E6);
	private final Color red = new Color(0xFF00000);
	private final Color green = new Color(0x00FF00);

	private Color getCellColor(int r, int c) {
		if(cellSelections[r][c]) {
			return lightBlue;
		} else if(cellCorrectness[r][c] != Correctness.NEUTRAL) {
			if(cellCorrectness[r][c] == Correctness.CORRECT) {
				return green;
			} else {
				return red;
			}
		} else {
			return GuiUtil.defaultColor; 
		}
	}
	
	public void reset() {
		for(int r = 0; r < 9; r++) {
			for(int c = 0; c < 9; c++) {
				cellSelections[r][c] = false;
				cellCorrectness[r][c] = Correctness.NEUTRAL;
			}
		}
	}
	
	public void update(int r, int c) {
		int cellValue = board.getDigitAt(r, c);
		JLabel cellLabel = labelArray[r][c];
		
		// font weight
		if(board.isCellPermanent(r, c)) {
			cellLabel.setFont(GuiUtil.boldFontWithSize(30));
		} else {
			cellLabel.setFont(GuiUtil.plainFontWithSize(30));
		}

		// text
		if(cellValue == 0) {
			cellLabel.setText("");
		} else {
			cellLabel.setText(String.valueOf(cellValue));
		}
		
		// color
		cellLabel.setBackground(getCellColor(r, c));
	}

	public void update() {
		for(int r = 0; r < 9; r++) {
			for(int c = 0; c < 9; c++) {
				update(r, c);
			}
		}
	}
	
	public void solveBoard() {
		if(!boardInitialized) // can't solve a blank board
			return;

		board.solve();
		update();
	}
	
	public void setNewBoard(Difficulty diff) {
		boardInitialized = true;
		board.newBoard(diff);
	}
	
	public void checkBoard() {
		boolean[][] checkedBoard = board.check();
		
		for(int r = 0; r < 9; r++) {
			for(int c = 0; c < 9; c++) {
				if(board.getDigitAt(r, c) == 0 || board.isCellPermanent(r, c)) { // ignore blank and given cells
					cellCorrectness[r][c] = Correctness.NEUTRAL;
				} else if(checkedBoard[r][c]) { // correct
					cellCorrectness[r][c] = Correctness.CORRECT;
				} else {
					cellCorrectness[r][c] = Correctness.INCORRECT;
				}
			}
		}
		
		update();
	}

	public BoardPanel(SudokuBoard board, SudokuFrame parent) {
		this.board = board;
		this.parent = parent;

		setLayout(new GridBagLayout());
		
		for(int l = 0; l < 9; l++) { // instantiate and set up 9 boxes (bolded subsections of the board)
			boxArray[l] = new JPanel(new GridBagLayout());
			boxArray[l].setVisible(true);
			boxArray[l].setBorder(GuiUtil.createOutsideBorder(Color.black));
			add(boxArray[l], GuiUtil.createGBC(l % 3, l / 3));
		}


		// number buttons
		for(int r = 0; r < 9; r++) {
			for(int c = 0; c < 9; c++) {
				JLabel label = new JLabel("", SwingConstants.CENTER);
				label.setPreferredSize(new Dimension(60, 60));
				label.setBorder(GuiUtil.createOutsideBorder(Color.black, 1));
				label.addMouseListener(new CellMouseListener(this, r, c));
				label.setOpaque(true); // make background color visible
				boxArray[getBoxNumber(r, c)].add(label, GuiUtil.createGBC(c % 3, r % 3));
				labelArray[r][c] = label;
			}
		}

		reset(); // clear all selections and highlights
		setVisible(true);
	}
}

class ButtonsPanel extends JPanel {
	
	ButtonsPanel(SudokuBoard board, SudokuFrame parent) {
		this.setVisible(true);
		this.setLayout(new GridBagLayout());
		int currentGridRow = 0;
		
		JButton clearCellButton = GuiUtil.createButton("Clear Cell", 200, 50, 25, 
				                                   e -> parent.gameBoardPanel.fillSelectedCells(0));
		GuiUtil.addToGrid(this, clearCellButton, 0, currentGridRow++, 3, 1);
		
		GuiUtil.addToGrid(this, GuiUtil.createSpacer(0, 10), 0, currentGridRow++, 3, 1);
		
		// number buttons
		for(int i = 0; i < 9; i++) {
			final int guess = i + 1;
			JButton numberButton = GuiUtil.createButton(String.valueOf(i+ 1), 70,  70, 40, 
					                                e -> parent.gameBoardPanel.fillSelectedCells(guess));
			GuiUtil.addToGrid(this, numberButton, i % 3, currentGridRow + i / 3);
		}
		currentGridRow += 3;

		GuiUtil.addToGrid(this, GuiUtil.createSpacer(0, 50), 0, currentGridRow++, 3, 1);
		
		JButton newGameButton = GuiUtil.createButton("New Game", 200, 50, 25, e -> new NewGameDialog(parent));
		GuiUtil.addToGrid(this, newGameButton,  0, currentGridRow++, 3, 1);

		GuiUtil.addToGrid(this, GuiUtil.createSpacer(0, 20), 0, currentGridRow++, 3, 1);
		
		JButton solveButton = GuiUtil.createButton("Solve", 200, 50, 25, e -> parent.gameBoardPanel.solveBoard());
		GuiUtil.addToGrid(this, solveButton, 0, currentGridRow++, 3, 1);

		GuiUtil.addToGrid(this, GuiUtil.createSpacer(0, 20), 0, currentGridRow++, 3, 1);

		JButton checkButton = GuiUtil.createButton("Check", 200, 50, 25, e -> parent.gameBoardPanel.checkBoard());
		GuiUtil.addToGrid(this, checkButton, 0, currentGridRow++, 3, 1);

		GuiUtil.addToGrid(this, GuiUtil.createSpacer(0, 20), 0, currentGridRow++, 3, 1);
		
		JButton uncheckButton = GuiUtil.createButton("Uncheck", 200, 50, 25, e -> {
			parent.gameBoardPanel.reset();
			parent.gameBoardPanel.update();
		});
		GuiUtil.addToGrid(this,  uncheckButton, 0, currentGridRow++, 3, 1);
	}
}