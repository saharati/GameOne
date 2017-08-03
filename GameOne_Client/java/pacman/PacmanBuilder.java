package pacman;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import client.Client;
import objects.pacman.PacmanObject;
import pacman.objects.PacmanMap;
import pacman.objects.PacmanMapObject;
import util.ComponentUtil;
import windows.GameSelect;

/**
 * Map builder window for pacman.
 * @author Sahar
 */
public final class PacmanBuilder extends JFrame
{
	private static final long serialVersionUID = 1815790049829742350L;
	
	private static final Logger LOGGER = Logger.getLogger(PacmanBuilder.class.getName());
	private static final String IMAGE_PATH = "./images/pacman/";
	
	public static final int[] ARRAY_DIMENSIONS = {16, 12};
	public static final int BLOCK_SIZE = 64;
	public static final Dimension PIXEL_DIMENSIONS = new Dimension(BLOCK_SIZE, BLOCK_SIZE);
	
	private final Map<PacmanObject, Image> _pacmanObjects = new LinkedHashMap<>();
	private final PacmanButton[][] _buttons = new PacmanButton[ARRAY_DIMENSIONS[0]][ARRAY_DIMENSIONS[1]];
	private final PacmanPanel _selectionPanel;
	private Entry<PacmanObject, Image> _selectedEntry;
	private int _editingMapId = -1;
	private int _currentMap = -1;
	private int _currentScore;
	
	protected PacmanBuilder()
	{
		super("GameOne Client - Pacman");
		
		for (final PacmanObject obj : PacmanObject.values())
		{
			final ImageIcon icon = new ImageIcon(IMAGE_PATH + obj.getImage());
			if (icon.getImageLoadStatus() == MediaTracker.ERRORED)
			{
				LOGGER.severe("Failed initializing pacman image icon: " + IMAGE_PATH + obj.getImage());
				
				ComponentUtil.showHyperLinkPopup("Initialize Error", "<html><body>Could not read file " + IMAGE_PATH + obj.getImage() + ".<br>Please make sure it exists and that it is readable.<br>For support open an issue at <a href=\"https://github.com/saharati/GameOne\">https://github.com/saharati/GameOne</a>.</body></html>", JOptionPane.ERROR_MESSAGE);
				
				System.exit(0);
			}
			
			_pacmanObjects.put(obj, icon.getImage());
		}
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
				_buttons[i][j].addActionListener(a -> _buttons[gc.gridx - 1][gc.gridy - 1].setEntry(_selectedEntry));
				
				map.add(_buttons[i][j], gc);
			}
		}
		add(map, BorderLayout.NORTH);
		
		_selectionPanel = new PacmanPanel(this, _pacmanObjects.size());
		add(_selectionPanel, BorderLayout.SOUTH);
		
		setResizable(false);
		pack();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		
		LOGGER.info("PacmanBuilder screen loaded.");
	}
	
	public Map<PacmanObject, Image> getPacmanObjects()
	{
		return _pacmanObjects;
	}
	
	public PacmanButton[][] getButtons()
	{
		return _buttons;
	}
	
	public PacmanPanel getSelectionPanel()
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
	
	public PacmanMap getCurrentMap()
	{
		return Client.getInstance().getPacmanMaps().get(_currentMap);
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
			final PacmanMapObject[][] objects = Client.getInstance().getPacmanMaps().get(editingMapId).getObjects();
			for (int i = 0;i < objects.length;i++)
				for (int j = 0;j < objects[i].length;j++)
					_buttons[i][j].setEntry(objects[i][j].getType(), _pacmanObjects.get(objects[i][j].getType()));
		}
	}
	
	public void addScore(int score)
	{
		_currentScore += score;
	}
	
	public void reload()
	{
		_selectionPanel.reset();
		
		setEditingMapId(-1);
		
		_currentMap = -1;
		_currentScore = 0;
		
		Client.getInstance().getPacmanMaps().values().forEach(m -> m.reset());
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		reload();
		Client.getInstance().setCurrentDetails(GameSelect.getInstance(), null, true);
	}
	
	public static PacmanBuilder getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PacmanBuilder INSTANCE = new PacmanBuilder();
	}
}