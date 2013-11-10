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

public class BlockDialog extends JDialog {
	private JPanel inputPanel;
	private JPanel buttonPanel;
	
	private JLabel nameLabel;
	private JTextField nameField;
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
	
	public BlockDialog(JFrame parent) {
		super(parent);
		
		inputPanel = new JPanel(new GridLayout(4, 2));
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		
		nameLabel = new JLabel("Name");
		nameField = new JTextField("");
		xLabel = new JLabel("Xsize");
		xField = new JTextField("");
		yLabel = new JLabel("Ysize");
		yField = new JTextField("");
		zLabel = new JLabel("Zsize");
		zField = new JTextField("");
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		inputPanel.add(nameLabel);
		inputPanel.add(nameField);
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
	
	public String getBlockName() {
		return nameField.getText();
	}
	
	public float getXSize() throws IllegalArgumentException {
		return Float.parseFloat(xField.getText());
	}
	
	public float getYSize() throws IllegalArgumentException {
		return Float.parseFloat(yField.getText());
	}
	
	public float getZSize() throws IllegalArgumentException {
		return Float.parseFloat(zField.getText());
	}
}