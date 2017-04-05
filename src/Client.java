import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private static int port;
    private static Socket socket;
    private static int height = 15;
    private static int width = 15;
    private static int nbLetter = 7;
    private String name;
    final String endstring = "/.";
    static char[][] tab;
    static char[] letters;
    Display display;
	Inputh inputh;
    
    static OutputStream output = null;
    
    public Client(String name) {
    	this.setName(name);
    	tab = new char[height][width];
    	letters = new char[nbLetter];
    	display = new Display(this, width, height, nbLetter);
    	inputh = new Inputh(socket);
    }
    
    public Client(String name, int h, int w, int nbl, Display disp, Inputh inputh) {
    	this.setName(name);
    	this.display = disp;
    	this.inputh = inputh;
    	tab = new char[h][w];
    	letters = new char[nbl];
    }
    
    
	//Search if he can create a word with each line in all direction
	private void recherche() {
		boolean end = false;
		
		String str = "";
		
		while(!end) {

			inputh.take();
			str = inputh.get();
			inputh.release();
			switch(str) {

			//(S -> C) Debut d’un nouveau tour, plateau courant et tirage courant.
			case("TOUR/.") :
				//plateau
				inputh.take();
				str = inputh.get();
				plateau(str);
				
				//tirage
				str = inputh.get();
				tirage(str);
				
				inputh.release();
				
				break;

			//RVALIDE
			case("RVALIDE/.") :
				break;
				
			//RINVALIDE
			case("RINVALIDE/.") :
				inputh.take();
				str = inputh.get();
				inputh.release();
				System.out.println(str);
				break;
				
			case("RFIN/.") :
				end = true;
				break;

			case("RATROUVE/.") :
				inputh.take();
				str = inputh.get();
				inputh.release();
				System.out.println(str);
				break;
				
			case("RECEPTION/.") :
				str = inputh.get();
				display.printmsg(str);
				break;
			case("PRECEPTION/.") :
				str = inputh.get();
				display.printmsg(str);
				str = inputh.get();
				display.printmsg(">> Vous chuchote "+str+"\n");
				break;
			case("DECONNEXION/.") :
				str = inputh.get();
				display.printmsg(">> Deconnection de "+str+"\n");
				break;
			default:
				System.out.println(">> Unknown command "+str);
				break;
			}
		}
	}
	
	//Search if he can create a word with each line in all direction
	private void submit() {
		boolean end = false;
		
		String str = "";
		
		while(!end) {

			inputh.take();
			str = inputh.get();
			inputh.release();
			switch(str) {
			//RVALIDE
			case("SVALIDE/.") :
				break;
				
			//RINVALIDE
			case("SINVALIDE/.") :
				inputh.take();
				str = inputh.get();
				inputh.release();
				System.out.println(str);
				break;
				
			case("SFIN/.") :
				end = true;
				break;

			case("TROUVE/.") :
				inputh.take();
				str = inputh.get();
				inputh.release();
				System.out.println(str);
				break;
				
			case("RECEPTION/.") :
				str = inputh.get();
				display.printmsg(str);
				break;
			case("PRECEPTION/.") :
				str = inputh.get();
				display.printmsg(str);
				str = inputh.get();
				display.printmsg(">> Vous chuchote "+str+"\n");
				break;
			case("DECONNEXION/.") :
				str = inputh.get();
				display.printmsg(">> Deconnection de "+str+"\n");
				break;
			default:
				System.out.println(">> Unknown command "+str);
				break;
			}
		}
	}
	
	public void result() {
		boolean end = false;

		String str = "";

		while(!end) {

			inputh.take();
			str = inputh.get();
			inputh.release();
			switch(str) {
			case("RECEPTION/.") :
				str = inputh.get();
				display.printmsg(str);
				break;
			case("PRECEPTION/.") :
				str = inputh.get();
				display.printmsg(str);
				str = inputh.get();
				display.printmsg(">> Vous chuchote "+str+"\n");
				break;
			case("DECONNEXION/.") :
				str = inputh.get();
				display.printmsg(">> Deconnection de "+str+"\n");
				break;
			case("BILAN/.") :
				str = inputh.get();
				display.printmsg(">> Meilleur mot "+str+"\n");
				str = inputh.get();
				display.printmsg(">> Vainqueur "+str+"\n");
				str = inputh.get();
				display.printmsg(">> Scores "+str+"\n");
				end = true;
				break;
			default:
				System.out.println(">> Unknown command "+str);
				break;
			}
		}
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
						tmp.remove(stab[i][j]);
					} else {
						return false;
					}
				}
			}
		}
		String word = "TROUVE/.";
		try {
			output.write(word.getBytes());
			for(int i = 0; i < height; i++) {
				for(int j = 0; j < width; j++) {
					output.write(stab[i][j]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	

	//Recover the table for the research
	private void startSession() {
		boolean end = false;
		
		//Execution loop
		while(!end) {
			//(S -> C) Debut d’une session.
			recherche();
			submit();
			//VAINQUEUR/bilan/
			//(S -> C) Fin de la session courante, scores finaux de la session.
			result();
		}
	}
	
	public boolean connection() {
		boolean b = false;
//		CONNEXION/user/
//		(C -> S) Nouvelle connexion d’un client nomme ’user’
		String message = "CONNEXION/.";
		try {
			output.write(message.getBytes());
			message = name;
			output.write(message.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String str = "";
		
		while(!b) {

			inputh.take();
			str = inputh.get();
			
			switch(str) {
//			BIENVENUE/placement/tirage/scores/phase/temps/
//			(S -> C) Validation d’une connexion. Envoi du plateau courant, tirage courant et scores.
			case("BIENVENUE/.") :
				str = inputh.get();
				plateau(str);
				str = inputh.get();
				tirage(str);
				str = inputh.get();
				score(str);
				str = inputh.get();
				phase(str);
				str = inputh.get();
				temps(str);
				break;
//			Precision de la phase courante et du temps restant pour cette phase.
//			REFUS/
//			(S -> C) Refus de la connexion (par exemple parce qu’un client avec le meme nom est deja connecte).
			case("REFUS/.") :
				inputh.release();
				return false;
//			CONNECTE/user/
//			(S -> C) Signalement de la connexion de ’user’ aux autres clients.
			case("CONNECTE/.") :
				str = inputh.get();
				display.printmsg("Connection de "+str+"\n");
				break;
			case("SESSION/.") :
				b = true;
				break;
			case("RECEPTION/.") :
				str = inputh.get();
				display.printmsg(str);
				break;
			case("PRECEPTION/.") :
				str = inputh.get();
				display.printmsg(str);
				str = inputh.get();
				display.printmsg(">> Vous chuchote "+str+"\n");
				break;
			default:
				System.out.println(">> Unknown command "+str);
				break;
			}
			
			inputh.release();
		}
		return true;
	}

	//Leave the room with a message
	public void deconnexion() {
		//(C -> S) Deconnexion de ’user’.
		//SORT/user/
		String message = "SORT/.";
		try {
			output.write(message.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		message = Integer.toString(socket.getLocalPort());
		try {
			output.write(message.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
    	   
        try {
        	if (args.length == 0) {
        		throw new RuntimeException("need at least the adresse");
        	}
        	
        	String adresse = args[0];
            port = 2017;
            if (args.length == 2) {
            	port = Integer.parseInt(args[1]);
            }
            socket = new Socket(adresse, port);

            // Open stream
            output = socket.getOutputStream();
            Client clt = null;
            if(args.length == 3) {
            	clt = new Client(args[2]);
            } else {
                clt = new Client("Joe Black");
            }
            clt.connection();
            clt.startSession();
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	public void tirage(String str) {
		String[] strtab = str.split(",");
		for(int i = 0; i < nbLetter; i++) {
			letters[i] = strtab[i].charAt(0);
		}
	}
	
	public void plateau(String str){
		String[] strtab = str.split(",");
		int index = 0;
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				tab[i][j] = strtab[index].charAt(0);
			}
		}
	}
	
	public void score(String str) {

	}
	
	public void phase(String str) {

	}
	
	public void temps(String str) {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Display getDisplay() {
		return display;
	}
}
