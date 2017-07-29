package util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Font;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SpringLayout.Constraints;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public final class ComponentUtil
{
	protected static final Logger LOGGER = Logger.getLogger(ComponentUtil.class.getName());
	
	public static void showHyperLinkPopup(final String title, final String text, final int messageType)
	{
		final JLabel label = new JLabel();
		final Font font = label.getFont();
		final StringBuilder sb = new StringBuilder();
		sb.append("<html><body style=\"");
		sb.append("font-family:" + font.getFamily() + ";font-weight:" + (font.isBold() ? "bold" : "normal") + ";font-size:" + font.getSize() + "pt");
		sb.append("\">");
		sb.append(text);
		sb.append("</body></html>");
		
		final JEditorPane ep = new JEditorPane("text/html", sb.toString());
		ep.setEditable(false);
		ep.setBackground(label.getBackground());
		ep.addHyperlinkListener(new HyperlinkListener()
		{
			@Override
			public void hyperlinkUpdate(final HyperlinkEvent e)
			{
				if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED) && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE))
				{
					try
					{
						Desktop.getDesktop().browse(e.getURL().toURI());
					}
					catch (final IOException | URISyntaxException e1)
					{
						LOGGER.log(Level.WARNING, "Couldn't open url in browser: ", e);
					}
				}
			}
		});
		
		JOptionPane.showMessageDialog(null, ep, title, messageType);
	}
	
	public static void makeCompactGrid(final Container parent, final int rows, final int cols, final int initialX, final int initialY, final int xPad, final int yPad)
	{
		final SpringLayout layout = (SpringLayout) parent.getLayout();
		
		Spring x = Spring.constant(initialX);
		for (int c = 0;c < cols;c++)
		{
			Spring width = Spring.constant(0);
			for (int r = 0;r < rows;r++)
				width = Spring.max(width, getConstraintsForCell(r, c, parent, cols).getWidth());
			for (int r = 0;r < rows;r++)
			{
				final Constraints constraints = getConstraintsForCell(r, c, parent, cols);
				
				constraints.setX(x);
				constraints.setWidth(width);
			}
			
			x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
		}
		
		Spring y = Spring.constant(initialY);
		for (int r = 0;r < rows;r++)
		{
			Spring height = Spring.constant(0);
			for (int c = 0;c < cols;c++)
				height = Spring.max(height, getConstraintsForCell(r, c, parent, cols).getHeight());
			for (int c = 0; c < cols; c++)
			{
				final Constraints constraints = getConstraintsForCell(r, c, parent, cols);
				
				constraints.setY(y);
				constraints.setHeight(height);
			}
			
			y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
		}
		
		final Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH, y);
		pCons.setConstraint(SpringLayout.EAST, x);
	}
	
	private static Constraints getConstraintsForCell(final int row, final int col, final Container parent, final int cols)
	{
		final SpringLayout layout = (SpringLayout) parent.getLayout();
		final Component c = parent.getComponent(row * cols + col);
		
		return layout.getConstraints(c);
	}
}