package Sudoku;

import java.awt.event.*;

import javax.swing.*;

interface ActionEventHandler {
	void handle(ActionEvent e);
}

public class BoardKeyBindings {
	private boolean isControlDown;
	private BoardPanel boardPanel;

	private void addKeyBinding(String keyStrokeName, AbstractAction abstractAction) {
		boardPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(keyStrokeName), keyStrokeName);
		boardPanel.getActionMap().put(keyStrokeName, abstractAction);
	}
	
	private void addKeyBinding(String keyStrokeName, ActionEventHandler handler) {
		addKeyBinding(keyStrokeName, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handler.handle(e);
			}
		});
	}

	public BoardKeyBindings(BoardPanel bp) {
		boardPanel = bp;
		
		for(int n = 0; n < 10; n++) {
			final int guess = n;
			addKeyBinding(String.valueOf(n), e -> bp.fillSelectedCells(guess));
		}
		
		// destructive navigation
		addKeyBinding("UP", e -> bp.moveSelection("UP", false));
		addKeyBinding("DOWN", e -> bp.moveSelection("DOWN", false));
		addKeyBinding("LEFT", e -> bp.moveSelection("LEFT", false));
		addKeyBinding("RIGHT", e -> bp.moveSelection("RIGHT", false));

		addKeyBinding("typed k", e -> bp.moveSelection("UP", false));
		addKeyBinding("typed j", e -> bp.moveSelection("DOWN", false));
		addKeyBinding("typed h", e -> bp.moveSelection("LEFT", false));
		addKeyBinding("typed l", e -> bp.moveSelection("RIGHT", false));
		
		// non-destructive navigation
		//(shift)

		addKeyBinding("shift UP", e -> bp.moveSelection("UP", true));
		addKeyBinding("shift DOWN", e -> bp.moveSelection("DOWN", true));
		addKeyBinding("shift LEFT", e -> bp.moveSelection("LEFT", true));
		addKeyBinding("shift RIGHT", e -> bp.moveSelection("RIGHT", true));

		addKeyBinding("typed K", e -> bp.moveSelection("UP", true));
		addKeyBinding("typed J", e -> bp.moveSelection("DOWN", true));
		addKeyBinding("typed H", e -> bp.moveSelection("LEFT", true));
		addKeyBinding("typed L", e -> bp.moveSelection("RIGHT", true));
		
		// hotkeys
		addKeyBinding("control N", e -> new NewGameDialog(bp.parent));
		addKeyBinding("control S", e -> bp.solveBoard());
		addKeyBinding("control C", e -> bp.checkBoard());
	}
}
