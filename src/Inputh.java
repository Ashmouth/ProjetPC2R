import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Inputh implements Runnable{
	private ArrayList<String> pile;
	private InputStream input = null;
	private InputStreamReader isr;
	private BufferedReader br;
	private Socket socket;
	private Lock lock;
	
	public Inputh(Socket socket) {
		this.setSocket(socket);
		isr = new InputStreamReader(input);
		br = new BufferedReader(isr);
		lock = new ReentrantLock();
		pile = new ArrayList<String>();
		try {
			input = socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		String str = "";
		while(true) {
			try {
				str = br.readLine();
			
			take();
			add(str);
			switch(str) {
//			BIENVENUE/placement/tirage/scores/phase/temps/
//			(S -> C) Validation d’une connexion. Envoi du plateau courant, tirage courant et scores.
			case("BIENVENUE/."):
				str = br.readLine();
				add(str);
				str = br.readLine();
				add(str);
				str = br.readLine();
				add(str);
				str = br.readLine();
				add(str);
				str = br.readLine();
				add(str);
				break;
//			Precision de la phase courante et du temps restant pour cette phase.
//			REFUS/
//			(S -> C) Refus de la connexion (par exemple parce qu’un client avec le meme nom est deja connecte).
			case("REFUS/."):
				break;
//			CONNECTE/user/
//			(S -> C) Signalement de la connexion de ’user’ aux autres clients.
			case("CONNECTE/."):
				str = br.readLine();
				add(str);
				break;
//			Deconnexion
//			DECONNEXION/user/
//			(S -> C) Signalement de la deconnexion de ’user’ aux autres clients.
			case("DECONNEXION/."):
				str = br.readLine();
				add(str);
				break;
//			Debut d’une session
//			SESSION/
//			(S -> C) Debut d’une nouvelle session.
			case("SESSION/."):
				break;
//			VAINQUEUR/bilan/
//			(S -> C) Fin de la session courante, scores finaux de la session.
			case("VAINQUEUR/."):
				str = br.readLine();
				add(str);
				break;
//			Phase de recherche
//			TOUR/plateau/tirage/
//			(S -> C) Debut d’un nouveau tour, plateau courant et tirage courant.
			case("TOUR/."):
				str = br.readLine();
				add(str);
				str = br.readLine();
				add(str);
				break;
//			RVALIDE/
//			(S -> C) Validation de la solution par le serveur, fin de la phase de recherche.
			case("RVALIDE/."):
				break;
//			RINVALIDE/raison/
//			(S -> C) Invalidation de la solution par le serveur, raison explicite.
			case("RINVALIDE/."):
				str = br.readLine();
				add(str);
				break;
//			RATROUVE/user/
//			(S -> C) Signalement d’un mot trouve par ’user’. Fin de la phase de rech. et debut de la  phase de soum.
			case("RATROUVE/."):
				str = br.readLine();
				add(str);
				break;
//			RFIN/
//			(S -> C) Expiration du delai imparti a la reflexion. Fin de la phase de recherche et nouveau tour.
			case("RFIN/."):
				break;
//			Phase de soumission
//			SVALIDE/
//			(S -> C) Validation de la solution par le serveur.
			case("SVALIDE/."):
				break;
//			SINVALIDE/raison/
//			(S -> C) Invalidation de la solution par le serveur, ’raison’ explicite.
			case("SINVALIDE/."):
				str = br.readLine();
				add(str);
				break;
//			SFIN/
//			(S -> C) Expiration du delai imparti a la soumission, fin de la phase de soumission et phase de resultat.
			case("SFIN/."):
				break;
//			Phase de resultat
//			BILAN/mot/vainqueur/scores/
//			(S -> C) Bilan du tour, nom et mot du gagnant, scores de tous les joueurs.
			case("BILAN/."):
				str = br.readLine();
				add(str);
				str = br.readLine();
				add(str);
				str = br.readLine();
				add(str);
				break;
//			RECEPTION/message/
//			(S -> C) Reception d’un message public.
			case("RECEPTION/."):
				str = br.readLine();
				add(str);
				break;
//			PRECEPTION/message/user/
//			(S -> C) Reception d’un message prive de l’utilisateur "user".
			case("PRECEPTION/."):
				str = br.readLine();
				add(str);
				str = br.readLine();
				add(str);
				break;
			}
			} catch (IOException e) {
				e.printStackTrace();
			}
			release();
		}
	}

	public String get() {
		return pile.remove(0);
	}
	
	public void take() {
		lock.lock();
	}
	
	public void release() {
		lock.unlock();
	}
	
	public void add(String str) {
		pile.add(str);
	}
	
	public void close() {
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
}
