package util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * Table model used in waiting room's jtable.
 * @author Sahar
 */
public final class InfoTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = -475285636203274193L;
	
	public static final String[] COLUMN_NAMES = {"Name", "Status", "Score / Wins / Loses"};
	
	private final List<Object[]> _data = new CopyOnWriteArrayList<>();
	
	@Override
	public int getColumnCount()
	{
		return COLUMN_NAMES.length;
	}
	
	@Override
	public String getColumnName(final int col)
	{
		return COLUMN_NAMES[col];
	}
	
	@Override
	public int getRowCount()
	{
		return _data.size();
	}
	
	@Override
	public Object getValueAt(final int row, final int col)
	{
		return _data.get(row)[col];
	}
	
	@Override
	public void setValueAt(final Object value, final int row, final int col)
	{
		_data.get(row)[col] = value;
		fireTableCellUpdated(row, col);
	}
	
	@Override
	public boolean isCellEditable(final int row, final int col)
	{
		return false;
	}
	
	public void addRow(final Object[] row)
	{
		_data.add(row);
		fireTableRowsInserted(getRowCount(), getRowCount());
	}
	
	public void removeRow(final String username)
	{
		for (final Object[] data : _data)
		{
			if (data[0].equals(username))
			{
				final int index = _data.indexOf(data);
				_data.remove(data);
				
				fireTableRowsDeleted(index, index);
				break;
			}
		}
	}
	
	public void updateInfo(final Object[][] infoArray)
	{
		final List<Object[]> newData = new CopyOnWriteArrayList<>(infoArray);
		
		// Iterate all rows
		for (int row = 0;row < getRowCount();row++)
		{
			boolean found = false;
			
			// Iterate all newData
			for (final Object[] data : newData)
			{
				// If we find a matching cell.
				if (data[0].equals(getValueAt(row, 0)))
				{
					// Update rest of the cells in this row.
					if (!data[1].equals(getValueAt(row, 1)))
						setValueAt(data[1], row, 1);
					if (!data[2].equals(getValueAt(row, 2)))
						setValueAt(data[2], row, 2);
					
					// Mark found and remove it from newData.
					found = true;
					newData.remove(data);
					break;
				}
			}
			// If we didn't find this row in newData, it means it got removed, delete it.
			if (!found)
				removeRow((String) getValueAt(row, 0));
		}
		// Iterate all newData and insert.
		newData.forEach(data -> addRow(data));
	}
}