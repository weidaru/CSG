package csci582_hw5.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.vecmath.Point3f;

public class EndpointsDialog extends JDialog {
	private JLabel startLabel;
	private JLabel endLabel;
	private JTextField startField[];
	private JTextField endField[];
	private JButton okButton;
	
	private JPanel inputPanel;
	private JPanel buttonPanel;
	
	private Point getCenterLocation(int w, int h) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return new Point((screenSize.width - w)/2, (screenSize.height - h)/2);
	}
	
	
	public EndpointsDialog(JFrame parent) {
		super(parent);
		
		startLabel = new JLabel("StartPoint");
		endLabel = new JLabel("EndPoint");
		startField = new JTextField[3];
		startField[0] = new JTextField();
		startField[1] = new JTextField();
		startField[2] = new JTextField();
		endField = new JTextField[3];
		endField[0] = new JTextField();
		endField[1] = new JTextField();
		endField[2] = new JTextField();
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		inputPanel = new JPanel(new GridLayout(2, 4));
		inputPanel.add(startLabel);
		inputPanel.add(startField[0]);
		inputPanel.add(startField[1]);
		inputPanel.add(startField[2]);
		inputPanel.add(endLabel);
		inputPanel.add(endField[0]);
		inputPanel.add(endField[1]);
		inputPanel.add(endField[2]);
		
		buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(inputPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
		
		pack();
		setModal(true);
		setLocation(getCenterLocation(getWidth(), getHeight()));
		setVisible(true);
	}
	
	public Point3f getStartPoint() {
		Point3f result = null;
		
		try {
			float x = Float.parseFloat(startField[0].getText());
			float y = Float.parseFloat(startField[1].getText());
			float z = Float.parseFloat(startField[2].getText());	
			result = new Point3f(x, y, z);
		}
		catch(IllegalArgumentException ex) {
			
		}
		
		return result;
	}
	
	public Point3f getEndPoint() {
		Point3f result = null;
		
		try {
			float x = Float.parseFloat(endField[0].getText());
			float y = Float.parseFloat(endField[1].getText());
			float z = Float.parseFloat(endField[2].getText());	
			result = new Point3f(x, y, z);
		}
		catch(IllegalArgumentException ex) {
			
		}
		
		return result;
	}
}
