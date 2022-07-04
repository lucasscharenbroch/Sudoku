package Sudoku;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

public class GuiUtil {
	//grid bag constraints
	public static GridBagConstraints createGBC(int gridx, int gridy, int gridwidth, int gridheight) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		
		return gbc;
	}
	
	public static GridBagConstraints createGBC(int gridx, int gridy) {
		return createGBC(gridx, gridy, 1, 1);
	}
	
	public static void addToGrid(JComponent container, JComponent containee, int gridx, int gridy, 
			                     int width, int height) {
		container.add(containee, createGBC(gridx, gridy, width, height));
	}

	// default width and height = 1
	public static void addToGrid(JComponent container, JComponent objectToAdd, int gridx, int gridy) {
		addToGrid(container, objectToAdd, gridx, gridy, 1, 1);
	}
	
	public static JLabel createSpacer(int width, int height) {
		JLabel spacer = new JLabel();
		spacer.setPreferredSize(new Dimension(width, height));
		return spacer;
	}
	
	//outside
	public static Border createOutsideBorder(Color color, int width) {
		Border outsideBorder = BorderFactory.createLineBorder(color, width);
		Border border = BorderFactory.createCompoundBorder(null, outsideBorder);
		return border;
	}
	
	public static Border createOutsideBorder(Color color) {
		return createOutsideBorder(color, 1);
	}
	
	//inside
	public static Border createInsideBorder(Color color, int width) {
		Border insideBorder = BorderFactory.createLineBorder(color, width);
		Border border = BorderFactory.createCompoundBorder(insideBorder, null);
		return border;
	}

	public static Border createInsideBorder(Color color) {
		return createInsideBorder(color, 1);
	}
	
	public static Font boldFontWithSize(int size) {
		return new Font("Sans_Serif", Font.BOLD, size);
	}
	
	public static Font plainFontWithSize(int size) {
		return new Font("Sans_Serif", Font.PLAIN, size);
	}
	
	public static JButton createButton(String buttonText, int width, int height, int fontSize, 
			                           ActionListener actionPerformed) {
		JButton button = new JButton(buttonText);
		button.setPreferredSize(new Dimension(width, height));
		button.setFont(boldFontWithSize(fontSize));
		button.addActionListener(actionPerformed);
		return button;
	}
	
	public static JRadioButton createRadioButton(String buttonText, int fontSize, ActionListener actionPerformed) {
		JRadioButton button = new JRadioButton(buttonText);
		button.setFont(boldFontWithSize(fontSize));
		button.addActionListener(actionPerformed);
		return button;
	}
	
	public static Color defaultColor = new JPanel().getBackground();
}
