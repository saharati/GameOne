package mario.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import mario.MarioBuilder;
import mario.MarioScreen;
import util.threadpool.ThreadPool;

/**
 * Navigation bar in mario map builder.
 * @author Sahar
 */
public final class SelectionPanel extends JPanel
{
	private static final long serialVersionUID = -8356976796308267070L;
	
	private static final Dimension BUTTON_SIZE = new Dimension(35, 50);
	private static final Dimension ACTION_SIZE = new Dimension(75, 32);
	private static final Dimension ACTION_SIZE2 = new Dimension(50, 32);
	private static final int TOP_GAP = (BUTTON_SIZE.height - ACTION_SIZE.height) / 2;
	
	private String _selectedType;
	private int _totalMovement;
	
	public SelectionPanel(final MarioBuilder builder)
	{
		super(new BorderLayout());
		
		final JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
		for (final Entry<String, ImageIcon> entry : builder.getMapEditImages().entrySet())
			imagePanel.add(createMarioButton(entry.getKey(), entry.getValue()));
		add(imagePanel, BorderLayout.WEST);
		
		final JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 10, TOP_GAP));
		final JButton play = new JButton("Play");
		play.setPreferredSize(ACTION_SIZE);
		play.addActionListener(a -> MarioScreen.getInstance().onStart());
		actionPanel.add(play);
		final JButton save = new JButton("Save");
		save.setPreferredSize(ACTION_SIZE);
		// save.addActionListener(a -> _client.getConnection().requestMaps(((MarioBuilder) _client.getCurrentWindow()).getObjects())); // TODO
		actionPanel.add(save);
		final JButton left = new JButton("<");
		left.setPreferredSize(ACTION_SIZE2);
		left.addMouseListener(new Move(10));
		actionPanel.add(left);
		final JButton right = new JButton(">");
		right.setPreferredSize(ACTION_SIZE2);
		right.addMouseListener(new Move(-10));
		actionPanel.add(right);
		add(actionPanel, BorderLayout.EAST);
	}
	
	private JButton createMarioButton(final String type, final ImageIcon image)
	{
		final JButton btn = new JButton(image);
		btn.setPreferredSize(BUTTON_SIZE);
		btn.addActionListener(a -> _selectedType = type);
		
		return btn;
	}
	
	public String getSelectedType()
	{
		return _selectedType;
	}
	
	public int getTotalMovement()
	{
		return _totalMovement;
	}
	
	private class Move extends MouseAdapter implements Runnable
	{
		private int _directionSpeed;
		private ScheduledFuture<?> _future;
		
		private Move(final int directionSpeed)
		{
			_directionSpeed = directionSpeed;
		}
		
		@Override
		public void mousePressed(final MouseEvent e)
		{
			_future = ThreadPool.scheduleAtFixedRate(this, 0, 20);
		}
		
		@Override
		public void mouseReleased(final MouseEvent e)
		{
			_future.cancel(false);
		}
		
		@Override
		public void run()
		{
			final JPanel p = MarioBuilder.getInstance().getMapHolder();
			if (p.getX() + _directionSpeed > 0)
				return;
			
			_totalMovement -= _directionSpeed;
			
			p.setLocation(p.getX() + _directionSpeed, p.getY());
		}
	}
}