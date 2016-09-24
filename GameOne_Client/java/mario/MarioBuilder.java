package mario;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import client.Client;
import mario.gui.BackgroundPanel;
import mario.gui.SelectionPanel;
import objects.mario.MarioObject;
import objects.mario.MarioType;
import windows.GameSelect;

/**
 * Mario map editor window.
 * @author Sahar
 */
public final class MarioBuilder extends JFrame
{
	private static final long serialVersionUID = 5147600842261285898L;
	
	private static final Logger LOGGER = Logger.getLogger(MarioBuilder.class.getName());
	
	public static final String IMAGE_PATH = "./images/mario/";
	public static final Dimension SCREEN_SIZE = new Dimension(1000, 800);
	public static final int MAX_DISTANCE = SCREEN_SIZE.width + 100;
	public static final int SCREEN_MOVING_POINT = MarioBuilder.SCREEN_SIZE.width - 450;
	
	private final Map<String, ImageIcon> _mapEditImages = new HashMap<>();
	private final Map<String, ImageIcon> _allImages = new HashMap<>();
	private final JPanel _mapHolder = new JPanel(null);
	private SelectionPanel _sp;
	private JLabel _currentObject;
	
	private MarioBuilder()
	{
		super("GameOne Client - Super Mario");
		
		SwingUtilities.invokeLater(() ->
		{
			// Initialize all menu images.
			for (final MarioType obj : MarioType.values())
			{
				final ImageIcon icon = new ImageIcon(IMAGE_PATH + obj.getImage());
				
				if (obj.appearsOnMapBuilder())
					_mapEditImages.put(obj.getImage(), icon);
				_allImages.put(obj.getImage(), icon);
			}
			
			// Build the bottom navigation bar.
			_sp = new SelectionPanel(this);
			
			// Add the objects to the map.
			reload();
			
			// The map holder is the whole map, while BackgroundPanel is only the viewport.
			_mapHolder.setOpaque(false);
			_mapHolder.addMouseMotionListener(new MotionDetector());
			_mapHolder.addMouseListener(new CreateObject());
			
			final BackgroundPanel bg = new BackgroundPanel(_mapHolder);
			
			// Add the components to the frame.
			setLayout(new BorderLayout());
			add(bg, BorderLayout.NORTH);
			add(_sp, BorderLayout.SOUTH);
			
			// The regular settings...
			setResizable(false);
			pack();
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			LOGGER.info("MarioBuilder screen loaded.");
		});
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		reload();
		GameSelect.getInstance().enableAllButtons();
	}
	
	public void reload()
	{
		_mapHolder.removeAll();
		
		// Add the objects to the map.
		int maxX = 0;
		for (final MarioObject o : Client.getInstance().getMarioObjects())
		{
			final JLabel label = createMapBuilderIcon(o.getType().getImage(), o.getX(), o.getY());
			_mapHolder.add(label);
			
			maxX = Math.max(maxX, o.getX());
		}
		
		_mapHolder.setBounds(0, 0, maxX + MAX_DISTANCE, SCREEN_SIZE.height);
		_mapHolder.revalidate();
		_mapHolder.repaint();
		
		MarioScreen.getInstance().reload();
	}
	
	public Map<String, ImageIcon> getMapEditImages()
	{
		return _mapEditImages;
	}
	
	public Map<String, ImageIcon> getAllImages()
	{
		return _allImages;
	}
	
	public JPanel getMapHolder()
	{
		return _mapHolder;
	}
	
	private JLabel createMapBuilderIcon(final String image, final int x, final int y)
	{
		final ImageIcon icon = _mapEditImages.get(image);
		final JLabel label = new JLabel(icon);
		label.setName(image);
		label.setBounds(x, y, icon.getIconWidth(), icon.getIconHeight());
		
		return label;
	}
	
	private Component getNearestObject(final int x, final int y)
	{
		Component nearest = null;
		double dist = Double.MAX_VALUE;
		for (final Component o : _mapHolder.getComponents())
		{
			if (o == _currentObject)
				continue;
			
			double temp = Math.sqrt(Math.pow(x - o.getX(), 2) + Math.pow(y - o.getY(), 2));
			if (temp < dist)
			{
				nearest = o;
				dist = temp;
			}
		}
		
		return nearest;
	}
	
	private class MotionDetector extends MouseMotionAdapter
	{
		@Override
		public void mouseMoved(final MouseEvent e)
		{
			if (_sp.getSelectedType() == null)
				return;
			
			if (_currentObject == null || !_currentObject.getName().equals(_sp.getSelectedType()))
			{
				if (_currentObject != null)
					_mapHolder.remove(_currentObject);
				
				_currentObject = createMapBuilderIcon(_sp.getSelectedType(), e.getX(), e.getY());
				
				_mapHolder.add(_currentObject);
			}
			else
				_currentObject.setLocation(e.getX(), e.getY());
			
			_mapHolder.repaint();
		}
	}
	
	private class CreateObject extends MouseAdapter
	{
		@Override
		public void mousePressed(final MouseEvent e)
		{
			final boolean remove = e.isControlDown();
			if (_sp.getSelectedType() == null && !remove)
				return;
			
			int x = e.getX();
			int y = e.getY();
			
			Component o = null;
			if (remove)
				o = _mapHolder.getComponentAt(x - 1, y - 1);
			else
			{
				final ImageIcon i = _mapEditImages.get(_sp.getSelectedType());
				
				// Only coins can float anywhere, anything else should be placed adjacent to something else.
				if (!_sp.getSelectedType().equals(MarioType.COIN.getImage()))
				{
					final int maxX = x + i.getIconWidth();
					final int maxY = y + i.getIconHeight();
					final Component comp = getNearestObject(x, y);
					if (x > comp.getX() && x < comp.getX() + 10 || x < comp.getX() && x > comp.getX() - 10)
						x = comp.getX();
					if (y > comp.getY() && y < comp.getY() + 10 || y < comp.getY() && y > comp.getY() - 10)
						y = comp.getY();
					if (maxX > comp.getX() && maxX < comp.getX() + 10 || maxX < comp.getX() && maxX > comp.getX() - 10)
						x = comp.getX() - i.getIconWidth();
					if (maxY > comp.getY() && maxY < comp.getY() + 10 || maxY < comp.getY() && maxY > comp.getY() - 10)
						y = comp.getY() - i.getIconHeight();
					if (x > comp.getBounds().getMaxX() && x < comp.getBounds().getMaxX() + 10 || x < comp.getBounds().getMaxX() && x > comp.getBounds().getMaxX() - 10)
						x = (int) comp.getBounds().getMaxX();
					if (y > comp.getBounds().getMaxY() && y < comp.getBounds().getMaxY() + 10 || y < comp.getBounds().getMaxY() && y > comp.getBounds().getMaxY() - 10)
						y = (int) comp.getBounds().getMaxY();
				}
				
				o = createMapBuilderIcon(_sp.getSelectedType(), x, y);
			}
			
			if (remove)
				_mapHolder.remove(o);
			else
				_mapHolder.add(o);
			
			_mapHolder.repaint();
		}
	}
	
	public static MarioBuilder getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final MarioBuilder INSTANCE = new MarioBuilder();
	}
}