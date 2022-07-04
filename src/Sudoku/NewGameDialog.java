package Sudoku;


import java.awt.*;

import javax.swing.*;

public class NewGameDialog extends JDialog {
	private JFrame parent; 
	private Difficulty difficulty;
	
	private int setUpDifficultyRadioButtons(int currentGridRow) {

		JRadioButton trivialButton = GuiUtil.createRadioButton("Trivial", 15, e -> difficulty = Difficulty.TRIVIAL);
		add(trivialButton, GuiUtil.createGBC(0, currentGridRow++));

		JRadioButton easyButton = GuiUtil.createRadioButton("Easy", 15, e -> difficulty = Difficulty.EASY);
		add(easyButton, GuiUtil.createGBC(0, currentGridRow++));

		JRadioButton mediumButton = GuiUtil.createRadioButton("Medium", 15, e -> difficulty = Difficulty.MEDIUM);
		add(mediumButton, GuiUtil.createGBC(0, currentGridRow++));

		JRadioButton hardButton = GuiUtil.createRadioButton("Hard", 15, e -> difficulty = Difficulty.HARD);
		add(hardButton, GuiUtil.createGBC(0, currentGridRow++));

		JRadioButton extremeButton = GuiUtil.createRadioButton("Extreme", 15, e -> difficulty = Difficulty.EXTREME);
		add(extremeButton, GuiUtil.createGBC(0, currentGridRow++));

		ButtonGroup difficultyRadios = new ButtonGroup();
		difficultyRadios.add(trivialButton);
		difficultyRadios.add(easyButton);
		difficultyRadios.add(mediumButton);
		difficultyRadios.add(hardButton);
		difficultyRadios.add(extremeButton);
		
		// default = medium
		mediumButton.setSelected(true);
		difficulty = Difficulty.MEDIUM;

		return currentGridRow;
	}

	public NewGameDialog(SudokuFrame parent) {
		super(parent, "New Game");
		this.parent = parent;
		setLayout(new GridBagLayout());
		
		setSize(new Dimension(400, 400));
		setResizable(false);
		
		int currentGridRow = 0;
		
		JLabel promptLabel = new JLabel("Select a difficulty: ");
		promptLabel.setFont(GuiUtil.boldFontWithSize(30));
		add(promptLabel, GuiUtil.createGBC(0, currentGridRow++));
		
		add(GuiUtil.createSpacer(0, 25), GuiUtil.createGBC(0, currentGridRow++));
		
		currentGridRow = setUpDifficultyRadioButtons(currentGridRow);

		add(GuiUtil.createSpacer(0, 25), GuiUtil.createGBC(0, currentGridRow++));
		
		JPanel cancelOkPanel = new JPanel();
		add(cancelOkPanel, GuiUtil.createGBC(0, currentGridRow++));
		

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> dispose());
		cancelOkPanel.add(cancelButton, GuiUtil.createGBC(0,  0));
		
		cancelOkPanel.add(GuiUtil.createSpacer(30, 0), GuiUtil.createGBC(1, 0));
		
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(e -> {
			parent.gameBoardPanel.reset();
			parent.gameBoardPanel.setNewBoard(difficulty);
			parent.gameBoardPanel.update();
			dispose();
		}
		);
		cancelOkPanel.add(okButton, GuiUtil.createGBC(2, 0));

		setModal(true); // cannot leave focus
		setVisible(true);
	}
}
