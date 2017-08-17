package sal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

import client.Client;
import configs.Config;
import network.request.RequestTurnChange;
import network.request.RequestUpdateGameScore;
import network.request.RequestWaitingRoom;
import objects.GameId;
import objects.GameResult;
import util.InfoTableModel;
import util.random.Rnd;
import windows.WaitingRoom;

public final class SalScreen extends JFrame
{
	private static final long serialVersionUID = -5025724966312043051L;
	
	private static final Logger LOGGER = Logger.getLogger(SalScreen.class.getName());
	
	private static final int BOARD_SIZE = 5;
	private static final int NEXT_SIZE = 3;
	private static final String[] COLUMNS = {"Name", "Score"};
	private static final String IMAGE_PATH = "./images/sal/";
	private static final Object[][] DATA =
	{
		{"hatzor", 150},
		{"tahzuka", 100},
		{"teufa", 70},
		{"144", 40},
		{"105", 30},
		{"101", 20},
		{"binui", 10}
	};
	private static final Object[][] INITIAL_PLAYER_DATA =
	{
		{"Me", 0},
		{"\u2191", "\u2191"},
		{"Enemy", 0}
	};
	
	public static final Dimension SQAURE_SIZE = new Dimension(71, 83);
	public static final Map<String, Image> IMAGES = new HashMap<>();
	static
	{
		for (final File file : new File(IMAGE_PATH).listFiles())
			IMAGES.put(file.getName().substring(0, file.getName().lastIndexOf('.')), new ImageIcon(file.getAbsolutePath()).getImage());
	}
	
	private final InfoTableModel _playersModel = new InfoTableModel(COLUMNS);
	private final JTable _players = new JTable(_playersModel);
	private final InfoTableModel _scoresModel = new InfoTableModel(COLUMNS);
	private final JTable _scores = new JTable(_scoresModel);
	private final SalImage[][] _matrixPanel = new SalImage[BOARD_SIZE][BOARD_SIZE];
	private final SalImage[] _nextCards = new SalImage[NEXT_SIZE];
	private boolean _myTurn;
	private boolean _isVertical;
	
