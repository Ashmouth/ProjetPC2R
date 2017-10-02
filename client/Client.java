import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class Client {
    private static int port;
    private static Socket socket;
    private static int height = 15;
    private static int width = 15;
    private static int nbLetter = 7;
    private String name;
    static char[][] tab;
    static char[] letters;
    ArrayBlockingQueue<String> stack;
    Display display;
	Inputh inputh;
    
    static OutputStream output = null;
    
    public Client(String name) {
    	this.name = name;
    	tab = new char[height][width];
    	letters = new char[nbLetter];
    	display = new Display(this, width, height, nbLetter);
    	stack = new ArrayBlockingQueue<String>(64);
    	inputh = new Inputh(socket, stack);
    	inputh.start();
    }
    
    public Client(String name, int h, int w, int nbl, Display disp, Inputh inputh) {
    	this.name = name;
    	this.display = disp;
    	this.inputh = inputh;
    	tab = new char[h][w];
    	letters = new char[nbl];
    	inputh.start();
    }


	public boolean gameloop() {
		boolean end = false;

		String message = "CONNEXION/"+name+"/\n";
		try {
			output.write(message.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Client.gameloop() error IO");
		}
		
		String str = "";
		display.printLetter(letters);
		display.printtab(tab);
		display.printbutton();
		display.initChat();
		display.printOption();
		
		while(!end) {
			try {
				str = stack.take();
			} catch (InterruptedException e) {
				System.out.println("Client.gameloop() error stack");
			}
			String[] strtab = str.split("/");
			System.out.println(">> Debug Gameloop "+str);
			
			switch(strtab[0]) {
			
			case("BIENVENUE") :
				plateau(strtab[1]);
				tirage(strtab[2]);
				display.refresh(tab, letters);
				display.refreshOption(strtab[3], strtab[4], strtab[5]);
				break;

			case("REFUS") :
				display.printmsg(">> Connection impossible \n");
				return false;
				
			case("CONNECTE") :
				display.printmsg(">> Connection de "+strtab[1]+"\n");
				break;
				
			case("SESSION") :
				display.printmsg(">> Debut de la Session \n");
				break;
				
			case("DECONNEXION") :
				display.printmsg(">> Deconnection de "+strtab[1]+"\n");
				break;
				
			case("TOUR") :
				display.printmsg(">> Debut du tour \n");
				plateau(strtab[1]);
				tirage(strtab[2]);
				display.refresh(tab, letters);
				display.refreshPhase("REC");
				display.buttons.setEnabled(true);
				break;

			case("RVALIDE") :
				display.printmsg(">> Recherche valide\n");
				break;
				
			case("RINVALIDE") :
				display.printmsg(">> Reponse non valide car "+strtab[1]+"\n");
				break;
				
			case("RFIN") :
				display.printmsg(">> Fin de la phase de Recherche \n");
				display.refreshPhase("SOU");
				
				break;

			case("RATROUVE") :
				display.printmsg(">> Reponse trouver de "+strtab[1]+"\n");
				break;
				
			case("SVALIDE") :
				display.printmsg(">> Soumission valide\n");
				break;
				
			case("SINVALIDE") :
				display.printmsg(">> Reponse invalide car "+strtab[1]+"\n");
				break;
				
			case("SFIN") :
				display.printmsg(">> Fin de la phase de Soumission \n");
				display.refreshPhase("RES");
				display.buttons.setEnabled(false);
				break;

			case("TROUVE") :
				display.printmsg(">> Reponse trouver de "+strtab[1]+"\n");
				break;
				
			case("BILAN") :
				display.printmsg(">> Meilleur mot "+strtab[1]+"\n");
				display.printmsg(">> Vainqueur "+strtab[2]+"\n");
				display.refreshScores(strtab[3]);
				System.out.println(">> Debug Bilan score"+strtab[3]);
				break;
				
			case("RECEPTION") :
				display.printmsg(strtab[1]);
				break;
				
			case("PRECEPTION") :
				display.printmsg("@ "+strtab[2]+" vous chuchote \n");
				display.printmsg("@> "+strtab[1]);
				break;
				
			case("MEILLEUR") :
				if(strtab[1].charAt(0) == '0') {
					display.printmsg(">> Dommage quelqu'un à trouvé mieux que toi");
				} else {
					display.printmsg(">> Ton mot est le meilleur mot !");
				}
				
			default:
				System.out.println(">> Unknown command "+strtab[0]);
				break;
			}
		}
		return true;
	}
	
	//(C -> S) Annonce d’une solution de placement par un joueur.
	public boolean submitTab(char[][] stab) {
		ArrayList<Character> tmp = new ArrayList<Character>();
		for(int i = 0; i < nbLetter; i++) {
			tmp.add(letters[i]);
		}
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				if (stab[i][j] != tab[i][j]){
					if(tmp.contains(stab[i][j])){
						Character c = stab[i][j];
						tmp.remove(c);
					} else {
						return false;
					}
				}
			}
		}
		String word = "TROUVE/";
		try {
			for(int i = 0; i < height; i++) {
				for(int j = 0; j < width; j++) {
					word+=stab[i][j];
				}
			}
			word+="/\n";
			output.write(word.getBytes());
		} catch (IOException e) {
			System.out.println("Client.submitTab(char[][] stab) error IO");
		}
		return true;
	}

	//Leave the room with a message
	public void deconnexion() {
		//(C -> S) Deconnexion de ’user’.
		//SORT/user/
		String message = "SORT/"+name+"/\n";
		try {
			output.write(message.getBytes());
		} catch (IOException e) {
			System.out.println("Client.deconnexion() error IO");
		}
	}
	
	public void sendmsg(String msg) {
		String message = "";
		System.out.println(msg);
		if(msg.length() == 0) {
			return;
		}
		if(msg.charAt(0) == '@') {
			int index = msg.indexOf(' ');
			String user = msg.substring(1, index);
			String tmp = msg.substring(index+1, msg.length());
			message = "PENVOI/"+user+"/"+tmp+"/\n";
		} else {
			message = "ENVOI/"+msg+"/\n";
		}
		try {
			output.write(message.getBytes());
		} catch (IOException e) {
			System.out.println("Client.sendmsg(String msg) error IO");
		}
	}
	
	
	public static void main(String[] args) {
    	   
        	if (args.length == 0) {
        		throw new RuntimeException("need at least the adresse");
        	}
        	
        	String adresse = args[0];
            port = 2017;
            if (args.length >= 1) {
            	port = Integer.parseInt(args[1]);
            }
            
            System.out.println(">> Debug main "+adresse+" port "+port);
            System.out.println(">> Open Stream");
            try {
				socket = new Socket(adresse, port);
				output = socket.getOutputStream();
			} catch (UnknownHostException e) {
				System.out.println("Client.main() error Host");
			} catch (IOException e) {
				System.out.println("Client.main() error IO");
			}
            Client clt = null;
            if(args.length >= 2) {
            	clt = new Client(args[2]);
            } else {
                clt = new Client("Joe Black");
            }
            clt.gameloop();
        }
	
	public void tirage(String str) {
		nbLetter = str.length();
		for(int i = 0; i < nbLetter; i++) {
			letters[i] = str.charAt(i);
		}
	}
	
	public void plateau(String str){
		int index = 0;
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				tab[i][j] = str.charAt(index);
				index++;
			}
		}
	}
}
