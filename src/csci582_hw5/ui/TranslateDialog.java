package csci582_hw5.ui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TranslateDialog extends JDialog {
	private JPanel inputPanel;
	private JPanel buttonPanel;
	
	private JLabel xLabel;
	private JTextField xField;
	private JLabel yLabel;
	private JTextField yField;
	private JLabel zLabel;
	private JTextField zField;
	private JButton okButton;
	
	private Point getCenterLocation(int w, int h) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return new Point((screenSize.width - w)/2, (screenSize.height - h)/2);
	}
	
	public TranslateDialog(JFrame parent) {
		super(parent);
		
		inputPanel = new JPanel(new GridLayout(3, 2));
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		
		xLabel = new JLabel("DeltaX");
		xField = new JTextField("");
		yLabel = new JLabel("DeltaY");
		yField = new JTextField("");
		zLabel = new JLabel("DeltaZ");
		zField = new JTextField("");
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		inputPanel.add(xLabel);
		inputPanel.add(xField);
		inputPanel.add(yLabel);
		inputPanel.add(yField);
		inputPanel.add(zLabel);
		inputPanel.add(zField);
		
		buttonPanel.add(okButton);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(inputPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
		
		pack();
		setModal(true);
		setLocation(getCenterLocation(getWidth(), getHeight()));
		setVisible(true);
	}
	
	public float getDeltaX() throws IllegalArgumentException {
		return Float.parseFloat(xField.getText());
	}
	
	public float getDeltaY() throws IllegalArgumentException {
		return Float.parseFloat(yField.getText());
	}
	
	public float getDeltaZ() throws IllegalArgumentException {
		return Float.parseFloat(zField.getText());
	}
}