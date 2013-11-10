package csci582_hw5.ui;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class TextDialog extends JDialog {
	private JLabel label;
	private JTextField textField;
	private JButton okButton;
	private FlowLayout layout;
	
	private Point getCenterLocation(int w, int h) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return new Point((screenSize.width - w)/2, (screenSize.height - h)/2);
	}
	
	public TextDialog(JFrame parent, String lableString) {
		super(parent);
		
		label = new JLabel(lableString);
		textField = new JTextField("");
		textField.setPreferredSize(new Dimension(50, 20));
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		layout = new FlowLayout();
		setLayout(layout);
		add(label);
		add(textField);
		add(okButton);
		
		pack();
		setModal(true);
		setLocation(getCenterLocation(getWidth(), getHeight()));
		setVisible(true);
	}
	
	public String getText() {
		return textField.getText();
	}
}

	