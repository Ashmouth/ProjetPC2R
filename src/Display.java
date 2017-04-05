import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Display extends JFrame {

	private static final long serialVersionUID = 1L;
	
	// Board width/height in pixels
	public static final Color OPEN_CELL_BGCOLOR = Color.PINK;
	public static final Color OPEN_CELL_TEXT_YES = new Color(253, 108, 158);  // RGB
	public static final Color OPEN_CELL_TEXT_NO = Color.RED;
	public static final Color CLOSED_CELL_BGCOLOR = new Color(255, 105, 180); // RGB
	public static final Color CLOSED_CELL_TEXT = Color.BLACK;
	public static final Font FONT_NUMBERS = new Font("Monospaced", Font.BOLD, 20);
	// Name-constants for UI control (sizes, colors and fonts)
	public static final int CELL_SIZE = 60;   // Cell width/height in pixels
	public static int CANVAS_WIDTH;
	public static int CANVAS_HEIGHT;
	private int x, y, nbletter;
	Client client;
	private JTextField[][] tfCells;
	private JTextField[] tfGive;
	private JTextField messageBox;
	private JButton sendMessage;
	private JTextArea chatBox;
	JPanel panelbutton;
	JPanel panelLetter;
	JPanel panelTab;
	JButton buttonx;
    JButton buttons;
    JButton buttond;
	
	
	public Display(Client client, int size, int nbletter) {
		new Display(client, size, size, nbletter);
	}
	
	public Display(Client client, int x, int y, int nbletter) {
		this.x = x;
		this.y = y;
		this.client = client;
		this.nbletter = nbletter;
		
		//CANVAS_WIDTH  = CELL_SIZE * x;
		//CANVAS_HEIGHT = CELL_SIZE * y;
		CANVAS_WIDTH  = 640;
		CANVAS_HEIGHT = 480;
		tfCells = new JTextField[x][y];
		tfGive = new JTextField[nbletter];
	    
		this.setTitle("Scrabble");
		this.setBackground(Color.PINK);
		this.setSize(x, y);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//TODO Deconnect
		this.setLocationRelativeTo(null);
	    this.setVisible(true);
	    
	    buttonx = new JButton("XXX");
	    buttons = new JButton("Soumission");
	    buttond = new JButton("Deconnexion");
	    buttons.addActionListener(new SubmitListener()); 
	    buttons.setEnabled(false);
	    buttond.addActionListener(new DeconnexionListener());
	    buttond.setEnabled(false);
	}
	
	public void initChat() {
		JPanel panelChat = new JPanel();
		panelChat.setBackground(new Color(249, 66, 158));
		panelChat.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		chatBox = new JTextArea(20, 20);
        chatBox.setEditable(false);
        chatBox.setFont(new Font("Serif", Font.PLAIN, 15));
        chatBox.setLineWrap(true);
        
        messageBox = new JTextField(20);
        //messageBox.requestFocusInWindow();

        sendMessage = new JButton("Send Message");
        sendMessage.addActionListener(new sendMessageButtonListener());
        
        c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		
        panelChat.add(chatBox, c);
        c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
        panelChat.add(messageBox, c);
        c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
        panelChat.add(sendMessage, c);
        
        panelTab.setPreferredSize(new Dimension(10, 20));
        this.add(panelChat, BorderLayout.EAST);
        pack();
	}
	
	public void printtab(char tab[][]) {
		
		panelTab = new JPanel();
		panelTab.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 4));
		panelTab.setLayout(new GridLayout(x, y));

		for (int row = 0; row < x; row++) {
			for (int col = 0; col < y; col++) {
				tfCells[row][col] = new JTextField();
				panelTab.add(tfCells[row][col]);
				if (tab[row][col] == '0') {
					tfCells[row][col].setText("");
					tfCells[row][col].setEditable(true);
					tfCells[row][col].setBackground(OPEN_CELL_BGCOLOR);
				} else {
					tfCells[row][col].setText(tab[row][col] + "");
					tfCells[row][col].setEditable(false);
					tfCells[row][col].setBackground(CLOSED_CELL_BGCOLOR);
					tfCells[row][col].setForeground(CLOSED_CELL_TEXT);
				}
				// Beautify all the cells
				tfCells[row][col].setHorizontalAlignment(JTextField.CENTER);
				tfCells[row][col].setFont(FONT_NUMBERS);
			}
		}
		// Set the size of the content-pane and pack all the components
		//  under this container.
		panelTab.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		
		this.add(panelTab, BorderLayout.CENTER);
		pack();
	}
	
	public void printLetter(char tab[]) {
		
		panelLetter = new JPanel();
		panelLetter.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 4));
		panelLetter.setLayout(new GridLayout(1, nbletter));

		for (int i = 0; i < nbletter; i++) {
			tfGive[i] = new JTextField();
			panelLetter.add(tfGive[i]);
			tfGive[i].setText(tab[i] + "");
			tfGive[i].setEditable(false);
			tfGive[i].setBackground(CLOSED_CELL_BGCOLOR);
			tfGive[i].setForeground(CLOSED_CELL_TEXT);
			// Beautify all the cells
			tfGive[i].setHorizontalAlignment(JTextField.CENTER);
			tfGive[i].setFont(FONT_NUMBERS);
		}
		// Set the size of the content-pane and pack all the components
		//  under this container.
		panelLetter.setPreferredSize(new Dimension(nbletter*CELL_SIZE, CELL_SIZE));
		
		this.add(panelLetter, BorderLayout.NORTH);
		pack();
	}
	
	public void printbutton() {
		panelbutton = new JPanel();
		panelbutton.setLayout(new GridLayout(1, nbletter));
		panelbutton.add(buttonx);
		panelbutton.add(buttons);
		panelbutton.add(buttond);
		this.add(panelbutton, BorderLayout.SOUTH);
		pack();
	}

	public char[][] recover() {
		char tab[][] = new char[x][y];
		String tmp;
		for (int row = 0; row < x; row++) {
			for (int col = 0; col < y; col++) {
				tmp = tfCells[row][col].getText();
				tab[row][col] = tmp.charAt(0);
			}
		}
		return tab;
	}

	public void clean() {
		this.remove(panelLetter);
		this.remove(panelTab);
	}
	
	public void refresh(char tab[][], char letters[]) {
		this.clean();
		this.printLetter(letters);
		this.printtab(tab);
		SwingUtilities.updateComponentTreeUI(this);
	}
	
	public void printmsg(String str) {
		chatBox.append(str);
	}
	
	public void setClient(Client client) {
		this.client = client;
	}

	class SubmitListener implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			buttons.setEnabled(true);
			client.submitTab(recover());
			//client.submitAnswer();
			buttons.setEnabled(false);
		}
	}


	class DeconnexionListener implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			buttons.setEnabled(true);
			client.deconnexion();
			buttond.setEnabled(false);
		}
	}     
	
	class sendMessageButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