	protected SalScreen()
	{
		super("GameOne Client - Slide a Lama");
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		final DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
		centerAlign.setHorizontalAlignment(SwingConstants.CENTER);
		
		_players.getTableHeader().setReorderingAllowed(false);
		_players.setRowHeight(_players.getRowHeight() + 10);
		_players.getTableHeader().setBackground(Color.PINK);
		_playersModel.updateInfo(INITIAL_PLAYER_DATA);
		_players.setPreferredScrollableViewportSize(_players.getPreferredSize());
		for (int i = 0;i < _playersModel.getColumnCount();i++)
		{
			final String header = _playersModel.getColumnName(i);
			_players.getColumn(header).setCellRenderer(centerAlign);
		}
		add(new JScrollPane(_players));
		
		final JPanel matrixPanel = new JPanel(new GridLayout(BOARD_SIZE + 1, BOARD_SIZE + 2, 5, 5));
		for (int i = 0;i < BOARD_SIZE + 1;i++)
		{
			for (int j = 0;j < BOARD_SIZE + 2;j++)
			{
				final int x = i;
				final int y = j;
				if (i == 0 && (j == 0 || j == BOARD_SIZE + 1))
				{
					final JPanel dummy = new JPanel();
					dummy.setBackground(Color.GREEN.darker());
					matrixPanel.add(dummy);
				}
				else if (i == 0)
				{
					final SalButton b = new SalButton(IMAGES.get("down"));
					b.addActionListener(a -> arrowClicked(x, y));
					matrixPanel.add(b);
				}
				else if (j == 0)
				{
					final SalButton b = new SalButton(IMAGES.get("right"));
					b.addActionListener(a -> arrowClicked(x, y));
					matrixPanel.add(b);
				}
				else if (j == BOARD_SIZE + 1)
				{
					final SalButton b = new SalButton(IMAGES.get("left"));
					b.addActionListener(a -> arrowClicked(x, y));
					matrixPanel.add(b);
				}
				else
				{
					final SalImage image = new SalImage();
					matrixPanel.add(image);
					
					_matrixPanel[i - 1][j - 1] = image;
				}
			}
		}
		matrixPanel.setBorder(new CompoundBorder(LineBorder.createBlackLineBorder(), BorderFactory.createLineBorder(Color.RED, 4)));
		matrixPanel.setBackground(Color.GREEN.darker());
		add(matrixPanel);
		
		final JPanel bottomPanel = new JPanel(new BorderLayout());
		_scores.getTableHeader().setReorderingAllowed(false);
		_scores.setRowHeight(_scores.getRowHeight() + 10);
		_scores.getTableHeader().setBackground(Color.PINK);
		_scoresModel.updateInfo(DATA);
		_scores.setPreferredScrollableViewportSize(_scores.getPreferredSize());
		for (int i = 0;i < _scoresModel.getColumnCount();i++)
		{
			final String header = _scoresModel.getColumnName(i);
			_scores.getColumn(header).setCellRenderer(centerAlign);
		}
		bottomPanel.add(new JScrollPane(_scores), BorderLayout.WEST);
		
		final int topGap = (_scores.getPreferredSize().height - SQAURE_SIZE.height) / 2;
		final JPanel nextPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, topGap));
		nextPanel.add(new SalImage("right"));
		for (int i = 0;i < NEXT_SIZE;i++)
		{
			final SalImage image = new SalImage();
			nextPanel.add(image);
			
			_nextCards[i] = image;
		}
		nextPanel.add(new SalImage("right"));
		nextPanel.setBorder(new CompoundBorder(LineBorder.createBlackLineBorder(), BorderFactory.createLineBorder(Color.GREEN.darker(), 3)));
		nextPanel.setBackground(Config.UI_COLOR);
		bottomPanel.add(nextPanel, BorderLayout.EAST);
		
		add(bottomPanel);
		
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		LOGGER.info("Slide a Lama screen loaded.");
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		Client.getInstance().sendPacket(new RequestUpdateGameScore(GameResult.LEAVE, calcScore()));
		Client.getInstance().setCurrentDetails(WaitingRoom.getInstance(), GameId.LAMA, false);
	}
	
	public void start()
	{
		_players.setValueAt(0, 0, 1);
		_players.setValueAt(0, 2, 1);
		
		_myTurn = true;
		
		for (int i = 0;i < BOARD_SIZE;i++)
		{
			for (int j = 0;j < BOARD_SIZE;j++)
			{
				int rnd;
				do
				{
					rnd = Rnd.get(DATA.length);
				} while (has3InARow(i, j, DATA[rnd][0]));
				_matrixPanel[i][j].setImage((String) DATA[rnd][0]);
			}
		}
		
		for (int i = 0;i < NEXT_SIZE;i++)
			_nextCards[i].setImage((String) DATA[Rnd.get(DATA.length)][0]);
		
		Client.getInstance().sendPacket(new RequestTurnChange(_matrixPanel, _nextCards, 0));
		
		setVisible(true);
	}
	
	public void start(final String[][] matrixPanel, final String[] nextCards)
	{
		_players.setValueAt(0, 0, 1);
		_players.setValueAt(0, 2, 1);
		
		_myTurn = false;
		
		_players.setValueAt("\u2193", 1, 0);
		_players.setValueAt("\u2193", 1, 1);
		
		for (int i = 0;i < matrixPanel.length;i++)
			for (int j = 0;j < matrixPanel[i].length;j++)
				_matrixPanel[i][j].setImage(matrixPanel[i][j]);
		
		for (int i = 0;i < nextCards.length;i++)
			_nextCards[i].setImage(nextCards[i]);
		
		setVisible(true);
	}
	
	public void updateData(final String[][] matrixPanel, final String[] nextCards, final int score)
	{
		for (int i = 0;i < matrixPanel.length;i++)
			for (int j = 0;j < matrixPanel[i].length;j++)
				_matrixPanel[i][j].setImage(matrixPanel[i][j]);
		
		for (int i = 0;i < nextCards.length;i++)
			_nextCards[i].setImage(nextCards[i]);
		
		_players.setValueAt(score, 2, 1);
		_players.setValueAt("\u2191", 1, 0);
		_players.setValueAt("\u2191", 1, 1);
		
		if (Config.GAME_BEEP)
			Toolkit.getDefaultToolkit().beep();
		
		_myTurn = true;
		
		if (score > 500)
		{
			Client.getInstance().sendPacket(new RequestUpdateGameScore(GameResult.LOSE, calcScore()));
			
			JOptionPane.showMessageDialog(null, "You lose!", "Noob", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private int calcScore()
	{
		return Math.abs((int) _players.getValueAt(0, 1) - (int) _players.getValueAt(2, 1));
	}
	
	private boolean has3InARow(final int i, final int j, final Object symbol)
	{
		if (i - 2 >= 0 && _matrixPanel[i - 1][j].getName().equals(symbol) && _matrixPanel[i - 2][j].getName().equals(symbol))
		{
			_isVertical = true;
			return true;
		}
		if (j - 2 >= 0 && _matrixPanel[i][j - 1].getName().equals(symbol) && _matrixPanel[i][j - 2].getName().equals(symbol))
		{
			_isVertical = false;
			return true;
		}
		
		return false;
	}
	
	private void arrowClicked(final int i, final int j)
	{
		if (!_myTurn)
		{
			JOptionPane.showMessageDialog(null, "This is not your turn, please wait.", "Wait", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (i == 0)
		{
			final int y = j - 1;
			for (int x = BOARD_SIZE - 1;x > 0;x--)
				_matrixPanel[x][y].setImage(_matrixPanel[x - 1][y].getName());
			_matrixPanel[0][y].setImage(_nextCards[NEXT_SIZE - 1].getName());
			
			for (int x = NEXT_SIZE - 1;x > 0;x--)
				_nextCards[x].setImage(_nextCards[x - 1].getName());
			_nextCards[0].setImage((String) DATA[Rnd.get(DATA.length)][0]);
		}
		else if (j == BOARD_SIZE + 1)
		{
			int x = i - 1;
			for (int y = 0;y < BOARD_SIZE - 1;y++)
				_matrixPanel[x][y].setImage(_matrixPanel[x][y + 1].getName());
			_matrixPanel[x][BOARD_SIZE - 1].setImage(_nextCards[NEXT_SIZE - 1].getName());
			
			for (int y = NEXT_SIZE - 1;y > 0;y--)
				_nextCards[y].setImage(_nextCards[y - 1].getName());
			_nextCards[0].setImage((String) DATA[Rnd.get(DATA.length)][0]);
		}
		else if (j == 0)
		{
			int x = i - 1;
			for (int y = BOARD_SIZE - 1;y > 0;y--)
				_matrixPanel[x][y].setImage(_matrixPanel[x][y - 1].getName());
			_matrixPanel[x][0].setImage(_nextCards[NEXT_SIZE - 1].getName());
			
			for (int y = NEXT_SIZE - 1;y > 0;y--)
				_nextCards[y].setImage(_nextCards[y - 1].getName());
			_nextCards[0].setImage((String) DATA[Rnd.get(DATA.length)][0]);
		}
		
		boolean metTriple;
		do
		{
			String name = null;
			metTriple = false;
			for (int x = 0;x < BOARD_SIZE;x++)
			{
				for (int y = 0;y < BOARD_SIZE;y++)
				{
					if (has3InARow(x, y, _matrixPanel[x][y].getName()))
					{
						metTriple = true;
						if (name == null)
							name = _matrixPanel[x][y].getName();
						do
						{
							if (_isVertical)
								for (int w = 0;w < 3;w++)
									_matrixPanel[x - w][y].setImage((String) DATA[Rnd.get(DATA.length)][0]);
							else
								for (int w = 0;w < 3;w++)
									_matrixPanel[x][y - w].setImage((String) DATA[Rnd.get(DATA.length)][0]);
						} while (has3InARow(x, y, name));
					}
				}
			}
			if (name != null)
			{
				for (int x = 0;x < DATA.length;x++)
				{
					if (name.equals(DATA[x][0]))
					{
						_players.setValueAt((int) _players.getValueAt(0, 1) + (int) DATA[x][1], 0, 1);
						break;
					}
				}
			}
		} while (metTriple);
		
		_myTurn = false;
		
		_players.setValueAt("\u2193", 1, 0);
		_players.setValueAt("\u2193", 1, 1);
		
		Client.getInstance().sendPacket(new RequestTurnChange(_matrixPanel, _nextCards, (int) _players.getValueAt(0, 1)));
	}
	
	public void showResult(final GameResult result)
	{
		switch (result)
		{
			case WIN:
				JOptionPane.showMessageDialog(null, "Congratulations, you win!", "Victory", JOptionPane.INFORMATION_MESSAGE);
				break;
			case LEAVE:
				JOptionPane.showMessageDialog(null, "Your opponent has logged off, you won!", "Victory", JOptionPane.INFORMATION_MESSAGE);
				break;
			case EXIT:
				Client.getInstance().sendPacket(new RequestUpdateGameScore(GameResult.EXIT, calcScore()));
				return;
		}
		
		setVisible(false);
		
		Client.getInstance().setCurrentDetails(WaitingRoom.getInstance(), GameId.LAMA, false);
		Client.getInstance().sendPacket(new RequestWaitingRoom(GameId.LAMA));
	}
	
	public static SalScreen getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SalScreen INSTANCE = new SalScreen();
	}
}