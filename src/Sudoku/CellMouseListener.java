package Sudoku;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class CellMouseListener implements MouseListener {
	private static boolean mouseButtonDown = false;

	private BoardPanel boardPanel;
	private int row;
	private int col;
	private boolean wasSelected;

	
	CellMouseListener(BoardPanel parent, int r, int c) {
		boardPanel = parent;
		row = r;
		col = c;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.isControlDown()) {
			boardPanel.setSelected(row, col);
			return;
		}

		boardPanel.clearSelections();

		if(!wasSelected) {
			boardPanel.setSelected(row,  col);
		}

		boardPanel.update();
		wasSelected = !wasSelected;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseButtonDown = true;
		boardPanel.setSelected(row, col);
		boardPanel.update(row, col);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseButtonDown = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		wasSelected = boardPanel.isSelected(row, col);
		if(mouseButtonDown) {
			boardPanel.setSelected(row, col);
			boardPanel.update(row, col);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
