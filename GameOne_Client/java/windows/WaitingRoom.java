package windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;

import client.Client;
import network.request.RequestGameStart;
import network.request.RequestInviteToDuel;
import util.InfoTableModel;
import util.threadpool.ThreadPool;

public final class WaitingRoom extends JFrame
{
	private static final long serialVersionUID = -3188277371240708763L;
	
	private static final String TITLE = "Duel Invitation";
	private static final String INVITE = "Do you want to invite %target% to a duel?";
	private static final String ASK = "%requestor% has invited you to a duel, do you accept?";
	private static final String WAIT = "Waiting user response...";
	
	protected static final JOptionPane INVITE_PANE = new JOptionPane(null, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, null, null);
	private static final JOptionPane ASK_PANE = new JOptionPane(null, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, null, null);
	private static final JOptionPane WAIT_PANE = new JOptionPane(WAIT, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[] {"Cancel"}, null);
	
	protected final JTable _table = new JTable();
	private final InfoTableModel _model = new InfoTableModel("Name", "Status", "Score / Wins / Loses");
	
	protected JDialog _inviteDialog;
	private JDialog _waitDialog;
	private JDialog _askDialog;
	
	protected WaitingRoom()
	{
		super("GameOne Client - Sahar Atias");
		
		_table.getTableHeader().setReorderingAllowed(false);
		_table.addMouseListener(new MouseHandler());
		_table.setIntercellSpacing(new Dimension(20, 1));
		_table.setRowHeight(_table.getRowHeight() + 10);
		_table.getTableHeader().setBackground(Color.PINK);
		_table.setModel(_model);
		
		final DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
		centerAlign.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0;i < _model.getColumnCount();i++)
		{
			final String header = _model.getColumnName(i);
			_table.getColumn(header).setCellRenderer(centerAlign);
			_table.getColumn(header).setPreferredWidth(200);
		}
		
		add(new JScrollPane(_table));
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		cancelAllDialogs();
		Client.getInstance().setCurrentDetails(GameSelect.getInstance(), null, true);
	}
	
	public void showWaitDialog()
	{
		ThreadPool.execute(() ->
		{
			_waitDialog = WAIT_PANE.createDialog(this, TITLE);
			_waitDialog.setVisible(true);
			_waitDialog.dispose();
			
			// If closed by user or cancelled by user, notify server.
			if (WAIT_PANE.getValue() == null || !WAIT_PANE.getValue().equals(JOptionPane.UNINITIALIZED_VALUE))
				Client.getInstance().sendPacket(RequestInviteToDuel.STATIC_PACKET);
		});
	}
	
	public void showAskDialog(final String requestor)
	{
		ThreadPool.execute(() ->
		{
			ASK_PANE.setMessage(ASK.replace("%requestor%", requestor));
			
			_askDialog = ASK_PANE.createDialog(this, TITLE);
			_askDialog.setVisible(true);
			_askDialog.dispose();
			
			// If closed by user, notify server.
			if (ASK_PANE.getValue() == null)
				Client.getInstance().sendPacket(RequestInviteToDuel.STATIC_PACKET);
			// If not closed by target...
			else if (!ASK_PANE.getValue().equals(JOptionPane.UNINITIALIZED_VALUE))
			{
				// Cancelled by user, notify server.
				if ((int) ASK_PANE.getValue() == JOptionPane.NO_OPTION)
					Client.getInstance().sendPacket(RequestInviteToDuel.STATIC_PACKET);
				// Approved by user, start game.
				else
					Client.getInstance().sendPacket(RequestGameStart.STATIC_PACKET);
			}
		});
	}
	
	public void cancelAllDialogs()
	{
		if (_inviteDialog != null && _inviteDialog.isVisible())
			_inviteDialog.dispose();
		if (_waitDialog != null && _waitDialog.isVisible())
			_waitDialog.dispose();
		if (_askDialog != null && _askDialog.isVisible())
			_askDialog.dispose();
	}
	
	public void reload(final Object[][] newData)
	{
		_model.updateInfo(newData);
		
		_table.setPreferredScrollableViewportSize(_table.getPreferredSize());
		_table.revalidate();
		
		pack();
		if (!isVisible())
		{
			setLocationRelativeTo(null);
			setVisible(true);
		}
	}
	
	protected class MouseHandler extends MouseAdapter
	{
		@Override
		public void mousePressed(final MouseEvent me)
		{
			Client.getInstance().sendPacket(RequestInviteToDuel.STATIC_PACKET);
			
			final int row = _table.rowAtPoint(me.getPoint());
			final String target = _table.getValueAt(row, 0).toString();
			
			INVITE_PANE.setMessage(INVITE.replace("%target%", target));
			
			_inviteDialog = INVITE_PANE.createDialog(WaitingRoom.this, TITLE);
			_inviteDialog.setVisible(true);
			_inviteDialog.dispose();
			
			if (INVITE_PANE.getValue() != null && (int) INVITE_PANE.getValue() == JOptionPane.YES_OPTION)
				Client.getInstance().sendPacket(new RequestInviteToDuel(target));
			else
				Client.getInstance().sendPacket(RequestInviteToDuel.STATIC_PACKET);
		}
	}
	
	public static WaitingRoom getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final WaitingRoom INSTANCE = new WaitingRoom();
	}
}