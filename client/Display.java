import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

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
	private JTextField phaseBox;
	private JTextField timesBox;
	private JButton sendMessage;
	private JTextArea chatBox;
	private JTextArea scoreBox;
	JPanel panelbutton;
	JPanel panelLetter;
	JPanel panelOption;
	JPanel panelTab;
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
		
		CANVAS_WIDTH  = 960;
		CANVAS_HEIGHT = 480;
		tfCells = new JTextField[x][y];
		tfGive = new JTextField[nbletter];
	    
		this.setTitle("Scrabble");
		this.setBackground(Color.PINK);
		this.setSize(x, y);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
	    this.setVisible(true);
	    
	    buttons = new JButton("Soumission");
	    buttond = new JButton("Deconnexion");
	    buttons.addActionListener(new SubmitListener()); 
	    buttond.addActionListener(new DeconnexionListener());
	}
	
	public void initChat() {
		JPanel panelChat = new JPanel();
		panelChat.setBorder(new TitledBorder(new EtchedBorder(), "Chat"));
		panelChat.setBackground(new Color(249, 66, 158));
		panelChat.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		chatBox = new JTextArea(25, 20);
        chatBox.setEditable(false);
        chatBox.setFont(new Font("Serif", Font.PLAIN, 15));
        chatBox.setLineWrap(true);
        
        JScrollPane scroll = new JScrollPane(chatBox);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        messageBox = new JTextField(20);

        sendMessage = new JButton("Envoyer");
        sendMessage.addActionListener(new sendMessageButtonListener());
        
        c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		
        panelChat.add(scroll);
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
	
	public void printOption() {
		panelOption = new JPanel();
		panelOption.setBorder(new TitledBorder(new EtchedBorder(), "ScoreBoard"));
		panelOption.setBackground(new Color(249, 66, 158));
		panelOption.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		scoreBox = new JTextArea(20, 20);
		scoreBox.setEditable(false);
		scoreBox.setFont(new Font("Serif", Font.PLAIN, 15));
		scoreBox.setLineWrap(true);
		
		phaseBox = new JTextField(20);
		phaseBox.setEditable(false);
		phaseBox.setFont(new Font("Serif", Font.PLAIN, 15));
		
		timesBox = new JTextField(20);
		timesBox.setEditable(false);
		timesBox.setFont(new Font("Serif", Font.PLAIN, 15));
        
        c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		
		panelOption.add(scoreBox, c);
        c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		panelOption.add(phaseBox, c);
        c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		panelOption.add(timesBox, c);
        
        panelTab.setPreferredSize(new Dimension(10, 20));
		this.add(panelOption, BorderLayout.WEST);
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
		panelbutton.add(buttond);
		panelbutton.add(buttons);
		this.add(panelbutton, BorderLayout.SOUTH);
		pack();
	}

	public char[][] recover() {
		char tab[][] = new char[x][y];
		String tmp;
		for (int row = 0; row < x; row++) {
			for (int col = 0; col < y; col++) {
				tmp = tfCells[row][col].getText();
				if(tmp.equals("")) {
					tab[row][col] = '0';
				} else {
					tab[row][col] = tmp.charAt(0);
				}
			}
		}
		return tab;
	}

	public void clean() {
		this.remove(panelLetter);
		this.remove(panelTab);
	}
	
	public void refresh(char tab[][], char letters[]) {
		System.out.println(">> Debug Cleanning");
		this.clean();
		System.out.println(">> Debug refresh Letters");
		this.printLetter(letters);
		System.out.println(">> Debug refresh Tab");
		this.printtab(tab);
		System.out.println(">> Debug Update");
	}
	
	public void refreshOption(String scores, String phase, String temps) {
		refreshScores(scores);
		refreshPhase(phase);
		String msg = "Temps : "+temps+"\n";
		timesBox.setText(msg);
	}
	
	public void refreshPhase(String phase) {
		String msg = "Phase de Jeu : "+phase+"\n";
		phaseBox.setText(msg);
	}
	
	public void refreshScores(String scores) {
		String tmp[] = scores.split("\\*");
		String msg = "Scores : \n";
		for(int i = 1; i < Integer.parseInt(tmp[0])+1; i++) {
			msg += (tmp[i] + " : " + tmp[i+1] + "\n");
		}
		scoreBox.setText(msg);
	}
	
	public void printmsg(String str) {
		chatBox.append(str+"\n");
	}
	
	public void setClient(Client client) {
		this.client = client;
	}

	class SubmitListener implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			buttons.setEnabled(false);
			client.submitTab(recover());
			buttons.setEnabled(true);
		}
	}


	class DeconnexionListener implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			client.deconnexion();
			buttond.setEnabled(false);
		}
	}     
	
	class sendMessageButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			String msg = messageBox.getText();
			client.sendmsg(msg);
			messageBox.setText("");
		}
	}
}