//			buttons.setEnabled(true);
			String msg = messageBox.getText();
			String user = client.getName();
			//String user = "Marco";
			String str = user+" - "+msg+"\n";
			chatBox.append(str);
			messageBox.setText("");
//			buttond.setEnabled(false);
		}
	}
	
	//For testing Display
	public static void main(String[] args) {
		Display disp = new Display(null, 7, 7, 7);
		char tab[][] = {{'0', '0', 'T', '0', '0', '0', '0'},
				{'0', '0', 'I', '0', '0', '0', '0'},
				{'0', '0', 'M', '0', '0', '0', '0'},
				{'0', '0', 'E', '0', '0', '0', '0'},
				{'0', '0', '0', '0', '0', '0', '0'},
				{'0', '0', '0', '0', '0', '0', '0'},
				{'0', '0', '0', '0', '0', '0', '0'},
				};
		char letters[] = {'A', 'T', 'I', 'R', 'N', 'E', 'P'};
		disp.printLetter(letters);
		disp.printtab(tab);
		disp.printbutton();
		disp.initChat();
		
		char tabCorrect[][] = {{'0', '0', 'T', '0', '0', '0', '0'},
				{'0', '0', 'I', '0', '0', '0', '0'},
				{'0', '0', 'M', '0', '0', '0', '0'},
				{'0', '0', 'E', '0', '0', '0', '0'},
				{'0', '0', 'S', '0', '0', '0', '0'},
				{'0', '0', '0', '0', '0', '0', '0'},
				{'0', '0', '0', '0', '0', '0', '0'},
				};
		char letterCorrect[] = {'A', 'T', 'I', 'R', 'M', 'E', 'P'};
		disp.refresh(tabCorrect, letterCorrect);
	}
}