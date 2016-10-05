package chess;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;

import chess.objects.AbstractObject;

/**
 * A single button in the chess board.
 * @author Sahar
 */
public final class ChessCell extends JButton
{
	private static final long serialVersionUID = 608473057363743650L;
	
	private static final Dimension CELL_SIZE = new Dimension(100, 100);
	
	private final int _cellX;
	private final int _cellY;
	private final Color _backgroundColor;
	
	private AbstractObject _object;
	
	public ChessCell(final int cellX, final int cellY, final boolean turn)
	{
		_cellX = cellX;
		_cellY = cellY;
		_backgroundColor = turn ? Color.GRAY : Color.LIGHT_GRAY;
		
		setPreferredSize(CELL_SIZE);
		setBackground(_backgroundColor);
		setOpaque(true);
	}
	
	public int getCellX()
	{
		return _cellX;
	}
	
	public int getCellY()
	{
		return _cellY;
	}
	
	public Color getBackgroundColor()
	{
		return _backgroundColor;
	}
	
	public AbstractObject getObject()
	{
		return _object;
	}
	
	public void setObject(final AbstractObject object)
	{
		_object = object;
		
		repaint();
	}
	
	@Override
	protected void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
		
		if (_object != null)
			g.drawImage(_object.getImage(), 0, 0, null);
	}
}