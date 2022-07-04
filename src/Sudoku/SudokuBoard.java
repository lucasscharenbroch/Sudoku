package Sudoku;

import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

/**
 * represents a difficultly level of a Sudoku game
 */
enum Difficulty{
	TRIVIAL (30), EASY (35), MEDIUM (40),
	HARD (45), EXTREME (50);
	
	private int numBlanks;
	
	private Difficulty(int blanks) {
		numBlanks = blanks;
	}
	
	public int getNumBlanks() {
		return numBlanks;
	}
}

/** predefines the abstracted behavior of SudokuBoard. */
interface GameBoard {
	/** 
	 * creates a new game board 
	 * @param diff - the requested difficulty for the game
	 */
	void newBoard(Difficulty diff);

	/** 
	 * attempts to place guess at (row, col)
	 * @param guess - the player's numerical guess for (row, col) 
	 * @param row - the cell's row (in the board)
	 * @param col - the cell's column (in the board)
	 * @return - was the guess placed successfully?
	 */ 
	boolean putGuess(int guess, int row, int col);

	/** @return - the number at the position (row, col) */
	int getDigitAt(int row, int col);
	
	/**
	 * checks if the cell is permanent (cannot be modified) 
	 * @param row - the cell's row (in the board)
	 * @param col - the cell's column (in the board)
	 * @return - is the cell permanent?
	 */
	boolean isCellPermanent(int row, int col);

	/** 
	 * checks each cell on the board for validity
	 * @return - returns an array, representing each cell's validity 
	 * (true = valid, false = invalid)
	 */
	boolean[][] check();
	
	/**
	 * solves the board, overwriting any incorrect digits.
	 */
	void solve();
}

/**
 * holds the data of a Sudoku Board and provides
 * methods for user interaction with that board. 
 */
public class SudokuBoard implements GameBoard{
	/**
	 * checks if the num is permanent.
	 * @param num - the num in question
	 * @return - is the num permanent?
	 */
	private boolean isPermanent(int num) {
		return num > 9;
	}

	/**
	 * converts num to permanent (adds 10)
	 * @param num - the number to convert to permament
	 * @return - the converted num 
	 */
	private int permifyDigit(int digit) {
		return (isPermanent(digit)) ? digit : digit + 10;
	}
	
	/**
	 * converts num from permanent to non-permanent (subtracts 10)
	 * @param num - the permanent number to revert
	 * @return - the converted num 
	 */
	private int UnpermifyDigit(int digit) {
		return (isPermanent(digit)) ? digit - 10 : digit;
	}
	
	/**
	 * the game board- holds digits. 
	 * a digit that is greater than 9 represents
	 * a "permanent" digit- one placed there by the
	 * game, not the player. (Permanent digits are 10
	 * higher than their non-permanent counterparts.)
	 * 0's represent blank spaces.
	 */
	private int[][] board;

	{
		board = new int[9][9];
	}
	
	/**
	 * Contains information about a "box" (3x3 square
	 * of cells within the board that can only contain
	 * one of each digit)
	 */
	private class Box {
		private int startRow;
		private int startCol; 

		/**
		 * creates an instance of Box that holds the coordinates
		 * of the box that encloses the cell
		 * 
		 * @param row - the cell's row (in the board)
		 * @param col - the cell's column (in the board)
		 */
		public Box(int row, int col) {
			startRow = (row / 3) * 3;
			startCol = (col / 3) * 3;
		}
		
		/**
		 * @return - an array of row indices included in the box.
		 */
		public int[] getRows() {
			int[] rows = {startRow, startRow+1, startRow+2};
			return rows;
		}
		
		/**
		 * @return - an array of col indices included in the box.
		 */
		public int[] getCols() {
			int[] cols = {startCol, startCol+1, startCol+2};
			return cols;
		}
		
	}
	
