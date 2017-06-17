package gui;

import java.awt.Component;
import java.awt.Container;

import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SpringLayout.Constraints;

public final class SpringUtilities
{
	public static void makeCompactGrid(final Container parent, final int rows, final int cols, final int initialX, final int initialY, final int xPad, final int yPad)
	{
		final SpringLayout layout = (SpringLayout) parent.getLayout();
		
		// Align all cells in each column and make them the same width.
		Spring x = Spring.constant(initialX);
		for (int c = 0;c < cols;c++)
		{
			Spring width = Spring.constant(0);
			for (int r = 0;r < rows;r++)
				width = Spring.max(width, getConstraintsForCell(r, c, parent, cols).getWidth());
			for (int r = 0;r < rows;r++)
			{
				final Constraints constraints = getConstraintsForCell(r, c, parent, cols);
				
				constraints.setX(x);
				constraints.setWidth(width);
			}
			
			x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
		}
		
		// Align all cells in each row and make them the same height.
		Spring y = Spring.constant(initialY);
		for (int r = 0;r < rows;r++)
		{
			Spring height = Spring.constant(0);
			for (int c = 0;c < cols;c++)
				height = Spring.max(height, getConstraintsForCell(r, c, parent, cols).getHeight());
			for (int c = 0; c < cols; c++)
			{
				final Constraints constraints = getConstraintsForCell(r, c, parent, cols);
				
				constraints.setY(y);
				constraints.setHeight(height);
			}
			
			y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
		}
		
		// Set the parent's size.
		final Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH, y);
		pCons.setConstraint(SpringLayout.EAST, x);
	}
	
	private static Constraints getConstraintsForCell(final int row, final int col, final Container parent, final int cols)
	{
		final SpringLayout layout = (SpringLayout) parent.getLayout();
		final Component c = parent.getComponent(row * cols + col);
		
		return layout.getConstraints(c);
	}
}