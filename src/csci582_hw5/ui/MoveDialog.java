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


public class MoveDialog extends JDialog {
	private JPanel inputPanel;
	private JPanel buttonPanel;
	
	private JLabel nameLabel;
	private JTextField nameField;
	private JLabel transformLabel;
	private JTextField transformField;
	private JLabel newObjLabel;
	private JTextField newObjField;
	private JButton okButton;
	
	private Point getCenterLocation(int w, int h) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return new Point((screenSize.width - w)/2, (screenSize.height - h)/2);
	}
	
	public MoveDialog(JFrame parent) {
		super(parent);
		
		inputPanel = new JPanel(new GridLayout(3, 2));
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		
		nameLabel = new JLabel("ObjectName");
		nameField = new JTextField("");
		transformLabel = new JLabel("TransformName");
		transformField = new JTextField("");
		newObjLabel = new JLabel("NewObjectName");
		newObjField = new JTextField("");
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		inputPanel.add(nameLabel);
		inputPanel.add(nameField);
		inputPanel.add(transformLabel);
		inputPanel.add(transformField);
		inputPanel.add(newObjLabel);
		inputPanel.add(newObjField);

		buttonPanel.add(okButton);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(inputPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
		
		pack();
		setModal(true);
		setLocation(getCenterLocation(getWidth(), getHeight()));
		setVisible(true);
	}
	
	public String getObjName() {
		return nameField.getText();
	}
	
	public String getTransformName() {
		return transformField.getText();
	}
	
	public String getNewObjectName() {
		return newObjField.getText();
	}
}
