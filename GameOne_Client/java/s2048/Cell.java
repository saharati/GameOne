package s2048;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public final class Cell extends JLabel
{
	private static final long serialVersionUID = -8045288160985304143L;
	
	private static final Dimension BLOCK_SIZE = new Dimension(150, 150);
	private static final Border BORDER = BorderFactory.createLineBorder(Color.BLACK, 5);
	private static final Font FONT = new Font("Arial", Font.BOLD, 50);
	
	private int _number;
	
	public Cell()
	{
		setOpaque(true);
		setPreferredSize(BLOCK_SIZE);
		setBorder(BORDER);
		setFont(FONT);
		setHorizontalAlignment(SwingConstants.CENTER);
		setBackground(Color.LIGHT_GRAY);
		setForeground(Color.RED);
	}
	
	public boolean taken()
	{
		return _number != 0;
	}
	
	public void reset()
	{
		_number = 0;
		
		setText("");
		setBackground(Color.LIGHT_GRAY);
		setForeground(Color.RED);
	}
	
	public int get()
	{
		return _number;
	}
	
	public void set()
	{
		_number = 2;
		
		setText("2");
		setBackground(Color.LIGHT_GRAY.darker());
		setForeground(Color.RED.brighter());
	}
	
	public int set(final Cell other, final boolean mul)
	{
		_number = other.get();
		
		setBackground(other.getBackground());
		setForeground(other.getForeground());
		
		if (mul)
		{
			_number *= 2;
			
			do
			{
				setBackground(getBackground().darker());
				setForeground(getForeground().brighter());
			} while (getBackground().hashCode() == getForeground().hashCode());
		}
		
		setText(String.valueOf(_number));
		return _number;
	}
}