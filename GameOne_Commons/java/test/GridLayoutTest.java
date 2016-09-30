package test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * This class is not a part of the project
 * Playing around a little bit with GridLayout.
 * @author Sahar
 */
public final class GridLayoutTest extends JFrame
{
	private static final long serialVersionUID = -2636303971930491916L;
	
	private static final Integer[] GAP_LIST = {0, 10, 15, 20};
    private static final int MAX_GAP = 20;
    
    private final JComboBox<Integer> _horizontalGapComboBox = new JComboBox<>(GAP_LIST);
    private final JComboBox<Integer> _verticalGapComboBox = new JComboBox<>(GAP_LIST);
    private final JButton _applyButton = new JButton("Apply Gaps");
    private final GridLayout _experimentLayout = new GridLayout(0, 2);
    private final GridLayout _controlsLayout = new GridLayout(2, 3);
    
    private GridLayoutTest(final String name)
    {
        super(name);
        
        setResizable(false);
    }
    
    public void addComponentsToPane(final Container pane)
    {
        final JPanel compsToExperiment = new JPanel(_experimentLayout);
        final JPanel controls = new JPanel(_controlsLayout);
        
        // Set up components preferred size.
        final JButton b = new JButton("Just fake button");
        final Dimension buttonSize = b.getPreferredSize();
        compsToExperiment.setPreferredSize(new Dimension((int) (buttonSize.getWidth() * 2.5) + MAX_GAP, (int) (buttonSize.getHeight() * 3.5) + MAX_GAP * 2));
        
        // Add buttons to experiment with Grid Layout.
        compsToExperiment.add(new JButton("Button 1"));
        compsToExperiment.add(new JButton("Button 2"));
        compsToExperiment.add(new JButton("Button 3"));
        compsToExperiment.add(new JButton("Long-Named Button 4"));
        compsToExperiment.add(new JButton("5"));
        
        // Add controls to set up horizontal and vertical gaps.
        controls.add(new Label("Horizontal gap:"));
        controls.add(new Label("Vertical gap:"));
        controls.add(new Label(" "));
        controls.add(_horizontalGapComboBox);
        controls.add(_verticalGapComboBox);
        controls.add(_applyButton);
        
        // Process the Apply Gaps button press.
        _applyButton.addActionListener(a -> 
        {
        	// Get the gap values.
            int horGap = (int) _horizontalGapComboBox.getSelectedItem();
            int verGap = (int) _verticalGapComboBox.getSelectedItem();
            
            // Set up the gap values
            _experimentLayout.setHgap(horGap);
            _experimentLayout.setVgap(verGap);
            
            // Set up the layout of the buttons
            _experimentLayout.layoutContainer(compsToExperiment);
        });
        
        pane.add(compsToExperiment, BorderLayout.NORTH);
        pane.add(new JSeparator(), BorderLayout.CENTER);
        pane.add(controls, BorderLayout.SOUTH);
    }
    
    /**
     * Create the GUI and show it.
     * For thread safety, this method is invoked from the event dispatch thread.
     */
    private static void createAndShowGUI()
    {
        // Create and set up the window.
        final GridLayoutTest frame = new GridLayoutTest("GridLayoutDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addComponentsToPane(frame.getContentPane());
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void main(final String[] args)
    {
        // Use an appropriate Look and Feel.
        try
        {
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        }
        catch (final UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        
        // Turn off metal's use of bold fonts.
        UIManager.put("swing.boldMetal", false);
        
        // Schedule a job for the event dispatch thread:
        // Creating and showing this application's GUI.
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
}