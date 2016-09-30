package windows;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class Top extends JFrame
{
	private static final long serialVersionUID = -2106697408370510028L;
	
	private static final Dimension SPACING = new Dimension(20, 1);
	private static final String[] HEAD = {"Name", "Wins", "Score", "Loses"};
	
	public Top(final String[][] data)
	{
		super("GameOne Client - Sahar Atias");
		
		final JTable table = new JTable()
		{
			private static final long serialVersionUID = 5338117964502108581L;
			
			@Override
			public boolean isCellEditable(final int r, final int c)
			{
				return false;
			}
		};
		
		table.setIntercellSpacing(SPACING);
		table.setRowHeight(table.getRowHeight() + 10);
		table.getTableHeader().setBackground(Color.YELLOW);
		table.setModel(new DefaultTableModel(data, HEAD));
		
		final DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
		centerAlign.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		for (final String s : HEAD)
			table.getColumn(s).setCellRenderer(centerAlign);
		
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		add(new JScrollPane(table));
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
	}
}