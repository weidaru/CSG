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


public class CSGOpDialog extends JDialog {
	private JPanel inputPanel;
	private JPanel buttonPanel;
	
	private JLabel old1Label;
	private JTextField old1Field;
	private JLabel old2Label;
	private JTextField old2Field;
	private JLabel newLabel;
	private JTextField newField;
	private JButton okButton;
	
	private Point getCenterLocation(int w, int h) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return new Point((screenSize.width - w)/2, (screenSize.height - h)/2);
	}
	
	public CSGOpDialog(JFrame parent) {
		super(parent);
		
		inputPanel = new JPanel(new GridLayout(3, 2));
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		
		old1Label = new JLabel("Object1Name");
		old1Field = new JTextField("");
		old2Label = new JLabel("Object2Name");
		old2Field = new JTextField("");
		newLabel = new JLabel("NewObjectName");
		newField = new JTextField("");
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		inputPanel.add(old1Label);
		inputPanel.add(old1Field);
		inputPanel.add(old2Label);
		inputPanel.add(old2Field);
		inputPanel.add(newLabel);
		inputPanel.add(newField);

		buttonPanel.add(okButton);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(inputPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
		
		pack();
		setModal(true);
		setLocation(getCenterLocation(getWidth(), getHeight()));
		setVisible(true);
	}
	
	public String getFirstObjectName() {
		return old1Field.getText();
	}
	
	public String getSecondObjectName() {
		return old2Field.getText();
	}
	
	public String getNewObjectName() {
		return newField.getText();
	}
}
