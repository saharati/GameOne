package test;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import util.random.Rnd;

/**
 * This class is not a part of the project.
 * Playing around a little bit with JOptionPane and JDialog before usage in WaitingRoom.
 * @author Sahar
 */
public final class OptionTest
{
	public static void main(final String[] args)
	{
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JOptionPane pane = new JOptionPane("Some text", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, null, null);
        final JDialog dialog = pane.createDialog(frame, "Some title");
        final ScheduledThreadPoolExecutor schedule = new ScheduledThreadPoolExecutor(1);
        
        schedule.scheduleAtFixedRate(() -> 
        {
        	pane.setMessage(Rnd.get(1000));
        }, 1000, 1000, TimeUnit.MILLISECONDS);
        
        dialog.setVisible(true);
        dialog.dispose();
        
        System.out.println(pane.getValue());
        
        frame.setVisible(true);
	}
}