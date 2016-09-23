package pacman;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import client.Client;
import objects.pacman.PacmanObject;
import pacman.objects.PacmanMap;
import pacman.objects.MapObject;
import windows.GameSelect;

/**
 * Map builder window for pacman.
 * @author Sahar
 */
public final class MapBuilder extends JFrame
{
	private static final long serialVersionUID = 1815790049829742350L;
	
	private static final Logger LOGGER = Logger.getLogger(MapBuilder.class.getName());
	private static final String IMAGE_PATH = "./images/pacman/";
	
	public static final int[] ARRAY_DIMENSIONS = {16, 12};
	public static final Dimension PIXEL_DIMENSIONS = new Dimension(64, 64);
	
	private final Map<PacmanObject, Image> _pacmanObjects = new LinkedHashMap<>();
	private final PacmanButton[][] _buttons = new PacmanButton[ARRAY_DIMENSIONS[0]][ARRAY_DIMENSIONS[1]];
	private final SelectionPanel _selectionPanel;
	private Entry<PacmanObject, Image> _selectedEntry;
	private int _editingMapId = -1;
	private int _currentMap = -1;
	private int _currentScore;
	
	private MapBuilder()
	{
		super("Pacman Map Builder");
		
		for (final PacmanObject obj : PacmanObject.values())
			_pacmanObjects.put(obj, new ImageIcon(IMAGE_PATH + obj.getImage()).getImage());
		_selectedEntry = _pacmanObjects.entrySet().iterator().next();
		
		setLayout(new BorderLayout());
		
		final JPanel map = new JPanel(new GridBagLayout());
		final GridBagConstraints gc = new GridBagConstraints();
		for (int i = 0;i < _buttons.length;i++)
		{
			for (int j = 0;j < _buttons[i].length;j++)
			{
				gc.gridx = i + 1;
				gc.gridy = j + 1;
				
				_buttons[i][j] = new PacmanButton(_selectedEntry);
				_buttons[i][j].addMouseListener(new PutIn(i, j));
				
				map.add(_buttons[i][j], gc);
			}
		}
		add(map, BorderLayout.NORTH);
		
		_selectionPanel = new SelectionPanel(this, _pacmanObjects.size());
		add(_selectionPanel, BorderLayout.SOUTH);
		
		setResizable(false);
		pack();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		
		LOGGER.info("Pacman map builder loaded.");
	}
	
	public Map<PacmanObject, Image> getPacmanObjects()
	{
		return _pacmanObjects;
	}
	
	public PacmanButton[][] getButtons()
	{
		return _buttons;
	}
	
	public SelectionPanel getSelectionPanel()
	{
		return _selectionPanel;
	}
	
	public Entry<PacmanObject, Image> getSelectedEntry()
	{
		return _selectedEntry;
	}
	
	public int getEditingMapId()
	{
		return _editingMapId;
	}
	
	public int getCurrentScore()
	{
		return _currentScore;
	}
	
	public PacmanMap getNextMap()
	{
		return Client.getInstance().getPacmanMaps().get(++_currentMap);
	}
	
	public void setSelectedEntry(final Entry<PacmanObject, Image> selectedEntry)
	{
		_selectedEntry = selectedEntry;
	}
	
	public void setEditingMapId(final int editingMapId)
	{
		_editingMapId = editingMapId;
		
		if (editingMapId == -1)
		{
			for (int i = 0;i < _buttons.length;i++)
				for (int j = 0;j < _buttons[i].length;j++)
					_buttons[i][j].setEntry(PacmanObject.EMPTY, _pacmanObjects.get(PacmanObject.EMPTY));
		}
		else
		{
			final MapObject[][] objects = Client.getInstance().getPacmanMaps().get(editingMapId).getObjects();
			for (int i = 0;i < objects.length;i++)
				for (int j = 0;j < objects[i].length;j++)
					_buttons[i][j].setEntry(objects[i][j].getType(), _pacmanObjects.get(objects[i][j].getType()));
		}
	}
	
	public void addScore(int score)
	{
		_currentScore += score;
	}
	
	public void reset()
	{
		_selectionPanel.reset();
		
		setEditingMapId(-1);
		
		_currentMap = -1;
		_currentScore = 0;
		
		Client.getInstance().getPacmanMaps().values().forEach(m -> m.reset());
		GameSelect.getInstance().enableAllButtons();
		
		// TODO
		//_client.getConnection().saveUserStatus(Math.max(_currentScore, _client.getScore()), Math.max(_currentMap, _client.getWins()), 0);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		reset();
	}
	
	private class PutIn extends MouseAdapter
	{
		private final int _i;
		private final int _j;
		
		private PutIn(final int i, final int j)
		{
			_i = i;
			_j = j;
		}
		
		@Override
		public void mousePressed(final MouseEvent e)
		{
			_buttons[_i][_j].setEntry(_selectedEntry);
		}
	}
	
	public static MapBuilder getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final MapBuilder INSTANCE = new MapBuilder();
	}
}