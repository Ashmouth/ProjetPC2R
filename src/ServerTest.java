
import java.io.IOException;
import java.net.ServerSocket;
import java.io.OutputStream;
import java.net.Socket;

public class ServerTest {
    private static String message = "Hello I'm your server.";
    private static int port;
    private static ServerSocket socket;

    public static void main(String[] args) {
        try {
            
            if(args.length == 1) { 
            	port = Integer.parseInt(args[0]);
            } else {
            	port = 2017;
            }
            socket = new ServerSocket(port);
            System.out.println("TCP server is running on " + port + "...");

            while (true) {
                // Accept new TCP client
                Socket client = socket.accept();
                // Open output stream
                OutputStream output = client.getOutputStream();

                // Write the message and close the connection
                output.write(message.getBytes());
                /*
                BIENVENUE/placement/tirage/scores/phase/temps/
				(S -> C) Validation d’une connexion. Envoi du plateau courant, tirage courant et scores.
				*/
                message = "BIENVENUE/.";
				output.write(message.getBytes());
				char tab[][] = new char[15][15];
				message = "";
				for(int i = 0; i < 15; i++) {
					for(int j = 0; j < 15; j++) {
						tab[i][j] = '0';
						message += tab[i][j] + ",";
					}
				}
				output.write(message.getBytes());
				
				//tirage
				char letters[] = {'A', 'T', 'I', 'R', 'M', 'E', 'P'};
				message = "/.";
				message = "";
				for(int i = 0; i < letters.length; i++) {
					message += letters[i] + ",";
				}
				output.write(message.getBytes());
				
				//scores
				//phase
				//temps
				/*
				REFUS/
				(S -> C) Refus de la connexion (par exemple parce qu’un client avec le meme nom est deja connecte).

				CONNECTE/user/
				(S -> C) Signalement de la connexion de ’user’ aux autres clients.
                 */
				
				
                
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
