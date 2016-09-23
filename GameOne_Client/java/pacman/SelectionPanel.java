package pacman;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import client.Client;
import objects.pacman.PacmanObject;

public final class SelectionPanel extends JPanel
{
	private static final long serialVersionUID = -7219243836284960529L;
	
	private final JComboBox<Integer> _mapIds = new JComboBox<>();
	
	public SelectionPanel(final MapBuilder builder)
	{
		setLayout(null);
		setBackground(Color.BLACK);
		setBounds(0, 0, 1024, 64);
		
		int x = 0;
		for (final Entry<PacmanObject, Image> entry : builder.getPacmanObjects().entrySet())
		{
			if (entry.getKey().isPlayer() && entry.getKey() != PacmanObject.PLAYER_NORMAL || entry.getKey() == PacmanObject.MOB_SLOW)
				continue;
			
			final PacmanButton b = new PacmanButton(entry, x, 0);
			b.addActionListener(a -> builder.setSelectedEntry(entry));
			add(b);
			
			x += 64;
		}
		x += 64;
		
		final JButton play = new JButton("Play");
		play.setBounds(x, 16, 64, 32);
		play.addMouseListener(new Play());
		add(play);
		
		x += 128;
		
		final JButton save = new JButton("Save");
		save.setBounds(x, 16, 64, 32);
		save.addMouseListener(new Save());
		add(save);
		
		x += 128;
		
		for (final int id : Client.getInstance().getPacmanMaps().keySet())
			_mapIds.addItem(id);
		_mapIds.setBounds(x, 16, 64, 32);
		_mapIds.addActionListener(a -> MapBuilder.getInstance().setEditingMapId(_mapIds.getItemAt(_mapIds.getSelectedIndex())));
		add(_mapIds);
	}
	
	// TODO when to use this method?
	public void reloadComboBox()
	{
		_mapIds.removeAllItems();
		for (final int id : Client.getInstance().getPacmanMaps().keySet())
			_mapIds.addItem(id);
	}
	
	private class Play extends MouseAdapter
	{
		@Override
		public void mousePressed(final MouseEvent e)
		{
			MapBuilder.getInstance().setVisible(false);
			MapBuilder.getInstance().getNextMap().setVisible(true);
		}
	}
	
	private class Save extends MouseAdapter
	{
		@Override
		public void mousePressed(final MouseEvent e)
		{
			if (!System.getProperty("user.name").equals("s5866872"))
				JOptionPane.showMessageDialog(null, "You do not have permissions to save new maps.", "Fail", JOptionPane.ERROR_MESSAGE);
			else
			{
				// TODO
				//_client.getConnection().requestMaps(((Builder) _client.getCurrentWindow()).getId(), ((Builder) _client.getCurrentWindow()).getObjects());
				JOptionPane.showMessageDialog(null, "Map saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
}