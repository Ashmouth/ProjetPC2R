import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class Display extends JFrame{

	private static final long serialVersionUID = 1L;
	
	// Board width/height in pixels
	public static final Color OPEN_CELL_BGCOLOR = Color.YELLOW;
	public static final Color OPEN_CELL_TEXT_YES = new Color(0, 255, 0);  // RGB
	public static final Color OPEN_CELL_TEXT_NO = Color.RED;
	public static final Color CLOSED_CELL_BGCOLOR = new Color(240, 240, 240); // RGB
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
	JButton buttonx = new JButton("XXX");
    JButton buttons = new JButton("Soumission");
    JButton buttond = new JButton("Deconnexion");
	
	public Display(Client client, int size, int nbletter){
		new Display(client, size, size, nbletter);
	}
	
	public Display(Client client, int x, int y, int nbletter){
		this.x = x;
		this.y = y;
		this.client = client;
		this.nbletter = nbletter;
		
		CANVAS_WIDTH  = CELL_SIZE * x;
		CANVAS_HEIGHT = CELL_SIZE * y;
		tfCells = new JTextField[x][y];
		tfGive = new JTextField[nbletter];
	    
		this.setTitle("Scrabble");
		this.setBackground(Color.PINK);
		this.setSize(x, y);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//TODO Deconnect
		this.setLocationRelativeTo(null);
	    this.setContentPane(buttons);
	    this.setContentPane(buttond);
	    buttons.addActionListener(new SubmitListener()); 
	    buttons.setEnabled(false);
	    buttond.addActionListener(new DeconnexionListener());
	    buttond.setEnabled(false);
	    this.setVisible(true);
	}
	
	public void printtab(char tab[][]) {
		
		Container cp = getContentPane();
		cp.setLayout(new GridLayout(x, y));

		for (int row = 0; row < x; row++) {
			for (int col = 0; col < y; col++) {
				tfCells[row][col] = new JTextField();
				cp.add(tfCells[row][col]);
				if (tab[row][col] != ' ') {
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
		cp.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		pack();
	}
	
	public void printLetter(char tab[]) {
		Container cp = getContentPane();
		cp.setLayout(new GridLayout(7, 1));

		for (int i = 0; i < nbletter; i++) {
			tfGive[i] = new JTextField();
				cp.add(tfGive[i]);
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
		cp.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
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
}