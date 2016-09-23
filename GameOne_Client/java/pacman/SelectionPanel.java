package pacman;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import client.Client;
import network.request.RequestPacmanMapEdit;
import objects.pacman.PacmanObject;

/**
 * The panel with all controls needed to play around with maps.
 * @author Sahar
 */
public final class SelectionPanel extends JPanel
{
	private static final long serialVersionUID = -7219243836284960529L;
	
	private static final Dimension ACTION_DIMENSION = new Dimension(MapBuilder.PIXEL_DIMENSIONS.width * 2, MapBuilder.PIXEL_DIMENSIONS.height / 2);
	private static final int TOP_GAP = ACTION_DIMENSION.height / 2;
	
	private final JComboBox<Integer> _mapIds = new JComboBox<>();
	
	public SelectionPanel(final MapBuilder builder, final int amount)
	{
		setBackground(Color.BLACK);
		setLayout(new BorderLayout());
		
		final JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
		for (final Entry<PacmanObject, Image> entry : builder.getPacmanObjects().entrySet())
		{
			if (entry.getKey().isPlayer() && entry.getKey() != PacmanObject.PLAYER_NORMAL || entry.getKey() == PacmanObject.MOB_SLOW)
				continue;
			
			final PacmanButton b = new PacmanButton(entry);
			b.addActionListener(a -> builder.setSelectedEntry(entry));
			imagePanel.add(b);
		}
		add(imagePanel, BorderLayout.WEST);
		
		final JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 10, TOP_GAP));
		actionPanel.setOpaque(false);
		final JButton play = new JButton("Play");
		play.setPreferredSize(ACTION_DIMENSION);
		play.addMouseListener(new Play());
		actionPanel.add(play);
		final JButton save = new JButton("Save");
		save.setPreferredSize(ACTION_DIMENSION);
		save.addMouseListener(new Save());
		actionPanel.add(save);
		for (final int id : Client.getInstance().getPacmanMaps().keySet())
			_mapIds.addItem(id);
		_mapIds.setPreferredSize(ACTION_DIMENSION);
		_mapIds.addActionListener(a -> MapBuilder.getInstance().setEditingMapId(_mapIds.getItemAt(_mapIds.getSelectedIndex())));
		actionPanel.add(_mapIds);
		add(actionPanel, BorderLayout.EAST);
	}
	
	public void reset()
	{
		_mapIds.setSelectedIndex(0);
	}
	
	public void reloadComboBox()
	{
		final ActionListener listener = _mapIds.getActionListeners()[0];
		
		_mapIds.removeActionListener(listener);
		_mapIds.removeAllItems();
		for (final int id : Client.getInstance().getPacmanMaps().keySet())
			_mapIds.addItem(id);
		_mapIds.addActionListener(listener);
	}
	
	private class Save extends MouseAdapter
	{
		@Override
		public void mousePressed(final MouseEvent e)
		{
			Client.getInstance().sendPacket(new RequestPacmanMapEdit(MapBuilder.getInstance().getEditingMapId(), MapBuilder.getInstance().getButtons()));
		}
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
}