	/**
	 * checks if the given cell (row, col) on the board
	 * holds a number that is valid (according to the game
	 * rules) 
	 * @param row - the cell's row (in the board)
	 * @param col - the cell's column (in the board)
	 * @return - is the cell's number valid?
	 */
	private boolean isCellValid(int row, int col) {
		int digit = getDigitAt(row, col);
		
		if(digit == 0) { // a blank cell is always valid
			return true;
		}

		// check for shared digits in row
		for(int c = 0; c < 9; c++) {
			if(c != col && getDigitAt(row, c) == digit) { // shared digit!
				return false;
			}
		}
		
		// check for shared digits in column
		for(int r = 0; r < 9; r++) {
			if(r != row && getDigitAt(r, col) == digit) { // shared digit!
				return false;
			}
		}
		
		// check for shared digits in box
		Box enclosingBox = new Box(row, col);
		for(int r : enclosingBox.getRows()) {
			for(int c : enclosingBox.getCols()) {
				if(r == row && c == col) // ignore the cell itself
					continue;
				
				if(getDigitAt(r, c) == getDigitAt(row, col)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * checks if the digit in board[row][col] is the only
	 * possible digit to be placed in that location.
	 * (and thus the digit can be removed from the board
	 * without adding to the board's possible solutions)
	 * <p>
	 * 
	 * 
	 * @param row - the cell's row (in the board)
	 * @param col - the cell's column (in the board)
	 * @return - is the digit the only possible digit for this position?
	 */
	private boolean isCellRemovable(int row, int col) {
		int digit = getDigitAt(row, col);
		board[row][col] = 0; // prevent false collisions
		boolean isRequiredInRow = true;
		boolean isRequiredInCol = true; 
		boolean isRequiredInBox = true;
		
		// check cells in the same column for other valid positions
		for(int r = 0; r < 9; r++) {
			if(r == row) // skip the position already known to be valid
				continue;
			
			if(getDigitAt(r, col) == 0) {
				// test the position
				board[r][col] = digit;
				if(isCellValid(r, col)) { // another valid position found
					isRequiredInRow = false;
				}
				board[r][col] = 0;
			}
		}
		
		// check cells in the same row
		for(int c = 0; c < 9; c++) {
			if(c == col)
				continue;
			
			if(getDigitAt(row, c) == 0) {
				board[row][c] = digit;
				if(isCellValid(row, c)) {
					isRequiredInCol = false;
				}
				board[row][c] = 0;
			}
			
		}
		
		// check box
		Box enclosingBox = new Box(row, col);
		for(int r : enclosingBox.getRows()) {
			for(int c : enclosingBox.getCols()) {
				if(r == row && col == c)
					continue;
				
				if(getDigitAt(r, c) == 0) {
					board[r][c] = digit;
					if(isCellValid(r, c)) {
						isRequiredInBox = false;
					}
					board[r][c] = 0;
				}
			}
		}
		
		
		board[row][col] = digit;
		return isRequiredInRow || isRequiredInCol || isRequiredInBox;
	}
	
	/**
	 * uses backtracking to fill board with random (but valid) numbers.
	 * <p> 
	 * starts at the top left, and for each cell, it generates
	 * an array of digits that have not yet been used in that row.
	 * the array is shuffled, then iterated through, checking if
	 * any number can legally be placed in that position. If the
	 * number can be placed there, it attempts to move on. If not,
	 * it backtracks.
	 * 
	 * @return - success?
	 */
	private boolean randomizeBoard(int currentRow, int currentCol) {
		if(currentRow == 9) //the entire board is randomized
			return true;

		ArrayList<Integer> possibleDigits = new ArrayList<Integer>(10);
		for(int i = 1; i < 10; i++) {
			possibleDigits.add(i);
		}
		
		for(int c = 0; c < currentCol; c++) {
			possibleDigits.remove(possibleDigits.indexOf(board[currentRow][c])); 
			// this works for removing small integers because of object interning
		}
		
		Collections.shuffle(possibleDigits);
		
		int nextCellsRow = (currentCol == 8) ? currentRow + 1 : currentRow;
		int nextCellsCol = (currentCol == 8) ? 0 : currentCol + 1;

		for(int digit : possibleDigits) {
			board[currentRow][currentCol] = digit;
			if(isCellValid(currentRow, currentCol)) {
				if(randomizeBoard(nextCellsRow, nextCellsCol)) { // attempt to continue
					return true;
				}
				// otherwise try next digit
			} 
		}
		board[currentRow][currentCol] = 0; // prevent false shared-digits when backtracking
		return false;
	}
	
	private boolean randomizeBoard() {
		board = new int[9][9]; // clear board
		return randomizeBoard(0, 0);
	}
	
	private Random rand = new Random(); // used by randInt to generate random numbers
	/**
	 * generates a pseudorandom integer between 0 (inclusive) and bound (exclusive) 
	 * @param bound - non-inclusive top bound of the random number
	 * @return - the pseudorandom number
	 */
	private int randInt(int bound) {
		return rand.nextInt(bound);
	}
	
	/**
	 * attempts to remove a number of "removable digits"
	 * (digits whose removal does not add another possible solution)
	 * @param numDigitsToRemove - number of digits to try to remove
	 * @return - the number of digits successfully removed
	 */
	private int removeDigits(int numDigitsToRemove) {
		removeAnother:
		for(int i = 0; i < numDigitsToRemove; i++) {
			// start at a random cell
			int firstAttemptRow = randInt(9); 
			int firstAttemptCol = randInt(9);
			
			int row = firstAttemptRow;
			int col = firstAttemptCol;
			
			// iterate through all cells until a digit can be removed
			do {
				if(isCellRemovable(row, col)) {
					board[row][col] = 0;
					continue removeAnother;
				}
				row = (col == 8) ? row + 1 : row;
				col = (col == 8) ? 0 : col + 1;

				if(row == 9) { // loop back to (0,0) when (8,8) reached
					row = 0;
					col = 0;
				}
			} while(!(row == firstAttemptRow && col == firstAttemptCol));
			return i; // no more cells can be removed
		}
		return numDigitsToRemove; // all digits successfully removed
	}
	
	
	/**
	 * sets all of the boards non-zero digits 
	 * to permanent (adds 10) - thus they cannot
	 * be modified with putGuess()  
	 */
	private void permifyBoard() {
		for(int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				if(board[r][c] != 0) {
					board[r][c] = permifyDigit(board[r][c]);
				}
			}
		}
	}
	
	/** 
	 * creates a new game board 
	 * @param diff - the requested difficulty for the game
	 */
	public void newBoard(Difficulty diff) {
		randomizeBoard();
		removeDigits(diff.getNumBlanks());
		permifyBoard();
	}
	
	/**
	 * changes any non-permanent (user-entered) digits to zero. 
	 */
	private void clearAllNonPermanentCells() {
		for(int r = 0; r < 9; r++) {
			for(int c = 0; c < 9; c++) {
				if(!isCellPermanent(r, c)) {
					board[r][c] = 0;
				}
			}
		}
	}
	
	/** 
	 * attempts to place guess at (row, col)
	 * @param guess - the player's numerical guess for (row, col) 
	 * @param row - the cell's row (in the board)
	 * @param col - the cell's column (in the board)
	 * @return - was the guess placed successfully?
	 */ 
	public boolean putGuess(int guess, int row, int col) {
		if(isPermanent(board[row][col])) {
			return false;
		}
		board[row][col] = guess;
		return true;
	}

	/** @return - the digit at the position (row, col) */
	public int getDigitAt(int row, int col) {
		return UnpermifyDigit(board[row][col]);
	}

	/**
	 * checks if the cell is permanent (cannot be modified) 
	 * @param row - the cell's row (in the board)
	 * @param col - the cell's column (in the board)
	 * @return - is the cell permanent?
	 */
	public boolean isCellPermanent(int row, int col) {
		return isPermanent(board[row][col]);
	}
	
	/** 
	 * checks each cell on the board for validity
	 * @return - returns an array, representing each cell's validity 
	 * (true = valid, false = invalid)
	 */
	public boolean[][] check() {
		boolean[][] validityGrid = new boolean[9][9];
		
		for(int r = 0; r < 9; r++) {
			for(int c = 0; c < 9; c++) {
				validityGrid[r][c] = isCellValid(r, c);
			}
		}

		return validityGrid;
	}
	
	/**
	 * solves the board, overwriting any incorrect digits.
	 * <p>
	 * iterates through the board, stopping at blank (0) digits.
	 * digits 1-9 are checked for validity (the number is allowed to
	 * go in that position), then removability (the number must go in that position)
	 * (validity is a prerequisite to the removability check)
	 * 
	 * if both are fufilled, the correct digit is placed in board.
	 */
	private void solve(int startRow, int startCol) {
		//adjust out-of-bounds start positions
		if(startCol > 8) {
			startRow++;
			startCol = 0;
		}
		if(startRow > 8) {
			solve(0, 0);
			return;
		}

		int emptyCellRow = 0;
		int emptyCellCol = 0;

		// find the first empty cell
		boolean emptyCellFound = false;
		findEmptyCell:
		for(int r = startRow; r < 9; r++) {
			for(int c = startCol; c < 9; c++) {
				if(board[r][c] == 0) {
					emptyCellFound = true;
					emptyCellRow = r;
					emptyCellCol = c;
					break findEmptyCell;
				}
			}
			startCol = 0; // reset start col after one row
		}

		if(!emptyCellFound) {
			if(startRow == 0 && startCol == 0) { // board is solved
				return;
			} else {
				solve(0, 0); // check for empty cells higher on the board
				return;
			}
		}
		
		// attempt to find the correct digit for the empty cell
		for(int possDigit = 1; possDigit <= 9; possDigit++) {
			board[emptyCellRow][emptyCellCol] = possDigit;
			if(isCellValid(emptyCellRow, emptyCellCol) &&
			   isCellRemovable(emptyCellRow, emptyCellCol)) {
				// the cell is now correctly filled
				solve(0, 0); // solve the rest of the board
				return;
			}
		}
		
		// couldn't find a removable digit
		board[emptyCellRow][emptyCellCol] = 0; // reset the empty cell
		solve(emptyCellRow, emptyCellCol + 1); // look for the next empty cell
	}
	
	/**
	 * sets up solve by clearing all user-entered (non-permanent) digits
	 * and calling solve(0, 0) (begins solving at row = 0 and col = 0)
	 */
	public void solve() {
		clearAllNonPermanentCells();
		solve(0, 0);
	}
	
}
