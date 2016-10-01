package mario.resources;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ScheduledFuture;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import client.Client;
import mario.SuperMario;
import network.request.RequestGameEdit;
import objects.mario.MarioType;
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
	
	private final JPanel _actionPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 10, TOP_GAP));
	private JLabel _selectedItem;
	
	public SelectionPanel()
	{
		super(new BorderLayout());
		
		final JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
		for (final MarioType type : MarioType.values())
		{
			if (type.appearsOnMapBuilder())
			{
				final ImageIcon icon = type.getIcon();
				final JButton btn = new JButton(icon);
				btn.setPreferredSize(BUTTON_SIZE);
				btn.addActionListener(a -> onChangeItem(icon, type.getClassName()));
				
				imagePanel.add(btn);
			}
		}
		add(imagePanel, BorderLayout.WEST);
		
		final JButton play = new JButton("Play");
		play.setPreferredSize(ACTION_SIZE);
		play.addActionListener(a -> SuperMario.getInstance().onStart());
		_actionPanel.add(play);
		final JButton save = new JButton("Save");
		save.setPreferredSize(ACTION_SIZE);
		save.addActionListener(a -> {
			disableAllButtons();
			Client.getInstance().sendPacket(new RequestGameEdit(SuperMario.getInstance().getAddedObjects(), SuperMario.getInstance().getRemovedObjects()));
		});
		_actionPanel.add(save);
		final JButton left = new JButton("<");
		left.setPreferredSize(ACTION_SIZE2);
		left.addMouseListener(new Move(10));
		_actionPanel.add(left);
		final JButton right = new JButton(">");
		right.setPreferredSize(ACTION_SIZE2);
		right.addMouseListener(new Move(-10));
		_actionPanel.add(right);
		add(_actionPanel, BorderLayout.EAST);
	}
	
	public void enableAllButtons()
	{
		for (final Component comp : _actionPanel.getComponents())
			comp.setEnabled(true);
	}
	
	public void disableAllButtons()
	{
		for (final Component comp : _actionPanel.getComponents())
			comp.setEnabled(false);
	}
	
	public JLabel getSelectedItem()
	{
		return _selectedItem;
	}
	
	public void removeSelectedItem()
	{
		if (_selectedItem != null)
		{
			SuperMario.getInstance().getMapHolder().remove(_selectedItem);
			
			_selectedItem = null;
		}
	}
	
	private void onChangeItem(final ImageIcon icon, final String className)
	{
		if (_selectedItem != null)
			SuperMario.getInstance().getMapHolder().remove(_selectedItem);
		
		final JLabel label = new JLabel(icon);
		label.setName(className);
		label.setBounds(-Integer.MAX_VALUE, -Integer.MAX_VALUE, icon.getIconWidth(), icon.getIconHeight());
		
		_selectedItem = label;
		
		SuperMario.getInstance().getMapHolder().add(_selectedItem);
	}
	
	private class Move extends MouseAdapter implements Runnable
	{
		private int _directionSpeed;
		private ScheduledFuture<?> _future;
		
		protected Move(final int directionSpeed)
		{
			_directionSpeed = directionSpeed;
		}
		
		@Override
		public void mousePressed(final MouseEvent e)
		{
			_future = ThreadPool.scheduleAtFixedRate(this, 0, 10);
		}
		
		@Override
		public void mouseReleased(final MouseEvent e)
		{
			_future.cancel(false);
		}
		
		@Override
		public void run()
		{
			final JPanel p = SuperMario.getInstance().getMapHolder();
			if (p.getX() + _directionSpeed > 0)
				return;
			
			p.setLocation(p.getX() + _directionSpeed, p.getY());
		}
	}
}