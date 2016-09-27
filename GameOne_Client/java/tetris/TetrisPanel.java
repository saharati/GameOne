package tetris;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/**
 * A single square in the tetris board.
 * @author Sahar
 */
public final class TetrisPanel extends JPanel
{
	private static final long serialVersionUID = -8339887710661682620L;
	
	private static final Dimension BLOCK_SIZE = new Dimension(40, 35);
	
	private Color _taken = Color.LIGHT_GRAY;
	
	public TetrisPanel()
	{
		setPreferredSize(BLOCK_SIZE);
	}
	
	public void setTakenBy(final Color taken, final boolean update)
	{
		_taken = taken;
		
		if (update)
			repaint();
	}
	
	public boolean isTaken()
	{
		return _taken != Color.LIGHT_GRAY;
	}
	
	@Override
	public Color getBackground()
	{
		return _taken;
	}
	
	@Override
	public Border getBorder()
	{
		return LineBorder.createBlackLineBorder();
	}
}