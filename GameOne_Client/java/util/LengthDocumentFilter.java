package util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Sets a max length for a certain JTextField.
 * @author Sahar
 */
public class LengthDocumentFilter extends DocumentFilter
{
	private final int _maxLength;
	
	public LengthDocumentFilter(final int maxLength)
	{
		_maxLength = maxLength;
	}
	
	@Override
	public void replace(final FilterBypass fb, final int offset, final int length, final String text, final AttributeSet attrs) throws BadLocationException
	{
		final int currentLength = fb.getDocument().getLength();
		final int overLimit = (currentLength + text.length()) - _maxLength - length;
		if (overLimit > 0)
			super.replace(fb, offset, length, text.substring(0, text.length() - overLimit), attrs); 
		else
			super.replace(fb, offset, length, text, attrs);
    }
}