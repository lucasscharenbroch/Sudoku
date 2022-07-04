package Sudoku;

public class Main {
	
	public static void main(String[] args) {
		SudokuBoard board = new SudokuBoard();
		UserInterface ui = new UserInterface(board);
		ui.showGui();
	}
	
}
